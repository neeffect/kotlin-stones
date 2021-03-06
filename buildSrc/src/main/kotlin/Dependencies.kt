import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.accessors.runtime.addDependencyTo
import org.gradle.kotlin.dsl.exclude

object Libs {
    const val kotlin_version = "1.4.30"
//    const val liquibase_version="3.6.1"
    const val nee_version = "0.6.8"

    object H2 {
        private const val version = "1.4.200"
        const val  database = "com.h2database:h2:$version"
    }

    object JOOQ {
        const val version = "3.13.5"
        const val  jooq = "org.jooq:jooq:$version"
    }

    object Nee {
        private const val version = nee_version
        const val ctxWebKtor = "pl.setblack:nee-ctx-web-ktor:$version"
        const val jdbc = "pl.setblack:nee-jdbc:$version"
        const val securityJdbc = "pl.setblack:nee-security-jdbc:$version"
        const val  jdbcTest = "pl.setblack:nee-security-jdbc-test:$version"
        const val  ctxWebTest = "pl.setblack:nee-ctx-web-test:$version"
    }

    object Vavr {
        private const val version = "0.10.2"
        const val kotlin = "io.vavr:vavr-kotlin:$version"
        const val jackson  ="io.vavr:vavr-jackson:$version"
    }

    object Hoplite {
        private const val version = "1.4.0"
        const val core = "com.sksamuel.hoplite:hoplite-core:$version"
        const val yaml = "com.sksamuel.hoplite:hoplite-yaml:$version"
    }

    object Jackson {
        val moduleKotlin = impl(Versions.moduleKotlin) {
            exclude("org.jetbrains.kotlin")
        }
        object Versions {
            private const val version = "2.12.1"
            const val moduleKotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:$version"
        }
    }

    object Ktor {
        private const val  version = "1.5.1"
        const val clientJs = "io.ktor:ktor-client-js:$version"
        const val clientJvm = "io.ktor:ktor-client-core-jvm:$version"
        const val clientApache = "io.ktor:ktor-client-apache:$version"
        const val clientCIO = "io.ktor:ktor-client-cio:$version"
        const val clientJsonJs = "io.ktor:ktor-client-json-js:$version"
        const val clientSerialization = "io.ktor:ktor-client-serialization:$version"
        const val clientSerializationJs = "io.ktor:ktor-client-serialization-js:$version"
        const val serverTestHost = "io.ktor:ktor-server-test-host:$version"
        const val serverNetty = "io.ktor:ktor-server-netty:$version"
        const val jackson = "io.ktor:ktor-jackson:$version"
    }

    object KtMath {
        val ktMath = impl( KtMath.Versions.ktMath) {
            exclude("org.jetbrains.kotlin")
        }
        object Versions {
            const val version = "0.1.3"
            val ktMath = "io.github.gciatto:kt-math:$version"
        }
    }

    object Kotest {
        private const val version = "4.4.1"
        const val runnerJunit5Jvm ="io.kotest:kotest-runner-junit5-jvm:$version"
        const val assertionsCoreJvm = "io.kotest:kotest-assertions-core-jvm:$version"
    }

    object Kotlin {
        private const val serializationVersion = "1.1.0-RC"
        private const val coroutinesVersion  = "1.4.2"
        const val kotlinStdLib =  "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
        //every time I use  it I waste a lot of time
        //just a note that more time was wasted
        const val serializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$kotlin_version"
        const val serializationCore = "org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion"
        const val coroutinesCore  = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
    }


    object Liquibase {
        private const val version = "4.3.1"
        const val core = "org.liquibase:liquibase-core:$version"
    }
}


fun implementation(lib: String): (DependencyHandler)->Dependency? = {
    it.add("implementation", lib)
}


fun impl(lib: String, dependencyConfiguration: Action<ExternalModuleDependency> = EmptyAction) : DependencyHandler.() -> Dependency? = {
    addDependencyTo(
        this, "implementation", lib, dependencyConfiguration
    ) as ExternalModuleDependency
}

internal object EmptyAction : Action<ExternalModuleDependency> {
    override fun execute(t: ExternalModuleDependency) {

    }

}
