import react.child
import react.functionalComponent
import styled.styledDiv

val stones = functionalComponent<AppProps> { props ->
    styledDiv {
        child(stonesList, props)
    }

}
