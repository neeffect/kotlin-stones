package pl.setblack.kstones.db

import org.jooq.impl.DSL
import pl.setblack.kstones.ctx.WebContext
import pl.setblack.kstones.ctx.WebEffects
import pl.setblack.kstones.dbModel.public_.Sequences
import pl.setblack.nee.NEE
import pl.setblack.nee.effects.tx.TxError
import pl.setblack.nee.effects.tx.TxProvider
import java.sql.Connection

interface SequenceGenerator<R : TxProvider<Connection, R>> {
    fun next() : NEE<R, TxError, Unit, Long>
}

class DbSequence()  : SequenceGenerator<WebContext>{
    override fun next() =
        NEE.constP(WebEffects.jdbc) { jdbcProvider ->
            val dsl = DSL.using(jdbcProvider.getConnection().getResource())
            dsl.nextval(Sequences.GLOBALSTONESSEQ)
        }
}