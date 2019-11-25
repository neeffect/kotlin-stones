package pl.setblack.kstones.db

import org.jooq.impl.DSL


import pl.setblack.kstones.dbModel.public_.Sequences
import pl.setblack.nee.Nee
import pl.setblack.nee.UANee
import pl.setblack.nee.UNee
import pl.setblack.nee.ctx.web.WebContext
import pl.setblack.nee.effects.tx.TxError
import pl.setblack.nee.effects.tx.TxProvider
import java.sql.Connection

interface SequenceGenerator<R : TxProvider<Connection, R>> {
    fun next() : UANee<R, Long>
}

class DbSequence  : SequenceGenerator<WebContext>{
    override fun next() =
        Nee.constP(WebContext.Effects.jdbc) { jdbcProvider ->
            val dsl = DSL.using(jdbcProvider.getConnection().getResource())
            dsl.nextval(Sequences.GLOBALSTONESSEQ)
        }
}