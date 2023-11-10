import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.MouseInfo
import java.awt.Point
import java.awt.event.MouseEvent


@OptIn(ExperimentalTextApi::class, ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App() {
    var xMin by remember { mutableStateOf(-10) }
    var yMin by remember { mutableStateOf(0) }
    var xMax by remember { mutableStateOf( 10) }
    var yMinMax by remember{mutableStateOf(0f)}
    var selectY by remember{mutableStateOf(true)}
    val textMeasurer = rememberTextMeasurer()
    var points by remember { mutableStateOf(mutableMapOf<Float, Float>()) }
    Canvas(modifier = Modifier.fillMaxSize().clickable{}.
        onPointerEvent(PointerEventType.Press){
            var point = it.changes.first().position
            points[point.x] = point.y
        },
        onDraw = {
            var yMax = this.size.height*(xMax-xMin)/this.size.width+yMin
            if(yMax*yMin<0){
                yMinMax = ((xMax-xMin)/(xMax+xMin)).toFloat()
            }
            //ось OX
            drawLine(
                color = Color.Black,
                start = Offset(0f, this.size.height * (1 + yMinMax) / 2),
                end = Offset(this.size.width, this.size.height * (1 + yMinMax) / 2)
            )
            //ось OY
            drawLine(color = Color.Black,
                start = Offset(-this.size.width*xMin/(xMax-xMin), 0f),
                end = Offset(-this.size.width*xMin/(xMax-xMin), this.size.height))
            for(point in points) {
                drawCircle(
                    color = Color.Green,
                    radius = 10f,
                    center = Offset(point.key, point.value)
                )
            }
            for(i in xMin .. xMax) {
                drawLine(color = Color.Black,
                    start = Offset(this.size.width*(i-xMin)/(xMax-xMin),
                        this.size.height*(1+yMinMax)/2-5),
                    end = Offset(this.size.width*(i-xMin)/(xMax-xMin),
                        this.size.height*(1+yMinMax)/2+5))
                drawText(textMeasurer = textMeasurer, text = i.toString(),
                    topLeft = Offset(this.size.width*(i-xMin)/(xMax-xMin)-5,
                        this.size.height*(1+yMinMax)/2))
            }
        })

    Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Bottom) {
        Row(modifier = Modifier.padding(10.dp, 10.dp)){
            Column(modifier = Modifier.padding(10.dp, 10.dp)){
                Text("xMin")
                TextField(value = xMin.toString(),
                    onValueChange = { value -> xMin = value.toIntOrNull() ?:-10 })
            }
            Column(modifier = Modifier.padding(10.dp, 10.dp)) {
                Text("xMax")
                TextField(value = xMax.toString(),
                    onValueChange = { value -> xMax = value.toIntOrNull() ?: 0 })
            }
            Column{
                Row{
                    RadioButton(
                        selected = selectY,
                        onClick = { selectY = true },
                        modifier = Modifier.padding(8.dp)
                    )
                    Text("yMin", fontSize = 22.sp)
                }
                Row{
                    RadioButton(
                        selected = !selectY,
                        onClick = { selectY = false },
                        //modifier = Modifier.padding(8.dp)
                    )
                    Text("ySlider", fontSize = 22.sp)
                }
            }
            Column(modifier = Modifier.padding(10.dp, 10.dp)) {
                //Text(text = "${yMinMax}", fontSize = 10.sp)
                if(!selectY) {
                    Slider(value = yMinMax,
                        valueRange = -1f..1f,
                        steps = 9,
                        onValueChange = { yMinMax = it })
                }
                else{
                    TextField(value = xMin.toString(),
                        onValueChange = { value -> yMin = value.toIntOrNull() ?: 0 })
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
