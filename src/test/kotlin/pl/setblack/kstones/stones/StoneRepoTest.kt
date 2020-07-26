package pl.setblack.kstones.stones

import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.ktor.application.Application
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.createTestEnvironment
import io.vavr.control.Option
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.LiquibaseException
import liquibase.resource.ClassLoaderResourceAccessor
import pl.setblack.kstones.db.DbSequence
import pl.setblack.nee.Nee
import pl.setblack.nee.ctx.web.WebContext
import pl.setblack.nee.effects.jdbc.JDBCConfig
import java.lang.RuntimeException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext


class StoneRepoTest : BehaviorSpec({
    Given("a repo") {
        val repo = StoneRepo(DbSequence())
        val wc = createTestWebContext()

        When(" stone inserted into db") {
            val stone = StoneData("old1", 4.toBigDecimal())
            val insertedStoneId = repo.addNewStone(stone)
            Then("stone can be read") {
                createDb().use {
                    val result = insertedStoneId.flatMap { maybeStoneId ->
                        maybeStoneId.map { stoneId ->
                            println("reading stone")
                            repo.readStone().constP()(stoneId).map {
                                Option.some(it)
                            }
                        }.getOrElse(Nee.pure(Option.none()))
                    }.perform(wc)(Unit).toFuture().get()
                    println("performed")
                    result.get().get().data.name shouldBe "old1"
                }
            }

            Then("stone will be in  all stones") {
                createDb().use {
                    val result = insertedStoneId.flatMap {
                        repo.readAllStones()
                    }.perform(wc)(Unit).toFuture().get()
                    result.get().size() shouldBe 1
                }
            }
        }
    }

}) {
    companion object {
        val jdbcConfig = JDBCConfig(
            driverClassName = "org.h2.Driver",
            url = "jdbc:h2:mem:test",
            user = "sa",
            password = ""
        )

        fun createDb() : AutoCloseable {
            try {
                val c = createDbConnection()
                val database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(c))
                val liquibase = Liquibase("db/db.changelog-master.xml", ClassLoaderResourceAccessor(), database)
                liquibase.update(Contexts(), LabelExpression())

                return TestConnection(c)
            } catch (e: SQLException) {
                throw RuntimeException(e)
            } catch (e: LiquibaseException) {
                throw RuntimeException(e)
            }
        }

        fun createDbConnection() = DriverManager.getConnection(
            jdbcConfig.url,
            jdbcConfig.user,
            jdbcConfig.password
        )

        fun createTestWebContext(): WebContext {
            val testEnv = createTestEnvironment()
            val testApplication = Application(testEnv)
            val testCall = TestApplicationCall(testApplication, false, EmptyCoroutineContext)
            return WebContext.create(jdbcConfig, testCall)
        }
    }

    data class TestConnection (val jdbcConnection: Connection) : AutoCloseable{
        override fun close() {
            cleanDb()
            jdbcConnection.close()
        }

        private fun cleanDb() {
            jdbcConnection.createStatement().use { dropAll ->
                val result = dropAll.execute("drop all objects")
            }

        }
    }


}