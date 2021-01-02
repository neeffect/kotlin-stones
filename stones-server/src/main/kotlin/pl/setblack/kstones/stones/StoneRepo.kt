package pl.setblack.kstones.stones


import dev.neeffect.nee.Nee
import dev.neeffect.nee.ANee
import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import dev.neeffect.nee.plus
import io.vavr.collection.List
import io.vavr.control.Option
import io.vavr.kotlin.toVavrList
import org.jooq.impl.DSL
import org.jooq.impl.DSL.count
import pl.setblack.kotlinStones.Stone
import pl.setblack.kotlinStones.StoneData
import pl.setblack.kotlinStones.StoneId
import pl.setblack.kotlinStones.StoneWithVotes
import pl.setblack.kstones.db.SequenceGenerator
import pl.setblack.kstones.dbModel.public_.tables.Stones
import pl.setblack.kstones.dbModel.public_.tables.Votes
import pl.setblack.kstones.dbModel.public_.tables.records.StonesRecord


class StoneRepo(
    private  val ctx: JDBCBasedWebContextProvider,
    private val seq: SequenceGenerator<Web>) {

    private val stonesCache = ctx.fx().cache()

    fun readAllStones() = Nee.with(ctx.fx().tx) { jdbcProvider ->
        val dsl = DSL.using(jdbcProvider.getConnection().getResource())
        dsl.select(
            Stones.STONES.ID,
            Stones.STONES.NAME,
            Stones.STONES.COLOR,
            Stones.STONES.SIZE,
            count(Votes.VOTES.ID)).from(Stones.STONES)
            .leftOuterJoin(Votes.VOTES)
            .on(Stones.STONES.ID.eq(Votes.VOTES.STONE_ID))
            .groupBy(Stones.STONES.ID)
            .fetch {record  ->
                StoneWithVotes(
                    Stone(
                        record[0] as Long,
                        StoneData(record[1] as String, record[2] as String, record[3] as Int)),
                    record[4] as Int)
            }
            .toVavrList()

    }

    fun readStone(id:StoneId) = Nee.with(
            stonesCache.of(id) + ctx.fx().tx) { jdbcProvider ->
            val dsl = DSL.using(jdbcProvider.getConnection().getResource())
            val record = dsl.selectFrom(Stones.STONES)
                .where(Stones.STONES.ID.eq(id))
                .fetchOneInto(Stones.STONES)
            Stone(record.id, StoneData(record.name, record.color,  record.size))
    }

    fun addNewStone(newStone: StoneData)
            = seq.next().flatMap {
        addStone(it, newStone)
    }

    private fun addStone(stoneId: Long, newStone: StoneData) = Nee.with(ctx.fx().tx) { jdbcProvider ->
        val dsl = DSL.using(jdbcProvider.getConnection().getResource())
        val insertedRows = dsl.insertInto(Stones.STONES)
            .values(stoneId, newStone.name, newStone.color, newStone.size)
            .execute()
        if (insertedRows == 1) {
            Option.some(stoneId)
        } else {
            Option.none()
        }
    }
}
