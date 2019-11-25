package pl.setblack.kstones.ctx

import pl.setblack.nee.effects.cache.CacheEffect
import pl.setblack.nee.effects.cache.CacheProvider
import pl.setblack.nee.effects.cache.NaiveCacheProvider
import pl.setblack.nee.effects.jdbc.JDBCProvider
import pl.setblack.nee.effects.tx.TxConnection
import pl.setblack.nee.effects.tx.TxEffect
import pl.setblack.nee.effects.tx.TxProvider
import java.sql.Connection

//class WebaaContext(
//    val jdbcProvider: TxProvider<Connection, JDBCProvider>,
//    val cacheProvider: CacheProvider)
//    : TxProvider<Connection, WebContext>,
//    CacheProvider by cacheProvider {
//    override fun getConnection(): TxConnection<Connection>  = jdbcProvider.getConnection()
//
//    override fun setConnectionState(newState: TxConnection<Connection>): WebContext =
//        WebContext(jdbcProvider.setConnectionState(newState), cacheProvider)
//}
//
//object WebEffects {
//    val jdbc = TxEffect<Connection, WebContext>()
//    val cache = CacheEffect<WebContext, Nothing>(NaiveCacheProvider())
//}