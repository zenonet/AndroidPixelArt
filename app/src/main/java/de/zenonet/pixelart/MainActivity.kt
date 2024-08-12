package de.zenonet.pixelart

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.gesture.MotionEvent
import com.smarttoolfactory.gesture.pointerMotionEvents
import de.zenonet.pixelart.ui.theme.PixelArtTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PixelArtTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Editor(
                        EditorViewModel(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun Editor(vm:EditorViewModel, modifier: Modifier = Modifier) {
    Column(modifier){
        ColorSelector(vm)
        Spacer(Modifier.height(2.dp))
        CanvasView(vm)
    }
}

@Composable
fun ColorSelector(vm:EditorViewModel, modifier: Modifier = Modifier) {
    val colors = arrayOf(Color.Green, Color.Red, Color.Cyan, Color.Blue, Color.Black, Color.White)
    Row(modifier.fillMaxWidth()){
        for (color in colors) {
            Button(onClick = { vm.selectedColor = color },
                Modifier
                    .fillMaxWidth()
                    .border((if (vm.selectedColor == color) 5.dp else 0.dp), Color.Black)
                    .weight(1f)
                    .background(color)
                    .alpha(0f)) {

            }
        }
    }
}
@Composable
fun CanvasView(vm:EditorViewModel, modifier: Modifier = Modifier) {
    var pressed by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio((vm.bitmap.width/vm.bitmap.height).toFloat())
            .background(Color.White)
            .pointerInput(Unit) {
                awaitEachGesture {
                    while (true) {
                        val event = awaitPointerEvent()
                        this
                        event.changes.forEach { pointerInputChange ->
                            currentPosition = pointerInputChange.position
                            pressed = pointerInputChange.pressed
                            pointerInputChange.consume()
                        }

                    }
                }

            }
    ) {
        val ratio = vm.bitmap.width / size.width

        drawImage(
            image = vm.bitmap.asImageBitmap(),
            dstSize = IntSize((size.width).toInt(), (vm.bitmap.width / ratio).toInt()),
            filterQuality = FilterQuality.None
        )

        if (pressed) {
            val x = (currentPosition.x * ratio).toInt()
            val y = (currentPosition.y * ratio).toInt()
            if (x >= vm.bitmap.width || y >= vm.bitmap.height) return@Canvas

            Log.i(
                "PIXELART",
                "up detected at x=${currentPosition.x}, y=${currentPosition.y}; pixel selected to change: x=$x, y=$y; bitmap size: x=${vm.bitmap.width}, y=${vm.bitmap.height}"
            )

            vm.bitmap.setPixel(
                x,
                y,
                vm.selectedColor.toArgb()
            )
            // Prevent recoloring on update of the viewmodel
            pressed = false
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PixelArtTheme {
        Editor(EditorViewModel())
    }
}