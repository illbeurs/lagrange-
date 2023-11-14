import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@OptIn(ExperimentalTextApi::class, ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App() {
    var xMin by remember { mutableStateOf(-10) }
    val yMin by remember { mutableStateOf(0) }
    var xMax by remember { mutableStateOf( 10) }
    var yMinMax by remember{mutableStateOf(0f)}
    var selectY by remember{mutableStateOf(true)}
    var build by remember{mutableStateOf(false)}
    var y0 by remember{mutableStateOf(0f)}
    val textMeasurer = rememberTextMeasurer()
    val polypoints by  remember{mutableStateOf(mutableListOf<Offset>())}
    val points by remember { mutableStateOf(mutableListOf<Pair<Float, Float>>())}
    var can = Canvas(modifier = Modifier.fillMaxSize().clickable{}.
        onPointerEvent(PointerEventType.Press){
            polypoints.clear()
            var point = it.changes.first().position
            points.add(Pair(point.x, point.y))
        },
        onDraw = {
            var yMax = this.size.height*(xMax-xMin)/this.size.width+yMin
            //ось OX
            drawLine(
                color = Color.Black,
                start = Offset(0f, this.size.height * (1 + yMinMax) / 2),
                end = Offset(this.size.width, this.size.height * (1 + yMinMax) / 2)
            )
            y0 = this.size.height * (1 + yMinMax) / 2
            //ось OY
            drawLine(color = Color.Black,
                start = Offset(-this.size.width*xMin/(xMax-xMin), 0f),
                end = Offset(-this.size.width*xMin/(xMax-xMin), this.size.height))
            for((x,y) in points) {
                drawCircle(
                    color = Color.Green,
                    radius = 10f,
                    center = Offset(x, y)
                )
            }
            for(i in xMin .. xMax) {
                drawLine(color = Color.Black,
                    start = Offset(this.size.width*(i-xMin)/(xMax-xMin),
                        this.size.height*(1+yMinMax)/2-5),
                    end = Offset(this.size.width*(i-xMin)/(xMax-xMin),
                        this.size.height*(1+yMinMax)/2+5))
                drawText(textMeasurer = textMeasurer, text = i.toString(),
                    topLeft = Offset(this.size.width*(i-xMin)/(xMax-xMin)-7,
                        this.size.height*(1+yMinMax)/2+6))
            }
            if (build){
                for (x in 0..size.width.toInt()) {
                    val y = BuildPoly(x.toFloat(),points)
                    polypoints.add(Offset(x.toFloat(), y))
                }
                drawPoints(
                    points = polypoints,
                    strokeWidth = 3f,
                    pointMode = PointMode.Points,
                    color = Color.Blue
                )
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
                    onValueChange = { value -> xMax = value.toIntOrNull() ?: 10 })
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
                    TextField(value = yMinMax.toString(),
                        onValueChange = { value -> yMinMax = value.toFloatOrNull() ?: 0f })
                }
            }
        }
    }
    Box(modifier = Modifier.padding(50.dp, 50.dp)) {
        // ... Ваш существующий код

        // Создаем кнопку
        val button = Button(
            onClick = {
                build = true
            },
            modifier = Modifier,
            contentPadding = PaddingValues(horizontal = 50.dp, vertical = 20.dp)
        ) {
            Text("Build")
        }

        // Размещаем кнопку внутри Column
        button
    }
    Box(modifier = Modifier.padding(300.dp, 50.dp)) {
        // ... Ваш существующий код

        // Создаем кнопку
        val button = Button(
            onClick = {
                      build = false
                      points.clear()
                      polypoints.clear()
            },
            modifier = Modifier,
            contentPadding = PaddingValues(horizontal = 50.dp, vertical = 20.dp)
        ) {
            Text("Clear")
        }

        // Размещаем кнопку внутри Column
        button
    }

}

fun lk(x:Float,xk:Float,points: MutableList<Pair<Float, Float>>):Float{
    var l = 1f;
    for ((xi,y) in points){
        if (xi==xk){continue}
        l*=(x-xi)/(xk-xi)
    }
    return l
}

fun BuildPoly(x:Float, points: MutableList<Pair<Float, Float>>):Float{
    var summ: Float = 0f
    for ((xk,yk) in points) {
        summ+= yk*lk(x,xk,points)
    }
    return summ
}
fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
