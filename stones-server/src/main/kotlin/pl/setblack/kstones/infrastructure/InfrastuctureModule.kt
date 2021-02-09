package pl.setblack.kstones.infrastructure

import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import dev.neeffect.nee.ctx.web.jwt.JwtAuthProvider
import dev.neeffect.nee.effects.jdbc.JDBCProvider
import dev.neeffect.nee.effects.security.SecurityProvider
import dev.neeffect.nee.security.User
import dev.neeffect.nee.security.UserRole
import dev.neeffect.nee.security.jwt.JwtConfigurationModule
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.vavr.kotlin.option
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.setblack.kstones.db.DbConnection
import pl.setblack.kstones.db.initializeDb
import java.sql.DriverManager

open class InfrastuctureModule(private val jwtConfigurationModule: JwtConfigurationModule<User, UserRole>) {

    open val jdbcConfig by lazy {
        DbConnection.jdbcConfig.also {
            upgradeDatabase()
        }
    }

    open val jdbcProvider by lazy {
        JDBCProvider(jdbcConfig)
    }

    open val context by lazy {
        object : JDBCBasedWebContextProvider() {
            override val jdbcProvider by lazy {
                this@InfrastuctureModule.jdbcProvider
            }

            override fun authProvider(call: ApplicationCall): SecurityProvider<User, UserRole> =
                JwtAuthProvider(call.request.header(HttpHeaders.Authorization).option(), jwtConfigurationModule)
        }
    }

    object SecurityRoles {
        val writer = UserRole("writer")
    }
}





private fun upgradeDatabase() {
    GlobalScope.launch {
        DriverManager.getConnection(
            DbConnection.jdbcConfig.url,
            DbConnection.jdbcConfig.user,
            DbConnection.jdbcConfig.password
        ).use {
            initializeDb(it)
        }
    }
}
