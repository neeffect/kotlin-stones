package pl.setblack.kstones.db

import dev.neeffect.nee.effects.jdbc.JDBCConfig
import liquibase.Liquibase
import liquibase.database.core.H2Database
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import java.sql.Connection

object DbConnection {
    val jdbcConfig = JDBCConfig(
        driverClassName = "org.h2.Driver",
        url = "jdbc:h2:~/kotlin-stones;AUTO_SERVER=TRUE;FILE_LOCK=SOCKET",
        user = "sa",
        password = ""
    )
}

fun initializeDb(dbConnection: Connection) {
    val database = H2Database().apply {
        connection = JdbcConnection(dbConnection)
    }
    val resourceAccessor = ClassLoaderResourceAccessor(DbConnection::class.java.classLoader)
    val liquibaseChangeLog = Liquibase("db/db.changelog-master.xml", resourceAccessor, database)
    liquibaseChangeLog.update(liquibase.Contexts(), liquibase.LabelExpression())
}
