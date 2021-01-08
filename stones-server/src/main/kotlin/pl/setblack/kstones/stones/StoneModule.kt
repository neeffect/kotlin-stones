package pl.setblack.kstones.stones

import dev.neeffect.nee.ctx.web.WebContext
import dev.neeffect.nee.effects.jdbc.JDBCProvider
import pl.setblack.kstones.db.DbSequence
import pl.setblack.kstones.dbModel.public_.Sequences
import pl.setblack.kstones.infrastructure.InfrastuctureModule

import java.sql.Connection

typealias Web = WebContext<Connection, JDBCProvider>

/**
 * This is DI  solution for Kotlin
 */
open class StonesModule(
    private val infra: InfrastuctureModule) {

    //---------------------------------
    private val seq: DbSequence by lazy {
        DbSequence(infra.context, Sequences.GLOBALSTONESSEQ)
    }

    open val stoneRepo by lazy { StoneRepo(infra.context, seq) }

    open val stoneService by lazy { StoneService(infra.context, stoneRepo) }

}

