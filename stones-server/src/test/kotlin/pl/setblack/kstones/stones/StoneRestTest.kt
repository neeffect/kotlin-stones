package pl.setblack.kstones.stones

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.be
import io.kotest.matchers.should
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.handleRequest
import kotlinx.coroutines.newFixedThreadPoolContext
import pl.setblack.nee.web.test.TestWebContext

internal class StoneRestTest : DescribeSpec({
    describe("rest server") {
        val stoneService = StonesModule().stoneRest
        val testWeb = object : TestWebContext() {
        }

        val engine = TestApplicationEngine(testWeb.testEnv)
        engine.start(false)
        engine.application.routing (stoneService.api())


        it("should display no stones when empty") {
            val stones = engine.handleRequest (
                HttpMethod.Get, "/stones").response.content
            stones!! should be("[]")
        }
    }
})
