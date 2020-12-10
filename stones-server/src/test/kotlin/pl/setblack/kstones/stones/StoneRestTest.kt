package pl.setblack.kstones.stones

import com.fasterxml.jackson.core.type.TypeReference
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.be
import io.kotest.matchers.should
import io.ktor.http.*
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.vavr.collection.List
import io.vavr.control.Option
import io.vavr.kotlin.some
import pl.setblack.kotlinStones.Stone
import pl.setblack.kotlinStones.StoneId
import dev.neeffect.nee.effects.jdbc.JDBCProvider
import dev.neeffect.nee.effects.time.HasteTimeProvider
import dev.neeffect.nee.security.User
import dev.neeffect.nee.security.UserRole
import dev.neeffect.nee.security.jwt.*
import dev.neeffect.nee.security.test.TestDB
import dev.neeffect.nee.web.test.TestWebContextProvider
import io.haste.Haste
import io.kotest.matchers.shouldBe
import io.vavr.kotlin.list
import pl.setblack.kstones.oauth.OauthModule
import pl.setblack.kstones.stones.StoneRestTest.Companion.OauthTestConfig.jwtModule
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*

internal class StoneRestTest : DescribeSpec({
    describe("rest server") {
        val testWeb = object : TestWebContextProvider() {
        }
        TestDB(testWeb.jdbcConfig).initializeDb().use { testDb ->
            testDb.addUser("editor", "editor", List.of("writer"))
            TestStonesDbSchema.updateDbSchema(testDb.connection)
            val testStonesModule = object : StonesModule(jwtModule) {
                override val jdbcProvider: JDBCProvider = JDBCProvider(testDb.connection)
            }
            val stoneRest = testStonesModule.stoneRest
            val engine = TestApplicationEngine(testWeb.testEnv)
            engine.start(false)
            engine.application.routing(stoneRest.api())
            it ("created expected jwt token") {
                val user   = User(UUID(1,1),"editor",list(UserRole("writer")))
                val jwt=jwtModule.jwtUsersCoder.encodeUser(user)
                val signed = jwtModule.jwtCoder.signJwt(jwt)
                signed should be (expectedWriterToken)


            }

            it("should display no stones when empty") {

                val stones = engine.handleRequest(
                    HttpMethod.Get, "/stones"
                ).response.content
                stones!! should be("[]")
            }
            it("should fail when adding stones without credentials") {
                val stonesResponseCode = engine.handleRequest(
                    HttpMethod.Post, "/stones"
                ) {
                    this.setBody("""{"name":"nikt", "color":"yellow", "size": 5}""")
                    this.addHeader("Content-Type", "application/json")
                    this.addHeader("Authorization", "Basic Wrong")
                }.response.status()


                stonesResponseCode should be(HttpStatusCode.Unauthorized)
            }
            it("should add  stone ") {

                val stonesString = engine.handleRequest(
                    HttpMethod.Post, "/stones"
                ) {
                    this.setBody("""{"name":"nikt", "color":"yellow", "size": 5}""")
                    this.addHeader("Content-Type", "application/json")
                    this.addHeader("Authorization", "Bearer $expectedWriterToken")
                }.response.content

                val stoneAdded = testWeb.jacksonMapper.readValue<Option<StoneId>>(
                    stonesString,
                    object : TypeReference<Option<StoneId>>() {
                    })
                stoneAdded should be(some(1L))
            }
            it("should add  second stone ") {

                val stonesString = engine.handleRequest(
                    HttpMethod.Post, "/stones"
                ) {
                    this.setBody("""{"name":"burp", "color":"red", "size": 5}""")
                    this.addHeader("Content-Type", "application/json")
                    this.addHeader("Authorization", "Bearer $expectedWriterToken")
                }.response.content

                val stoneAdded = testWeb.jacksonMapper.readValue<Option<StoneId>>(
                    stonesString,
                    object : TypeReference<Option<StoneId>>() {
                    })
                stoneAdded should be(some(2L))
            }
            it("added stones should be returned ") {

                val stonesString = engine.handleRequest(
                    HttpMethod.Get, "/stones"
                ) {
                    this.addHeader("Authorization", "Bearer $expectedWriterToken")
                }.response.content

                val stones = testWeb.jacksonMapper.readValue<List<Stone>>(stonesString,
                    object : TypeReference<List<Stone>>() {
                    })
                stones.size() should be(2)
            }
            it("second stone should be returned ") {

                val stonesString = engine.handleRequest(
                    HttpMethod.Get, "/stones/2"
                ).response.content

                val stone = testWeb.jacksonMapper.readValue(stonesString, Stone::class.java)
                stone.data.name should be("burp")
            }
        }
    }
}) {

    companion object {
        const val  expectedWriterToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MDcyOTM0MjMsImlhdCI6MTYwNzI5MzMyMywiaXNzIjoidGVzdCIsInN1YiI6IjAwMDAwMDAwLTAwMDAtMDAwMS0wMDAwLTAwMDAwMDAwMDAwMSIsImxvZ2luIjoiZWRpdG9yIiwiZGlzcGxheU5hbWUiOiJlZGl0b3IiLCJpZCI6IjAwMDAwMDAwLTAwMDAtMDAwMS0wMDAwLTAwMDAwMDAwMDAwMSIsInJvbGVzIjoid3JpdGVyIn0.ddycHBFLpXUmIJJn4KmxBXyafN7k3rm-t2NG-GxdoVQ"
        object OauthTestConfig {
            val jwtCfg = JwtConfig(100, "test", "test")
            val hasteTime = Haste.TimeSource.withFixedClock(
                Clock.fixed(Instant.parse("2020-12-06T22:22:03.00Z"), ZoneId.of("Europe/Berlin"))
            )

            val time = HasteTimeProvider(hasteTime)
            val jwtModule = object : JwtConfigurationModule<User, UserRole>(jwtCfg, time) {
                override val userCoder: UserCoder<User, UserRole> = SimpleUserCoder()
            }
        }
    }
}




