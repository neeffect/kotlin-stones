package pl.setblack.kstones

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




class StoneRepoTest : BehaviorSpec({
    Given("a repo") {
        createDb()
        val repo = StoneRepo()

        When("Inserting stone to db") {
            val stone = StoneData("old1", 4.toBigDecimal())
            val added = repo.addNewStone(stone)
        }
        Then("") {

        }
    }

}) {
    companion object {
        val dbUrl = "jdbc:h2:mem:test_mem"
        val dbUser = "sa"
        val dbPassword = ""
        fun createDb() {
            try {
                val c: Connection = DriverManager.getConnection(
                    dbUrl, dbUser, dbPassword)
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
    }
}