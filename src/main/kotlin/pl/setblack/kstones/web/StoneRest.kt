package pl.setblack.kstones.web

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pl.setblack.kstones.StoneService
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
// this worked
//                .toFuture()
//                .get()
//                .get()
//            call.respond(stones)

        }
        get("/demo") {
            call.respondText("HELLO WORLD!")
        }
    }
}