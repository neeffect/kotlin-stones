package pl.setblack.kstones.stones

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.vavr.jackson.datatype.VavrModule
import pl.setblack.kstones.db.DbConnection
import pl.setblack.nee.Nee
import pl.setblack.nee.ctx.web.JDBCBasedWebContext
import pl.setblack.nee.ctx.web.WebContext
import pl.setblack.nee.effects.jdbc.JDBCConfig
import pl.setblack.nee.effects.jdbc.JDBCProvider
import java.math.BigDecimal

class StoneRest(
    private val stoneService: StoneService,
    private val aJdbcProvider1: JDBCProvider) {

    val webContext = object  : JDBCBasedWebContext(){
        override val jdbcProvider = aJdbcProvider1
    }
    fun api(): Routing.() -> Unit = {
        install(ContentNegotiation) {
            jackson {
                this.registerModule(VavrModule())
                this.registerModule(KotlinModule())
            }
        }
        get("/stones") {
            val stones = stoneService
                .allStones()
            webContext.create(call).serveMessage(async { stones }, Unit)

        }
        post("/stones"){
            val newStone = call.receive<StoneData>()
            println(newStone)
            val stoneAdded =
                    stoneService.addStone(StoneData("irre", 10.99.toBigDecimal()))

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
