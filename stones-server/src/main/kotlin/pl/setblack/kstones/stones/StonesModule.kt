package pl.setblack.kstones.stones

import io.vavr.Lazy
import pl.setblack.kstones.db.DbConnection
import pl.setblack.kstones.db.DbSequence
import pl.setblack.nee.security.UserRole

/**
 * This is DI  solution for Kotlin
 */
open class StonesModule {
    private val seq: DbSequence = DbSequence()

    val jdbcConfig  = DbConnection.jdbcConfig

    val stoneRepo by lazy { StoneRepo(seq) }

    val stoneService by lazy { StoneService(stoneRepo) }

    val stoneRest by lazy { StoneRest(stoneService, jdbcConfig) }

    object SecurityRoles {
        val writer = UserRole("writer")
    }
}