
group "pl.setblack"
version "1.0-SNAPSHOT"
plugins {
    val kotlinVersion = "1.4.0"
    id("org.jetbrains.kotlin.js") version kotlinVersion apply false
    id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion apply false
}
allprojects {
    repositories {
        jcenter()
        mavenLocal()
        mavenCentral()
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-dev") }
        maven { setUrl("http://dl.bintray.com/kotlin/kotlin-js-wrappers") }
        maven("https://dl.bintray.com/cfraser/muirwik")
    }
}
