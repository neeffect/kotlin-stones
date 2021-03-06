import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType
import org.jetbrains.kotlin.utils.addToStdlib.min

group = "pl.setblack"
version = "0.0.1"

plugins {
    id("org.jetbrains.kotlin.js")
    id("org.jetbrains.kotlin.plugin.serialization")
}

repositories {
    jcenter()
    mavenLocal()
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-dev") }
    maven { setUrl("http://dl.bintray.com/kotlin/kotlin-js-wrappers") }
    maven("https://dl.bintray.com/cfraser/muirwik")
}

dependencies {
    val kotlinVersion = Libs.kotlin_version
    val kotlinJsVersion = "pre.146-kotlin-$kotlinVersion"
    val kotlinReactVersion = "17.0.1-$kotlinJsVersion"

    implementation(project(":stones-common"))
    implementation(kotlin("stdlib-js", kotlinVersion))

    implementation("org.jetbrains", "kotlin-react", kotlinReactVersion)
    implementation("org.jetbrains", "kotlin-react-dom", kotlinReactVersion)
    implementation("org.jetbrains", "kotlin-styled", "5.2.1-$kotlinJsVersion")
    implementation(npm("react-hot-loader", "^4.12.20"))

    implementation(devNpm("webpack-bundle-analyzer", "^3.8.0"))

    implementation("com.ccfraser.muirwik:muirwik-components:0.6.0")

    implementation("io.github.gciatto:kt-math:0.1.3")

    implementation(npm("@material-ui/core", "^4.9.14"))
    implementation(npm("@material-ui/icons", "^4.9.1"))
    implementation(npm("material-ui-color-picker", "^3.5.1"))

    implementation(Libs.Ktor.clientJs)
    implementation(Libs.Ktor.clientJsonJs)
    implementation(Libs.Kotlin.serializationJson)
    compileOnly(Libs.Ktor.clientSerialization)
    implementation(Libs.Ktor.clientSerializationJs)

    implementation(Libs.Kotlin.coroutinesCore)

}

kotlin {
    println("defaultJsCompileType is $defaultJsCompilerType")
    defaultJsCompilerType = KotlinJsCompilerType.LEGACY

    js {
        browser {
            useCommonJs()

            webpackTask {
                cssSupport.enabled = true
            }

            runTask {
                cssSupport.enabled = true
                sourceMaps = true
            }

            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
        binaries.executable()
    }
}


tasks.register("webpackStatsFile") {
    description = "Creates a webpack stats file for webpack-bundle-analyzer to use, and shows this in a browser"

    /**
     * Windows and Linux/Mac has different executables for webpack, so this just centralises getting the name
     * of the executable depending on platform. We are just making an assumption that if the path separator is '\' then
     * we are on a windows OS, otherwise a Unix based OS (based on https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html).
     */
    fun appendCmdExtIfRequired(executableName: String) = executableName + if (File.separatorChar == '\\') ".cmd" else ""

    doLast {
        val createdWebPackFile =
            rootProject.buildDir.resolve("js/packages/${rootProject.name}-${project.name}/webpack.config.js")
        if (!createdWebPackFile.exists()) {
            throw IllegalStateException("Could not create stats file as can't find webpack config file.")
        } else {
            val outputFile = buildDir.resolve("reports/webpack/webpack-stats.json")
            val workingDirFile = rootProject.buildDir.resolve("js/node_modules/.bin")

            val execResult = exec {
                workingDir = workingDirFile
                standardOutput = outputFile.outputStream()
                commandLine = listOf(
                    appendCmdExtIfRequired("./webpack-cli"),
                    "--config",
                    createdWebPackFile.toString(),
                    "--profile",
                    "--json"
                )
            }
            if (execResult.exitValue == 0) {
                // The output seems to put a %complete before outputting the json, so we stip that from the file
                val filteredLines =
                    outputFile.readLines().filter { !(it.substring(0, min(it.length, 5)).contains("%")) }
                outputFile.writeText(filteredLines.joinToString("\n"))
                exec {
                    workingDir = workingDirFile
                    commandLine = listOf(
                        appendCmdExtIfRequired("./webpack-bundle-analyzer"),
                        outputFile.toString(),
                        "-m",
                        "static",
                        "-r",
                        outputFile.resolveSibling("webpack-stats-report.html").toString()
                    )
                }
            } else {
                println("webpack-cli stats creation exit value was not zero: ${execResult.exitValue}")
            }
        }
    }
}
