import com.ccfraser.muirwik.components.ReactHtmlElementAttributes
import com.ccfraser.muirwik.components.createStyled
import kotlinext.js.jsObject
import react.RComponent
import react.RState
import org.w3c.dom.events.Event
import react.RBuilder
import react.ReactElement
import react.createElement


interface ColorPickerProps : ReactHtmlElementAttributes {
    var name: String

    var defaultValue: String

    var value: String

    var onChange: ((String) -> Unit)?
}

@JsModule("material-ui-color-picker")
private external val colorPickerModule: dynamic

@Suppress("UnsafeCastFromDynamic")
val colorPickerComponent: RComponent<ColorPickerProps, RState> = colorPickerModule.default


fun RBuilder.mColorPicker(
    name: String,
    defaultValue: String,
    value: String = defaultValue,
    onChange: ((String) -> Unit)? = { value ->
        println(value)
    }
): ReactElement {
    val props: ColorPickerProps = jsObject()
    props.name = name
    props.defaultValue = defaultValue
    props.onChange = onChange
    props.value = value
    return child(createElement(colorPickerComponent, props, null))
}

