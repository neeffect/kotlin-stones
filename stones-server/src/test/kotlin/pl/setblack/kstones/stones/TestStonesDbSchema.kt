package pl.setblack.kstones.stones

import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.LiquibaseException
import liquibase.resource.ClassLoaderResourceAccessor
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object TestStonesDbSchema {
    val jdbcConfig = TestCtx.jdbcConfig

    fun createDb() : AutoCloseable {
            val c = createDbConnection()
            return updateDbSchema(c)
    }

    internal fun updateDbSchema(c: Connection):TestConnection {
        try {
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

}

data class TestConnection (val jdbcConnection: Connection) : AutoCloseable{
    override fun close() {
        cleanDb()
        jdbcConnection.close()
    }

    private fun cleanDb() {
        jdbcConnection.createStatement().use { dropAll ->
            val result = dropAll.execute("drop all objects")
            println(result)
        }
    }

}
