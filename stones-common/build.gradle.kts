plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

dependencies {
//LESSON adding dependencies here has no effect
}

kotlin {
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
            dependencies {
                Libs.KtMath.ktMath
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
            }

        }
    }
    jvm()
    js {
        browser()
    }
}
