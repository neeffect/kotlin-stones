package pl.setblack.kstones.stones

import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import dev.neeffect.nee.ctx.web.WebContext
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
import pl.setblack.kstones.db.DbSequence
import pl.setblack.kstones.oauth.OauthModule

import java.sql.Connection

typealias Web = WebContext<Connection, JDBCProvider>

/**
 * This is DI  solution for Kotlin
 */
open class StonesModule(private val jwtConfigurationModule: JwtConfigurationModule<User, UserRole>) {

    open val jdbcConfig = DbConnection.jdbcConfig

    open val jdbcProvider = JDBCProvider(jdbcConfig)

    open val context by lazy {
        object : JDBCBasedWebContextProvider() {
            override val jdbcProvider = this@StonesModule.jdbcProvider

            override fun authProvider(call: ApplicationCall): SecurityProvider<User, UserRole> =
                JwtAuthProvider( call.request.header(HttpHeaders.Authorization).option(), jwtConfigurationModule)
        }
    }

    //---------------------------------
    private val seq: DbSequence by lazy {
        DbSequence(context)
    }

    open val stoneRepo by lazy { StoneRepo(context, seq) }

    open val stoneService by lazy { StoneService(context, stoneRepo) }

    open val stoneRest by lazy { StoneRest(context, stoneService) }


    object SecurityRoles {
        val writer = UserRole("writer")
    }
}

