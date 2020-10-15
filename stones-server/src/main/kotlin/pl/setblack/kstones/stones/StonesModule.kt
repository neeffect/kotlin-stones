package pl.setblack.kstones.stones

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vavr.jackson.datatype.VavrModule
import pl.setblack.kstones.db.DbConnection
import pl.setblack.kstones.db.DbSequence
import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import dev.neeffect.nee.ctx.web.WebContext
import dev.neeffect.nee.effects.jdbc.JDBCProvider
import dev.neeffect.nee.security.UserRole
import java.sql.Connection

typealias Web = WebContext<Connection, JDBCProvider>

/**
 * This is DI  solution for Kotlin
 */
open class StonesModule {

    open val jdbcConfig = DbConnection.jdbcConfig

    open val jdbcProvider = JDBCProvider(jdbcConfig)

    open val context by lazy {
        object : JDBCBasedWebContextProvider() {
            override val jdbcProvider = this@StonesModule.jdbcProvider
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

