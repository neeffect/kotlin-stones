package pl.setblack.kstones.web

import com.fasterxml.jackson.module.kotlin.KotlinModule
import dev.neeffect.nee.security.jwt.JwtConfig
import dev.neeffect.nee.security.oauth.OauthConfig
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.vavr.jackson.datatype.VavrModule
import pl.setblack.kstones.db.DbConnection.jdbcConfig
import pl.setblack.kstones.db.initializeDb
import pl.setblack.kstones.stones.StonesModule
import pl.setblack.kstones.oauth.OauthConfigLoder
import pl.setblack.kstones.oauth.OauthModule
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.DriverManager

internal fun startServer(config: Pair<JwtConfig, OauthConfig>) {
    val oauthModule = OauthModule(config.second, config.first)
    val stonesModule = StonesModule(oauthModule.jwtConfigModule)

    val server = embeddedServer(Netty, port = 3000) {
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
        routing{
            route("api") {
                stonesModule.stoneRest.api()()
                oauthModule.oauthApi.oauthApi()()
            }

            get("/"){
                call.respondText ("ok")
            }

        }

        routing(stonesModule.context.sysApi())
    }

    DriverManager.getConnection(
        jdbcConfig.url,
        jdbcConfig.user,
        jdbcConfig.password
    ).use {
        initializeDb(it)
    }
    server.start(wait = true)
}

fun main() {

    val secPath = Paths.get("securedEtc").toAbsolutePath()
    println(secPath)
    if (Files.exists(secPath)) {
        val oauthConfigLoder = OauthConfigLoder(secPath)
        oauthConfigLoder.loadJwtConfig().flatMap { jwtConfig ->
            oauthConfigLoder.loadOauthConfig().map {oauthConfig ->
                Pair(jwtConfig, oauthConfig)
            }
        }.forEach {  config ->
            startServer(config)
        }


    } else {
        println("directory: $secPath does not exist")
    }


}


