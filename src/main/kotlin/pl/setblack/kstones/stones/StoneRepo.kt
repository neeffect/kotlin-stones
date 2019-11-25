package pl.setblack.kstones.stones


import io.vavr.collection.List
import io.vavr.control.Option
import io.vavr.kotlin.toVavrList
import org.jooq.impl.DSL
import pl.setblack.kstones.db.SequenceGenerator
import pl.setblack.kstones.dbModel.public_.tables.Stones
import pl.setblack.kstones.dbModel.public_.tables.records.StonesRecord
import pl.setblack.nee.Nee
import pl.setblack.nee.UANee
import pl.setblack.nee.andThen
import pl.setblack.nee.ctx.web.WebContext
import pl.setblack.nee.ctx.web.WebContext.Effects.cache
import pl.setblack.nee.ctx.web.WebContext.Effects.jdbc

class StoneRepo(private val seq: SequenceGenerator<WebContext>) {

    fun readAllStones(): UANee<WebContext, List<Stone>> = Nee.constP(jdbc) { jdbcProvider ->
        val dsl = DSL.using(jdbcProvider.getConnection().getResource())
        dsl.selectFrom(Stones.STONES)
            .fetchInto(StonesRecord::class.java)
            .toVavrList()
            .map {
                Stone(it.id, StoneData(it.name, it.price))
            }
    }.anyError()

    fun readStone() = Nee.pure(cache.andThen(jdbc)) { jdbcProvider ->
        { id: StoneId ->
            val dsl = DSL.using(jdbcProvider.getConnection().getResource())
            dsl.selectFrom(Stones.STONES)
                .where(Stones.STONES.ID.eq(id))
                .fetchOneInto(Stone::class.java)
        }
    }.constP()

    fun addNewStone(newStone: StoneData) = seq.next().flatMap {
        addStone(it, newStone)
    }

    private fun addStone(stoneId: Long, newStone: StoneData) = Nee.constP(jdbc) { jdbcProvider ->
        val dsl = DSL.using(jdbcProvider.getConnection().getResource())
        val insertedRows = dsl.insertInto(Stones.STONES)
            .values(stoneId, newStone.name, newStone.price)
            .execute()
        if (insertedRows == 1) {
            Option.some(stoneId)
        } else {
            Option.none()
        }
    }
}