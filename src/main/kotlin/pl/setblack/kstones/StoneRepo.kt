package pl.setblack.kstones

import io.vavr.kotlin.toVavrList
import org.jooq.impl.DSL
import pl.setblack.kstones.ctx.WebContext
import pl.setblack.kstones.dbModel.public_.Sequences.STONESSEQ
import pl.setblack.kstones.dbModel.public_.tables.Stones
import pl.setblack.kstones.dbModel.public_.tables.records.StonesRecord
import pl.setblack.nee.NEE
import pl.setblack.nee.andThen
import pl.setblack.nee.effects.cache.CacheEffect
import pl.setblack.nee.effects.cache.NaiveCacheProvider
import pl.setblack.nee.effects.jdbc.JDBCProvider
import pl.setblack.nee.effects.tx.TxEffect
import java.sql.Connection

class StoneRepo {
    val jdbc = TxEffect<Connection, WebContext>()
    val cache = CacheEffect<WebContext, Nothing>(NaiveCacheProvider())

    fun readAllStones() = NEE.wrapR(jdbc) { jdbcProvider ->
        val dsl = DSL.using(jdbcProvider.getConnection().getResource())
        dsl.selectFrom(Stones.STONES)
            .fetchInto(StonesRecord::class.java)
            .toVavrList()
    }

    fun readStone() = NEE.pure(cache.andThen(jdbc)) { jdbcProvider ->
        { id: StoneId ->
            val dsl = DSL.using(jdbcProvider.getConnection().getResource())
            dsl.selectFrom(Stones.STONES)
                .where(Stones.STONES.ID.eq(id))
                .fetchOneInto(Stone::class.java)
        }
    }.u()

    fun addNewStone(newStone: StoneData) =
        NEE.wrapR(jdbc) { jdbcProvider ->
            try {
                val dsl = DSL.using(jdbcProvider.getConnection().getResource())
                val nextKey = STONESSEQ.nextval()
                dsl.insertInto(Stones.STONES)
                    .columns(Stones.STONES.NAME, Stones.STONES.PRICE)
                    .values(newStone.name, newStone.price)
                    .execute()
            } catch (e:Exception) {
                e.printStackTrace()
                throw  e
            }

        }
}

