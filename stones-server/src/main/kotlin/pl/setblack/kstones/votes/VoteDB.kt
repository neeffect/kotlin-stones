package pl.setblack.kstones.votes

import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object VoteDB : Table<Nothing>("votes") {
    val id = long("id").primaryKey()
    val stoneId  = long("stone_id")
    val voter = varchar("voter")
}
