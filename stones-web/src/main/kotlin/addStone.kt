import com.ccfraser.muirwik.components.MColor
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.card.mCard
import com.ccfraser.muirwik.components.card.mCardActions
import com.ccfraser.muirwik.components.card.mCardContent
import com.ccfraser.muirwik.components.card.mCardHeader
import com.ccfraser.muirwik.components.mTextField
import com.ccfraser.muirwik.components.targetInputValue
import pl.setblack.kotlinStones.StoneData
import react.RProps
import react.functionalComponent
import services.addStone
import services.fetchStones
import styled.css
import styled.styledDiv

data class StoneProps(
    val user: User,
    val stonesState: StonesState,
    val setStones: (StonesState) -> Unit
) : RProps

val addStone = functionalComponent<StoneProps> { props ->
    val user = props.user
    val stones = props.stonesState
    val setStones = props.setStones
    mCard(raised = false) {
        css {
            +StonesStyles.overflowCard
        }
        mCardHeader(title = "add new stone") {

        }
        mCardContent {
            mTextField(label = "Name", value = stones.newData.name, onChange = { event ->
                setStones(stones.copy(newData = stones.newData.copy(name = event.targetInputValue)))
            }) {
            }
            styledDiv {
                css {
                    +StonesStyles.colorPickerWrapper
                }
                mColorPicker(name = "Color",
                    defaultValue ="select color",
                    value = stones.newData.color,
                    onChange = { value ->
                        println(value)

                        setStones(stones.copy(newData = stones.newData.copy(color = value)))
                    }
                )
            }

            mTextField(label = "Size", value = stones.newData.size.toString(), onChange = { event ->
                setStones(stones.copy(newData = stones.newData.copy(size = event.targetInputValue.toInt())))
            }) {
            }
        }


        mCardActions {
            mButton("add stone", MColor.primary, disabled = !stones.newData.valid(), variant = MButtonVariant.contained, onClick = { _ ->
                addStone(stones.newData, user)
                    .then {
                        fetchStones(user).then {
                            setStones(stones.copy(stones = it, newData = StoneData()))
                        }
                    }
            }) {

            }
        }
    }
}
