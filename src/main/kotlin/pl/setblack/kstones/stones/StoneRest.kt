package pl.setblack.kstones.stones

import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import pl.setblack.kstones.db.DbConnection
import pl.setblack.nee.Nee
import pl.setblack.nee.ctx.web.WebContext

class StoneRest(private val stoneService: StoneService) {

    fun api(): Routing.() -> Unit = {
        get("/stones") {
            val stones = stoneService
                .allStones()
            WebContext.create(DbConnection.jdbcConfig, call).serveMessage(async { stones }, Unit)
        }
        get("/demo") {
            call.respondText("HELLO WORLD!")
        }
    }
}

fun <E, P, A> async(func: () -> Nee<WebContext, E, P, A>) =
    (Nee.constP(WebContext.Effects.async) { _ -> Unit } as Nee<WebContext, E, P, Unit>)
        .flatMap {
            println("in thread " + Thread.currentThread().name)
            func()
        }
