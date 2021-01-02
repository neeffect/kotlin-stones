package pl.setblack.kstones.db

import dev.neeffect.nee.ANee
import org.jooq.impl.DSL
import org.jooq.Sequence
import pl.setblack.kstones.stones.Web
import dev.neeffect.nee.Nee
import dev.neeffect.nee.anyError
import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import dev.neeffect.nee.effects.tx.TxError
import dev.neeffect.nee.effects.tx.TxProvider
import dev.neeffect.nee.then
import java.sql.Connection

interface SequenceGenerator<R : TxProvider<Connection, R>> {
    fun next(): Nee<R, TxError, Long>
}

class DbSequence(
    private val context: JDBCBasedWebContextProvider,
    private val seq: Sequence<Long>
) : SequenceGenerator<Web> {
    override fun next() =
        Nee.with(context.fx().tx) { jdbcProvider ->
            val dsl = DSL.using(jdbcProvider.getConnection().getResource())
            dsl.nextval(seq)
        }
}
