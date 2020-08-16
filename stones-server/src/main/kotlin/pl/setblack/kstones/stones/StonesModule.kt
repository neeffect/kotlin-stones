package pl.setblack.kstones.stones

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vavr.Lazy
import io.vavr.jackson.datatype.VavrModule
import pl.setblack.kstones.db.DbConnection
import pl.setblack.kstones.db.DbSequence
import pl.setblack.nee.effects.jdbc.JDBCProvider
import pl.setblack.nee.security.UserRole

/**
 * This is DI  solution for Kotlin
 */
open class StonesModule {
    private val seq: DbSequence = DbSequence()

    open val objectMapper : ObjectMapper by lazy {
        ObjectMapper()
            .registerModule(VavrModule())
            .registerModule( KotlinModule())
    }

    open val jdbcConfig  = DbConnection.jdbcConfig

    open val jdbcProvider  = JDBCProvider(jdbcConfig)

    open val stoneRepo by lazy { StoneRepo(seq) }

    open val stoneService by lazy { StoneService(stoneRepo) }

    open val stoneRest by lazy { StoneRest(stoneService, jdbcProvider) }

    object SecurityRoles {
        val writer = UserRole("writer")
    }
}
