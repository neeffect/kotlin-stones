package pl.setblack.kstones

import java.math.BigDecimal

typealias  Price = BigDecimal
typealias  StoneId = Long

data class Stone(val id: StoneId, val data : StoneData)

data class StoneData(val name: String, val price: Price)

data class User(val name: String)

data class Stock(val stoneId: StoneId)
