package pl.setblack.kstones.stones

import io.vavr.Lazy
import pl.setblack.kstones.db.DbSequence

/**
 * This is DI  solution for Kotlin
 */
data class StonesModule(
    private val seq: DbSequence = DbSequence(),
    val stoneRepo: Lazy<StoneRepo> = Lazy.of { StoneRepo(seq) },
    val stoneService: Lazy<StoneService> = Lazy.of {StoneService(stoneRepo.get() )},
    val stoneRest: Lazy<StoneRest> = Lazy.of {
        StoneRest(
            stoneService.get()
        )
    }
)