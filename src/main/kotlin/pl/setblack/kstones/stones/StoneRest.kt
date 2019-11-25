package pl.setblack.kstones.stones

import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import pl.setblack.kstones.db.DbConnection
import pl.setblack.nee.ctx.web.WebContext

class StoneRest(private val stoneService: StoneService) {

    fun api(): Routing.() -> Unit = {
        get("/stones") {

            val stones = stoneService
                .allStones()
            WebContext.create(DbConnection.jdbcConfig, call).serveMessage(stones, Unit)
//                .perform(createWebContext())(Unit)
//                .map { it -> runBlocking  { call.respond(it) } }
        }
        get("/demo") {
            call.respondText("HELLO WORLD!")
        }
    }
}