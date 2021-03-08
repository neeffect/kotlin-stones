import com.ccfraser.muirwik.components.ReactHtmlElementAttributes
import kotlinext.js.jsObject
import react.RBuilder
import react.RComponent
import react.RState
import react.ReactElement
import react.createElement

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

interface ColorPickerProps : ReactHtmlElementAttributes {
    var name: String

    var defaultValue: String

    var value: String

    var onChange: ((String) -> Unit)?
}
