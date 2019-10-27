package pl.setblack.kstones

import arrow.core.right
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import liquibase.Liquibase
import liquibase.changelog.DatabaseChangeLog
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.LiquibaseException
import liquibase.resource.ClassLoaderResourceAccessor
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import liquibase.LabelExpression
import liquibase.Contexts
import org.jooq.impl.DSL.update
import pl.setblack.kstones.ctx.WebContext
import pl.setblack.nee.effects.cache.NaiveCacheProvider
import pl.setblack.nee.effects.jdbc.JDBCProvider


class StoneRepoTest : BehaviorSpec({
    Given("a repo") {
        createDb()
        val repo = StoneRepo()
        val wc = createWebContext()

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
        val dbUrl = "jdbc:h2:mem:test_mem"
        val dbUser = "sa"
        val dbPassword = ""
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
                dbUrl, dbUser, dbPassword)

        fun createWebContext() =
            WebContext(JDBCProvider(createDbConnection()),NaiveCacheProvider())
    }
}