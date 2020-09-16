plugins {
    kotlin("multiplatform")
}



kotlin {
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
            dependencies {
                implementation("io.github.gciatto:kt-math:0.1.3")
            }

        }
    }
    jvm()
    js {
        browser()
    }
}
