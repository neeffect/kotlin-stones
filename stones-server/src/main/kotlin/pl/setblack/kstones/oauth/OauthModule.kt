package pl.setblack.kstones.oauth

import dev.neeffect.nee.ctx.web.oauth.OauthSupportApi
import dev.neeffect.nee.security.UserRole
import dev.neeffect.nee.security.jwt.JwtConfig
import dev.neeffect.nee.security.oauth.*
import io.vavr.collection.Seq
import io.vavr.kotlin.list
import pl.setblack.kstones.stones.StonesModule

class OauthModule(
    oathConfig: OauthConfig,
    jwtConfig: JwtConfig
)  : SimpleOauthConfigModule(oathConfig, jwtConfig) {

    val oauthService by lazy {
        OauthService(this)
    }
    val oauthApi by lazy {
        OauthSupportApi(oauthService)
    }

    override val userRoles: (OauthProviderName, OauthResponse) -> Seq<UserRole> = { _,_->
        list(StonesModule.SecurityRoles.writer)
    }
}
