package pl.setblack.kstones.stones

import dev.neeffect.nee.Nee
import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import dev.neeffect.nee.plus
import io.vavr.control.Option
import io.vavr.kotlin.none
import io.vavr.kotlin.toVavrList
import org.jooq.SelectField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.count
import pl.setblack.kotlinStones.Stone
import pl.setblack.kotlinStones.StoneData
import pl.setblack.kotlinStones.StoneId
import pl.setblack.kotlinStones.StoneWithVotes
import pl.setblack.kstones.db.SequenceGenerator
import pl.setblack.kstones.dbModel.public_.tables.Stones
import pl.setblack.kstones.dbModel.public_.tables.Votes

class StoneRepo(
    private  val ctx: JDBCBasedWebContextProvider,
    private val seq: SequenceGenerator<Web>) {

    private val stonesCache = ctx.fx().cache()

    @Suppress("MagicNumber")
    fun readAllStones(votesOf:Option<String> = none()) = Nee.with(ctx.fx().tx) { jdbcProvider ->
        val dsl = DSL.using(jdbcProvider.getConnection().getResource())
        val v1 = Votes("v1")
        val v2 = Votes("v2")

        val whoVotedCondition = votesOf.map { name ->
            v2.VOTER.eq(name)
        }.getOrElse(DSL.falseCondition())
        val voterSelect = votesOf.map {v2.VOTER as SelectField<String> }.
            getOrElse(DSL.inline(null, v2.VOTER))
        dsl.select(
            Stones.STONES.ID,
            Stones.STONES.NAME,
            Stones.STONES.COLOR,
            Stones.STONES.SIZE,
            count(v1.ID),
            voterSelect
            ).from(Stones.STONES)
            .leftOuterJoin(v1)
            .on(Stones.STONES.ID.eq(v1.STONE_ID))
            .leftOuterJoin(v2)
            .on( Stones.STONES.ID.eq(v2.STONE_ID).and(whoVotedCondition))
            .groupBy(Stones.STONES.ID)
            .fetch {record  ->
                StoneWithVotes(
                    Stone(
                        record[0] as Long,
                        StoneData(record[1] as String, record[2] as String, record[3] as Int)),
                    record[4] as Int,
                    record[5] != null
                )
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
