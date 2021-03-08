package pl.setblack.kstones.web

import com.fasterxml.jackson.module.kotlin.KotlinModule
import dev.neeffect.nee.security.oauth.config.OauthConfigLoder
import dev.neeffect.nee.security.oauth.config.RolesMapper
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.vavr.jackson.datatype.VavrModule
import io.vavr.kotlin.list
import org.slf4j.LoggerFactory
import pl.setblack.kstones.infrastructure.InfrastuctureModule
import pl.setblack.kstones.oauth.StonesOauthModule
import java.nio.file.Files
import java.nio.file.Paths

internal val startTime = System.currentTimeMillis()

internal fun startServer(oauthModule: StonesOauthModule) {
    val webModule = WebModule(oauthModule.oauthModule.jwtConfigModule)


    LoggerFactory.getLogger("main").info("starting server")
    val server = embeddedServer(Netty, port = 3000, watchPaths = emptyList()) {

        install(ContentNegotiation) {
            jackson {
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

fun main() {
    LoggerFactory.getILoggerFactory().getLogger("main").info("starting")

    val rolesMapper: RolesMapper = { _, _ ->
        list(InfrastuctureModule.SecurityRoles.writer)
    }
    val secPath = Paths.get("securedEtc").toAbsolutePath()
    if (Files.exists(secPath)) {
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
    } else {
        println("directory: $secPath with security config does not exist")
    }
}


