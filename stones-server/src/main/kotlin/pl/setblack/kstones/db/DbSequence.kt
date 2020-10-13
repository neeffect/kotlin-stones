package pl.setblack.kstones.db

import org.jooq.impl.DSL
import pl.setblack.kstones.dbModel.public_.Sequences
import pl.setblack.kstones.stones.Web
import pl.setblack.nee.Nee
import pl.setblack.nee.UANee
import pl.setblack.nee.ctx.web.JDBCBasedWebContext
import pl.setblack.nee.effects.tx.TxProvider
import java.sql.Connection

interface SequenceGenerator<R : TxProvider<Connection, R>> {
    fun next() : UANee<R, Long>
}

class DbSequence(private val context: JDBCBasedWebContext) : SequenceGenerator<Web> {
    override fun next() =
        Nee.constP(context.effects().jdbc) { jdbcProvider ->
            val dsl = DSL.using(jdbcProvider.getConnection().getResource())
            dsl.nextval(Sequences.GLOBALSTONESSEQ)
        }
}
