group "pl.setblack"
version "1.1.0-SNAPSHOT"

plugins {
    val kotlinVersion = Libs.kotlin_version
    id("org.jetbrains.kotlin.jvm") version kotlinVersion apply false
    id("org.jetbrains.kotlin.js") version kotlinVersion apply false
    id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion apply false
    id("io.gitlab.arturbosch.detekt").version("1.16.0")
}

allprojects {
    apply (plugin = "io.gitlab.arturbosch.detekt")

    repositories {
        jcenter()
        mavenLocal()
        mavenCentral()
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-dev") }
        maven { setUrl("http://dl.bintray.com/kotlin/kotlin-js-wrappers") }
        maven("https://dl.bintray.com/cfraser/muirwik")
    }

    detekt {
        baseline = file("$projectDir/config/baseline.xml")
        reports {
            html.enabled = true // observe findings in your browser with structure and code snippets
            xml.enabled = true // check(style like format mainly for integrations like Jenkins)
            txt.enabled =
                true // similar to the console output, contains issue signature to manually edit baseline files
        }
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        this.jvmTarget = "1.8"
    }
}

