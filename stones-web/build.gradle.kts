import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType
import org.jetbrains.kotlin.utils.addToStdlib.min

plugins {
    val kotlinVersion = "1.4.0"
    id("org.jetbrains.kotlin.js") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenLocal()
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-dev") }
    maven { setUrl("http://dl.bintray.com/kotlin/kotlin-js-wrappers") }
    maven("https://dl.bintray.com/cfraser/muirwik")
}

dependencies {
    val kotlinJsVersion = "1.4.0"
    val kotlinJsLibVersion = "pre.112-kotlin-${kotlinJsVersion}"
    implementation(kotlin("stdlib-js", kotlinJsVersion))

    //React, React DOM + Wrappers (chapter 3)
    implementation("org.jetbrains:kotlin-react:16.13.1-${kotlinJsLibVersion}")
    implementation("org.jetbrains:kotlin-react-dom:16.13.1-${kotlinJsLibVersion}")
    //implementation(npm("react", "16.13.1"))
    //implementation(npm("react-dom", "16.13.1"))

    implementation("org.jetbrains:kotlin-styled:1.0.0-${kotlinJsLibVersion}")
    //implementation(npm("styled-components","5.2.0"))
    //implementation(npm("inline-style-prefixer","6.0.0"))


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.8")
    implementation("io.github.gciatto:kt-math:0.1.3")

    implementation("com.ccfraser.muirwik:muirwik-components:0.5.1")
    //implementation("org.jetbrains:kotlin-react:VERSION")
    //implementation("org.jetbrains:kotlin-react-dom:VERSION")

    implementation("org.jetbrains:kotlin-styled:1.0.0-${kotlinJsLibVersion}")
    implementation("org.jetbrains:kotlin-css-js:1.0.0-${kotlinJsLibVersion}")
    implementation("org.jetbrains:kotlin-css:1.0.0-${kotlinJsLibVersion}")
    implementation(npm("@material-ui/core", "^4.9.14"))

    //read about
    //implementation(npm("react-hot-loader", "^4.12.20"))

    implementation(npm("react-hot-loader", "^4.12.20"))


    implementation(devNpm("webpack-bundle-analyzer", "^3.8.0"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_14
}
kotlin {
    println("defaultJsCompileType is $defaultJsCompilerType")
    defaultJsCompilerType = KotlinJsCompilerType.LEGACY  // The default
//        defaultJsCompilerType = KotlinJsCompilerType.IR
//        defaultJsCompilerType = KotlinJsCompilerType.BOTH

    js {
        browser {
            useCommonJs()

            webpackTask {
                cssSupport.enabled = true
            }

            runTask {
                cssSupport.enabled = true
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

