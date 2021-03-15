package pl.setblack.kstones.oauth

import dev.neeffect.nee.ctx.web.oauth.OauthSupportApi
import dev.neeffect.nee.security.oauth.config.OauthModule

class StonesOauthModule(
    internal val oauthModule: OauthModule
) {

    val oauthApi by lazy {
        OauthSupportApi(oauthModule.oauthService)
    }
}
