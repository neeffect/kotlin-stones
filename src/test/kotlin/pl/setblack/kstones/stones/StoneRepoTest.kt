package pl.setblack.kstones.stones

import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.ktor.application.Application
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.createTestEnvironment
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.LiquibaseException
import liquibase.resource.ClassLoaderResourceAccessor
import pl.setblack.kstones.db.DbSequence
import pl.setblack.nee.ctx.web.WebContext
import pl.setblack.nee.effects.jdbc.JDBCConfig
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext


class StoneRepoTest : BehaviorSpec({
    Given("a repo") {
        createDb()
        val repo = StoneRepo(DbSequence())
        val wc = createTestWebContext()

        When("Inserting stone to db") {
            val stone = StoneData("old1", 4.toBigDecimal())
            val added = repo.addNewStone(stone)
                //.perform(wc)(Unit)
            Then("") {
                val result = added.flatMap {
                    repo.readAllStones()
                }.perform(wc)(Unit).toFuture().get()
                result.get().size() shouldBe 1
            }
        }
    }

}) {
    companion object {
        val jdbcConfig =  JDBCConfig(
            driverClassName = "org.h2.Driver",
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            user = "sa",
            password = ""
        )
        fun createDb() {
            try {
                val c = createDbConnection()
                val database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(c))
                val liquibase = Liquibase("db/db.changelog-master.xml", ClassLoaderResourceAccessor(), database)
                liquibase.update(Contexts(), LabelExpression())
            } catch (e: SQLException) {
                e.printStackTrace()
                throw NoSuchElementException(e.message)
            } catch (e: LiquibaseException) {
                e.printStackTrace()
                throw NoSuchElementException(e.message)
            }
        }

        fun createDbConnection() = DriverManager.getConnection(
            jdbcConfig.url,
            jdbcConfig.user,
            jdbcConfig.password
        )

        fun createTestWebContext() : WebContext{
            val testEnv = createTestEnvironment()
            val testApplication = Application( testEnv)
            val testCall = TestApplicationCall(testApplication,false, EmptyCoroutineContext)
            return WebContext.create(jdbcConfig, testCall)
        }

    }
}