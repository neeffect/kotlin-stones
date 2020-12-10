plugins {
    kotlin("multiplatform")
}



kotlin {
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
            dependencies {
                Libs.KtMath.ktMath
            }

        }
    }
    jvm()
    js {
        browser()
    }
}
