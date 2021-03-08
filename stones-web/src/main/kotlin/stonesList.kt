import com.ccfraser.muirwik.components.MBadgeAnchorOriginHorizontal
import com.ccfraser.muirwik.components.MBadgeAnchorOriginVertical
import com.ccfraser.muirwik.components.MBadgeColor
import com.ccfraser.muirwik.components.MColor
import com.ccfraser.muirwik.components.anchorOriginHorizontal
import com.ccfraser.muirwik.components.anchorOriginVertical
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.card.mCard
import com.ccfraser.muirwik.components.card.mCardHeader
import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemIcon
import com.ccfraser.muirwik.components.list.mListItemSecondaryAction
import com.ccfraser.muirwik.components.list.mListItemText
import com.ccfraser.muirwik.components.mBadge
import com.ccfraser.muirwik.components.mContainer
import com.ccfraser.muirwik.components.mIcon
import com.ccfraser.muirwik.components.spacingUnits
import kotlinx.browser.window
import kotlinx.css.Color
import kotlinx.css.backgroundColor
import kotlinx.css.borderRadius
import kotlinx.css.bottom
import kotlinx.css.height
import kotlinx.css.left
import kotlinx.css.margin
import kotlinx.css.padding
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.css.width
import pl.setblack.kotlinStones.StoneData
import pl.setblack.kotlinStones.StoneWithVotes
import react.child
import react.functionalComponent
import react.useEffect
import react.useReducer
import react.useState
import services.fetchStones
import services.voteStone
import styled.css
import styled.styledDiv

@Suppress("MagicNumber")
val stonesList = functionalComponent<AppProps> { props ->
    val user = props.state.user
    val (stones, setStones) = useState(StonesState())

    val (counter, pushCounter) = useReducer({s:Int,_:Any-> s+1},1)
    useEffect(listOf(user, counter)) {
        fetchStones(user).then {
            setStones(stones.copy(stones = it))
        }
    }

    useEffect(emptyList()) {
        window.setInterval({ pushCounter(1) }, 10000)
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
                child(addStone, StoneProps(user, stones, setStones))
            }
        }
}

const val DEFAULT_STONE_SIZE = 5

data class StonesState(
    val stones: List<StoneWithVotes> = listOf(),
    val newData: StoneData = StoneData("", "", DEFAULT_STONE_SIZE)
)
