package pl.setblack.kstones.oauth

import dev.neeffect.nee.ctx.web.oauth.OauthSupportApi
import dev.neeffect.nee.security.UserRole
import dev.neeffect.nee.security.jwt.JwtConfig
import dev.neeffect.nee.security.oauth.*
import dev.neeffect.nee.security.oauth.config.OauthModule
import io.vavr.collection.Seq
import io.vavr.kotlin.list
import pl.setblack.kstones.infrastructure.InfrastuctureModule

class StonesOauthModule(
    internal val oauthModule: OauthModule
)  {

    val oauthApi by lazy {
        OauthSupportApi(oauthModule.oauthService)
    }

}
