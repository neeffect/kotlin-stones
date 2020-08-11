package pl.setblack.kstones.stones

import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import pl.setblack.kstones.db.DbConnection
import pl.setblack.nee.Nee
import pl.setblack.nee.ctx.web.JDBCBasedWebContext
import pl.setblack.nee.ctx.web.WebContext
import pl.setblack.nee.effects.jdbc.JDBCConfig
import java.math.BigDecimal

class StoneRest(private val stoneService: StoneService, val parentJdbcConfig: JDBCConfig) {
    val webContext = object  : JDBCBasedWebContext(){
        override val jdbcConfig: JDBCConfig = parentJdbcConfig
    }
    fun api(): Routing.() -> Unit = {
        get("/stones") {
            val stones = stoneService
                .allStones()
            webContext.create(call).serveMessage(async { stones }, Unit)

        }
        post("/stones"){
            val newStone = call.receive<StoneData>()
            println(newStone)
            val stoneAdded =
                    stoneService.addStone(newStone)

            webContext.create(call).serveMessage(async { stoneAdded }, Unit)
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
