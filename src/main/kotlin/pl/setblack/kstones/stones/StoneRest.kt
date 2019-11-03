package pl.setblack.kstones.stones

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.runBlocking
import pl.setblack.kstones.stones.StoneService
import pl.setblack.kstones.ctx.WebContext
import pl.setblack.kstones.db.DbConnection
import pl.setblack.nee.effects.cache.NaiveCacheProvider
import pl.setblack.nee.effects.jdbc.JDBCProvider

class StoneRest(private val stoneService: StoneService) {

    fun createWebContext() =
        WebContext(JDBCProvider(DbConnection.createDbConnection()), NaiveCacheProvider())

    fun api(): Routing.() -> Unit = {
        get("/stones") {
            val stones = stoneService
                .allStones()
                .perform(createWebContext())(Unit)
                .map { runBlocking  { call.respond(it) } }
        }
        get("/demo") {
            call.respondText("HELLO WORLD!")
        }
    }
}