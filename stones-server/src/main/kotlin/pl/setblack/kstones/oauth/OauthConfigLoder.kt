package pl.setblack.kstones.oauth

import com.sksamuel.hoplite.*
import com.sksamuel.hoplite.decoder.Decoder
import com.sksamuel.hoplite.decoder.MapDecoder
import com.sksamuel.hoplite.fp.Validated
import com.sksamuel.hoplite.fp.flatMap
import com.sksamuel.hoplite.fp.invalid
import com.sksamuel.hoplite.fp.sequence
import dev.neeffect.nee.security.jwt.JwtConfig
import dev.neeffect.nee.security.oauth.OauthConfig
import io.vavr.collection.Map
import io.vavr.control.Either
import io.vavr.kotlin.toVavrMap
import java.nio.file.Path
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

class OauthConfigLoder(private val configPath: Path) {
    fun loadOauthConfig() : Either<ConfigError,OauthConfig> =   ConfigLoader.Builder()
        .addSource(PropertySource.path(configPath.resolve("oauthConfig.yml")))
        .addDecoder(VMapDecoder())
        .build()
        .loadConfig<OauthConfig>()
        .foldi ({error ->
            Either.left(ConfigError(error.description()))}, {cfg ->
            Either.right(cfg)})

    fun loadJwtConfig() : Either<ConfigError, JwtConfig> =
        ConfigLoader.Builder()
            .addSource(PropertySource.path(configPath.resolve("jwtConfig.yml")))
            .build()
            .loadConfig<JwtConfig>()
            .foldi ({error ->
                Either.left(ConfigError(error.description()))}, {cfg ->
                Either.right(cfg)})
}


data class ConfigError(val msg:String)

//TODO  -  report as problem
inline fun <A, E, T> Validated<E, A>.foldi(ifInvalid: (E) -> T, ifValid: (A) -> T): T = when (this) {
    is Validated.Invalid -> ifInvalid(error)
    is Validated.Valid -> ifValid(value)
}


internal class VMapDecoder : Decoder<Map<*,*>> {
    private val hMapDecoder = MapDecoder()
    override fun decode(node: Node, type: KType, context: DecoderContext): ConfigResult<Map<*, *>> =
        hMapDecoder.decode(node, type, context).map { kotlinMap ->
            kotlinMap.toVavrMap()
        }

    override fun supports(type: KType): Boolean =
        type.isSubtypeOf(Map::class.starProjectedType)

}
