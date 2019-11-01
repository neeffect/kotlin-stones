package pl.setblack.kstones.db

import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.LiquibaseException
import liquibase.resource.ClassLoaderResourceAccessor
import pl.setblack.kstones.ctx.WebContext
import pl.setblack.nee.effects.cache.NaiveCacheProvider
import pl.setblack.nee.effects.jdbc.JDBCProvider
import java.sql.DriverManager
import java.sql.SQLException
import java.util.NoSuchElementException

object DbConnection  {
    val dbUrl = "jdbc:h2:~/kotlin-stones;AUTO_SERVER=TRUE;FILE_LOCK=SOCKET"
    val dbUser = "sa"
    val dbPassword = ""

    fun createDbConnection() = DriverManager.getConnection(
        dbUrl, dbUser, dbPassword)


}