import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.card.mCard
import com.ccfraser.muirwik.components.card.mCardActions
import com.ccfraser.muirwik.components.card.mCardContent
import com.ccfraser.muirwik.components.card.mCardHeader
import com.ccfraser.muirwik.components.list.*
import kotlinx.css.*
import pl.setblack.kotlinStones.StoneData
import pl.setblack.kotlinStones.StoneWithVotes
import react.functionalComponent
import react.useEffect
import react.useState
import services.addStone
import services.fetchStones
import services.voteStone
import styled.css
import styled.styledDiv

data class StonesState(
    val stones: List<StoneWithVotes> = listOf(),
    val newData: StoneData = StoneData("", "", 5)
)

val stonesList = functionalComponent<AppProps> { props ->
    val user = props.state.user
    val (stones, setStones) = useState(StonesState())

    useEffect(listOf(user)) {
        fetchStones(user).then {
            setStones(stones.copy(stones = it))
        }
    }


        mContainer {
            mCard {
                mCardHeader(title = "existing stones : ${user?.login}") {

                }
                mList {

                    for (stone in stones.stones) {
                        mListItem {
                            mListItemIcon {
                                mIcon("brightness_5")
                            }

                            styledDiv {
                                css {
                                    width = 100.px
                                }
                                styledDiv {
                                    css {
                                        val size = (stone.stone.data.size * 3).px
                                        borderRadius = 50.pct
                                        backgroundColor = Color(stone.stone.data.color)
                                        width = size
                                        height = size
                                    }
                                    +" "
                                }
                            }

                            mListItemText {
                                +stone.stone.data.name
                            }
                            mListItemSecondaryAction {
                                mBadge(stone.votes, color = MBadgeColor.secondary) {
                                    //css(ComponentStyles.margin)
                                    attrs.anchorOriginHorizontal = MBadgeAnchorOriginHorizontal.right
                                    attrs.anchorOriginVertical = MBadgeAnchorOriginVertical.top
                                    css {
                                        margin(2.spacingUnits)
                                        padding(0.px, 2.spacingUnits)
                                    }
                                    if (user != null) {
                                        mIconButton(
                                            "thumb_up",
                                            onClick = {
                                                if (!stone.myVote) {
                                                    voteStone(stone.stone.id, user).then {
                                                        val newStones: List<StoneWithVotes> =
                                                            stones.stones.map { aStone: StoneWithVotes ->
                                                                if (aStone.stone.id == stone.stone.id) {
                                                                    aStone.upVoted()

                                                                } else {
                                                                    aStone
                                                                }
                                                            }
                                                        setStones(stones.copy(stones = newStones))
                                                    }
                                                }
                                            },
                                            color = if (stone.myVote) MColor.primary else MColor.secondary,
                                            disabled = stone.myVote
                                        ) {
                                            css {
                                                left = 28.px
                                                bottom = 14.px
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (user != null) {
                mCard(raised = false) {
                    mCardHeader(title = "add new stone") {

                    }
                    mCardContent {
                        mTextField(label = "Name", value = stones.newData.name, onChange = { event ->
                            setStones(stones.copy(newData = stones.newData.copy(name = event.targetInputValue)))
                        }) {
                        }

                        mTextField(label = "Color", value = stones.newData.color, onChange = { event ->
                            setStones(stones.copy(newData = stones.newData.copy(color = event.targetInputValue)))
                        }) {
                        }

                        mTextField(label = "Size", value = stones.newData.size.toString(), onChange = { event ->
                            setStones(stones.copy(newData = stones.newData.copy(size = event.targetInputValue.toInt())))
                        }) {
                        }
                    }


                    mCardActions {
                        mButton("add stone", MColor.primary, variant = MButtonVariant.contained, onClick = { _ ->
                            if (user != null) {
                                addStone(stones.newData, user)
                                    .then {
                                        fetchStones(user).then {
                                            setStones(stones.copy(stones = it))
                                        }
                                    }
                            }

                        }) {

                        }
                    }
                }
            }
        }

}

