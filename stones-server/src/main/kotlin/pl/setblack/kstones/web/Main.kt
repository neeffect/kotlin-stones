package pl.setblack.kstones.web

import com.fasterxml.jackson.module.kotlin.KotlinModule
import dev.neeffect.nee.effects.utils.Logging
import dev.neeffect.nee.security.oauth.config.OauthConfigLoder
import dev.neeffect.nee.security.oauth.config.RolesMapper
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.vavr.jackson.datatype.VavrModule
import io.vavr.kotlin.list
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import pl.setblack.kstones.db.DbConnection.jdbcConfig
import pl.setblack.kstones.db.initializeDb
import pl.setblack.kstones.infrastructure.InfrastuctureModule
import pl.setblack.kstones.oauth.StonesOauthModule
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.DriverManager

internal val startTime = System.currentTimeMillis()

internal fun startServer(oauthModule: StonesOauthModule) {
    val webModule = WebModule(oauthModule.oauthModule.jwtConfigModule)
    upgradeDatabase()

    LoggerFactory.getLogger("main").info("starting server")
    val server = embeddedServer(Netty, port = 3000, watchPaths = emptyList()) {

        install(ContentNegotiation) {
            jackson {
                //TODO check if needed
                this.registerModule(VavrModule())
                this.registerModule(KotlinModule())
            }
        }
        install(StatusPages) {
            exception<Throwable> { cause ->
                cause.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        routing {
            route("api") {
                webModule.stoneRest.api()()
                oauthModule.oauthApi.oauthApi()()
            }

            get("/") {
                call.respondText("ok")
            }

        }
        routing(webModule.infraModule.context.sysApi())
    }

    server.start(wait = false)
    val startupTime = System.currentTimeMillis() - startTime;
    LoggerFactory.getILoggerFactory().getLogger("main").info("started in $startupTime ms")
}

private fun upgradeDatabase() {
    GlobalScope.launch {
        DriverManager.getConnection(
            jdbcConfig.url,
            jdbcConfig.user,
            jdbcConfig.password
        ).use {
           // initializeDb(it)
        }
    }
}

fun main() {
    LoggerFactory.getILoggerFactory().getLogger("main").info("starting")

    val rolesMapper: RolesMapper = { _, _ ->
        list(InfrastuctureModule.SecurityRoles.writer)
    }
    val secPath = Paths.get("securedEtc").toAbsolutePath()
    if (Files.exists(secPath)) {
        try {
            val oauthConfigLoder = OauthConfigLoder(secPath)
            oauthConfigLoder.loadConfig(rolesMapper)
                .map { config ->
                    StonesOauthModule(config)

                }
                .map { oauth ->
                    startServer(oauth)
                }.mapLeft { configError ->
                    println("error loading config: $configError")
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    } else {
        println("directory: $secPath with security config does not exist")
    }


}


