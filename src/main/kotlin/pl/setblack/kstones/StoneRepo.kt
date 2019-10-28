package pl.setblack.kstones

import io.vavr.kotlin.toVavrList
import org.jooq.impl.DSL
import pl.setblack.kstones.ctx.WebEffects.cache
import pl.setblack.kstones.ctx.WebEffects.jdbc
import pl.setblack.kstones.dbModel.public_.tables.Stones
import pl.setblack.kstones.dbModel.public_.tables.records.StonesRecord
import pl.setblack.nee.NEE
import pl.setblack.nee.andThen

class StoneRepo {


    fun readAllStones() = NEE.constP(jdbc) { jdbcProvider ->
        val dsl = DSL.using(jdbcProvider.getConnection().getResource())
        dsl.selectFrom(Stones.STONES)
                .fetchInto(StonesRecord::class.java)
                .toVavrList()
                .map {
                    Stone( it.id, StoneData(it.name, it.price))
                }
    }

    fun readStone() = NEE.pure(cache.andThen(jdbc)) { jdbcProvider ->
        { id: StoneId ->
            val dsl = DSL.using(jdbcProvider.getConnection().getResource())
            dsl.selectFrom(Stones.STONES)
                    .where(Stones.STONES.ID.eq(id))
                    .fetchOneInto(Stone::class.java)
        }
    }.constP()

    fun addNewStone(newStone: StoneData) =
            NEE.constP(jdbc) { jdbcProvider ->
                try {
                    val dsl = DSL.using(jdbcProvider.getConnection().getResource())
                    dsl.insertInto(Stones.STONES)
                            .columns(Stones.STONES.NAME, Stones.STONES.PRICE)
                            .values(newStone.name, newStone.price)
                            .returning(Stones.STONES.ID)
                            .fetchOne()
                            .id
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw  e
                }
            }
}
