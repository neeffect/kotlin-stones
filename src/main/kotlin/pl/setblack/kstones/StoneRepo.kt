package pl.setblack.kstones

import io.vavr.kotlin.toVavrList
import org.jooq.impl.DSL
import pl.setblack.kstones.dbModel.public_.tables.Stones
import pl.setblack.nee.NEE
import pl.setblack.nee.andThen
import pl.setblack.nee.effects.cache.CacheEffect
import pl.setblack.nee.effects.cache.NaiveCacheProvider
import pl.setblack.nee.effects.jdbc.JDBCProvider
import pl.setblack.nee.effects.tx.TxEffect
import java.sql.Connection

class StoneRepo {
    val jdbc = TxEffect<Connection, JDBCProvider>()
    val cache = CacheEffect<JDBCProvider, Nothing>(NaiveCacheProvider())

    fun readAllStones() = NEE.wrapR(jdbc) { jdbcProvider ->
        val dsl = DSL.using(jdbcProvider.getConnection().getResource())
        dsl.selectFrom(Stones.STONES)
            .fetchInto(Stone::class.java)
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
            DSL.using(jdbcProvider.getConnection().getResource())
                .insertInto(Stones.STONES)
                .values(newStone);
        }
}

