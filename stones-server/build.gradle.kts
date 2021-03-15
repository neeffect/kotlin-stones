import org.jetbrains.kotlin.config.KotlinCompilerVersion
import nu.studer.gradle.jooq.JooqEdition
import org.gradle.jvm.tasks.Jar

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("nu.studer.jooq") version "5.1.1"
    id("org.liquibase.gradle") version "2.0.4"
    id("org.jetbrains.kotlin.plugin.serialization")
}


 val db = mapOf(
    "url"      to  "jdbc:h2:${projectDir}/build/kotlin-stones;AUTO_SERVER=TRUE;FILE_LOCK=SOCKET",
    "user"     to "sa",
    "password" to ""
)

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(Libs.JOOQ.jooq)
    implementation(Libs.Kotlin.reflect)
    implementation(Libs.H2.database)
    jooqGenerator(Libs.H2.database)

    implementation(Libs.Nee.ctxWebKtor)
    implementation(Libs.Nee.jdbc)
    implementation(Libs.Nee.securityJdbc)

    implementation(Libs.Ktor.clientJvm)
    implementation(Libs.Ktor.clientCIO)

    implementation(Libs.Ktorm.core)

// https://mvnrepository.com/artifact/io.netty/netty-transport-native-epoll
    implementation( group = "io.netty",
        name ="netty-transport-native-epoll",
        version= "4.1.54.Final",
        classifier = "linux-x86_64")


    implementation(project(":stones-common"))
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))

    Libs.Jackson.moduleKotlin

    // implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation(Libs.Ktor.serverNetty)
    implementation(Libs.Ktor.jackson)
    implementation(Libs.Vavr.kotlin)
    implementation(Libs.Vavr.jackson)

    implementation(Libs.Liquibase.core)

    liquibaseRuntime(Libs.Liquibase.core)
    liquibaseRuntime(Libs.H2.database)

    testImplementation(Libs.Kotest.runnerJunit5Jvm)

    testImplementation(Libs.Ktor.serverTestHost)
    testImplementation(Libs.Nee.jdbcTest)
    testImplementation(Libs.Nee.ctxWebTest)
    implementation(Libs.Kotlin.serializationJson)
    implementation (Libs.Kotlin.serializationCore)

    runtimeOnly(Libs.Hoplite.core)
    runtimeOnly(Libs.Hoplite.yaml)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


tasks.withType<Test> {
    useJUnitPlatform()
}

liquibase {
    activities.register("main") {
        val db_url = db["url"]
        val db_user =  db["user"]
        val db_pass =  db["password"]

        this.arguments = mapOf(
            "logLevel" to "info",
            "changeLogFile" to "stones-server/src/main/resources/db/db.changelog-master.xml",
            "url" to db_url,
            "username" to db_user,
            "password" to db_pass
        )
    }
    runList = "main"
}


jooq {
    version.set(Libs.JOOQ.version)
    edition.set(JooqEdition.OSS)

    configurations {
        create("main") {
            jooqConfiguration.apply {
                //logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc.apply {
                    driver = "org.h2.Driver"
                    url = db["url"]
                    user = db["user"]
                    password = db["password"]
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.h2.H2Database"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = false
                    }
                    target.apply {
                        packageName = "pl.setblack.kstones.dbModel"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }

}

project.tasks["compileKotlin"].dependsOn += "generateJooq"
project.tasks["generateJooq"].dependsOn += "update"

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.apply {
    jvmTarget = "1.8"
    javaParameters = true
    allWarningsAsErrors = true
}
val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileTestKotlin.kotlinOptions.apply {
    jvmTarget = "1.8"
    javaParameters = true
    allWarningsAsErrors = false
}

compileKotlin.kotlinOptions.apply {
    jvmTarget = "1.8"
    javaParameters = true
    allWarningsAsErrors = true
}




val fatJar = task("fatJar", type = Jar::class) {
    archiveBaseName.set("server-all")
    manifest {
        attributes["Implementation-Title"] = "Gradle Jar File Example"
        attributes["Main-Class"] = "pl.setblack.kstones.web.MainKt"
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}
