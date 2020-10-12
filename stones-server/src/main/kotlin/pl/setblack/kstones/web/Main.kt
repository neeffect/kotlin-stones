package pl.setblack.kstones.web

import com.fasterxml.jackson.module.kotlin.KotlinModule
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
import pl.setblack.nee.Nee
import java.sql.Driver
import java.sql.DriverManager

fun main() {
    val stonesModule = StonesModule()

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


