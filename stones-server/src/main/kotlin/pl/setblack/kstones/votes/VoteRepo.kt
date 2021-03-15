package pl.setblack.kstones.votes

import dev.neeffect.nee.Nee
import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import io.vavr.control.Option
import org.jooq.impl.DSL
import org.jooq.impl.DSL.count
import org.ktorm.database.Database
import org.ktorm.database.Transaction
import org.ktorm.database.TransactionIsolation
import org.ktorm.database.TransactionManager
import org.ktorm.dsl.insert
import pl.setblack.kotlinStones.StoneId
import pl.setblack.kstones.db.DbSequence
import pl.setblack.kstones.db.SequenceGenerator
import pl.setblack.kstones.dbModel.public_.Sequences
import pl.setblack.kstones.dbModel.public_.tables.Votes
import pl.setblack.kstones.stones.Web
import java.lang.IllegalStateException
import java.sql.Connection


typealias VoterId = String

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class VoteId(val id: Long)

class VoteRepo(
    private val ctx: JDBCBasedWebContextProvider
) {
    private val txManager = NeeTransactionManager(ctx.jdbcProvider.getConnection().getResource())

    private val database = Database(txManager)

    private val seq: SequenceGenerator<Web> = DbSequence(ctx, Sequences.GLOBALVOTESSEQ)

    fun voteStone(stone: StoneId, voter: VoterId) =
        existsVote(stone, voter).flatMap { exists ->
            if (!exists) {
                seq.next().flatMap { voteId ->
                    addVote(VoteId(voteId), stone, voter)
                }
            } else {
                Nee.pure(Option.none())
            }
        }

    fun calcVotes(stone: StoneId) = Nee.with(ctx.fx().tx) { jdbcProvider ->
        DSL.using(jdbcProvider.getConnection().getResource()).let { dsl ->
            dsl.select(count()).from(Votes.VOTES).where(
                Votes.VOTES.STONE_ID.eq(stone)
            ).fetchSingle(0) as Int
        }
    }

    private fun addVote(id: VoteId, stone: StoneId, voter: VoterId) = Nee.with(ctx.fx().tx) { jdbcProvider ->
        txManager.withConnection(jdbcProvider.getConnection().getResource())
        val insertedRows = database.insert(VoteDB) {
            set(it.id, id.id)
            set(it.stoneId, stone)
            set(it.voter, voter)
        }
        if (insertedRows == 1) {
            Option.some(id)
        } else {
            Option.none()
        }
    }

    private fun existsVote(stone: StoneId, voter: VoterId) = Nee.with(ctx.fx().tx) { jdbcProvider ->
        DSL.using(jdbcProvider.getConnection().getResource()).let { dsl ->
            val counted = dsl.select(count()).from(Votes.VOTES)
                .where(Votes.VOTES.STONE_ID.eq(stone).and(Votes.VOTES.VOTER.eq(voter)))
                .fetchSingle(0) as Int
            counted > 0
        }
    }
}

class NeeTransactionManager(initialConnection: Connection) : TransactionManager {
    private var connection: Connection? = InitialConnection(initialConnection, this)
    override val currentTransaction: Transaction? = null
    override val defaultIsolation: TransactionIsolation? = null


    override fun newConnection(): Connection =
        this.connection?.let {
            WrappedConnection(it, this)
        } ?: throw  IllegalStateException()

    override fun newTransaction(isolation: TransactionIsolation?): Transaction = TODO()

    internal fun forgetConnection(): Unit = run {
        connection = null
    }


    internal fun withConnection(conn: Connection) =
        if (connection != null) {
            throw IllegalStateException()
        } else {
            this.connection = WrappedConnection(conn, this)
        }

    class WrappedConnection(
        private var conn: Connection,
        private var txManager: NeeTransactionManager) : Connection by conn {
        override fun close() {
            txManager.forgetConnection()
        }
    }

    class InitialConnection(
        private var conn: Connection,
        private var txManager: NeeTransactionManager) : Connection by conn {
        override fun close() {
            conn.close()
            txManager.forgetConnection()
        }
    }
}
