package pl.setblack.kotlinStones
import org.gciatto.kt.math.BigDecimal

data class Stone(val id: Long, val data: StoneData)

data class StoneData(val name: String, val price: BigDecimal)
