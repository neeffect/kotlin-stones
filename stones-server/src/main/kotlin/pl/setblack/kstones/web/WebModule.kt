package pl.setblack.kstones.web

import dev.neeffect.nee.security.User
import dev.neeffect.nee.security.UserRole
import dev.neeffect.nee.security.jwt.JwtConfigurationModule
import pl.setblack.kstones.infrastructure.InfrastuctureModule
import pl.setblack.kstones.stones.StoneRest
import pl.setblack.kstones.stones.StonesModule
import pl.setblack.kstones.votes.VotesModule

open class WebModule(private val jwtConfigurationModule: JwtConfigurationModule<User, UserRole>) {
    open val infraModule by lazy {
        InfrastuctureModule(jwtConfigurationModule)
    }

    open val stonesModule by lazy {
        StonesModule(infraModule)
    }

    open val votesModule by lazy {
        VotesModule(infraModule)
    }

    open val stoneRest by lazy {
        StoneRest(infraModule.context,
        stonesModule.stoneService,
            votesModule.votesService) }
}
