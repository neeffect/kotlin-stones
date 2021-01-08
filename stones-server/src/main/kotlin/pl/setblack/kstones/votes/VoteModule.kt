package pl.setblack.kstones.votes

import pl.setblack.kstones.infrastructure.InfrastuctureModule

open class VoteModule(private val infra: InfrastuctureModule) {
    open val votesRepo by lazy { VoteRepo(infra.context)}

    open val votesService by lazy {VoteService(infra.context,votesRepo)}

}
