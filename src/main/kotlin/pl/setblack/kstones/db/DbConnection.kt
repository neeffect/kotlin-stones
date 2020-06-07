package pl.setblack.kstones.db

import pl.setblack.nee.effects.jdbc.JDBCConfig

object DbConnection {
    val jdbcConfig = JDBCConfig(
        driverClassName = "org.h2.Driver",
        url = "jdbc:h2:~/kotlin-stones;AUTO_SERVER=TRUE;FILE_LOCK=SOCKET",
        user = "sa",
        password = ""
    )


   // fun createDbConnection()= TODO()


}