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
import pl.setblack.kstones.db.DbConnection

open class InfrastuctureModule(private val jwtConfigurationModule: JwtConfigurationModule<User, UserRole>) {

    open val jdbcConfig = DbConnection.jdbcConfig

    open val jdbcProvider = JDBCProvider(jdbcConfig)

    open val context by lazy {
        object : JDBCBasedWebContextProvider() {
            override val jdbcProvider = this@InfrastuctureModule.jdbcProvider

            override fun authProvider(call: ApplicationCall): SecurityProvider<User, UserRole> =
                JwtAuthProvider(call.request.header(HttpHeaders.Authorization).option(), jwtConfigurationModule)
        }
    }
    object SecurityRoles {
        val writer = UserRole("writer")
    }
}
