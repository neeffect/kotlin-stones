package pl.setblack.kstones.votes

import pl.setblack.kstones.infrastructure.InfrastuctureModule

open class VotesModule(private val infra: InfrastuctureModule) {
    open val votesRepo by lazy { VotesRepo(infra.context)}

    open val votesService by lazy {VoteService(infra.context,votesRepo)}

}
