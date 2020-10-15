package pl.setblack.kstones.stones


import dev.neeffect.nee.Nee
import dev.neeffect.nee.UANee
import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import dev.neeffect.nee.then
import io.vavr.collection.List
import io.vavr.control.Option
import io.vavr.kotlin.toVavrList
import org.jooq.impl.DSL
import pl.setblack.kotlinStones.Stone
import pl.setblack.kotlinStones.StoneData
import pl.setblack.kotlinStones.StoneId
import pl.setblack.kstones.db.SequenceGenerator
import pl.setblack.kstones.dbModel.public_.tables.Stones
import pl.setblack.kstones.dbModel.public_.tables.records.StonesRecord


class StoneRepo(
    private  val ctx: JDBCBasedWebContextProvider,
    private val seq: SequenceGenerator<Web>) {


    fun readAllStones(): UANee<Web, List<Stone>> = Nee.constP(ctx.fx().tx) { jdbcProvider ->
        val dsl = DSL.using(jdbcProvider.getConnection().getResource())
        dsl.selectFrom(Stones.STONES)
            .fetchInto(StonesRecord::class.java)
            .toVavrList()
            .map {
                Stone(it.id, StoneData(it.name, it.color, it.size))
            }
    }.anyError()

    fun readStone() = Nee.pure(
            ctx.fx().cache then ctx.fx().tx) { jdbcProvider ->
        { id: StoneId ->
            val dsl = DSL.using(jdbcProvider.getConnection().getResource())
            val record = dsl.selectFrom(Stones.STONES)
                .where(Stones.STONES.ID.eq(id))
                .fetchOneInto(Stones.STONES)
            Stone(record.id, StoneData(record.name, record.color,  record.size))
        }
    }.anyError()

    fun addNewStone(newStone: StoneData)
            = seq.next().flatMap {
        addStone(it, newStone)
    }

    private fun addStone(stoneId: Long, newStone: StoneData) = Nee.constP(ctx.fx().tx) { jdbcProvider ->
        val dsl = DSL.using(jdbcProvider.getConnection().getResource())
        val insertedRows = dsl.insertInto(Stones.STONES)
            .values(stoneId, newStone.name, newStone.color, newStone.size)
            .execute()
        if (insertedRows == 1) {
            Option.some(stoneId as StoneId)
        } else {
            Option.none()
        }
    }
}
