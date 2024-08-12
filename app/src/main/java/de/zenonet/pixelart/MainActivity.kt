package de.zenonet.pixelart

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.tooling.preview.Preview
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
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun Editor(modifier: Modifier = Modifier) {
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
// This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
// This is previous motion event before next touch is saved into this current position
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
    var path by remember { mutableStateOf(Path()) }
    var bitmap by remember { mutableStateOf(ImageBitmap(64, 64)) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerMotionEvents(
                onDown = { pointerInputChange: PointerInputChange ->
                    currentPosition = pointerInputChange.position
                    motionEvent = MotionEvent.Down
                    pointerInputChange.consume()
                },
                onMove = { pointerInputChange: PointerInputChange ->
                    currentPosition = pointerInputChange.position
                    motionEvent = MotionEvent.Move
                    pointerInputChange.consume()
                },
                onUp = { pointerInputChange: PointerInputChange ->
                    motionEvent = MotionEvent.Up
                    pointerInputChange.consume()
                },
                delayAfterDownInMillis = 25L
            )
    ) {
        when (motionEvent) {
            MotionEvent.Down -> {
                if(previousPosition != Offset.Unspecified)
                path.moveTo(currentPosition.x, currentPosition.y)
                previousPosition = currentPosition
            }

            MotionEvent.Move -> {
                if(previousPosition != Offset.Unspecified)
                path.quadraticBezierTo(
                    previousPosition.x,
                    previousPosition.y,
                    (previousPosition.x + currentPosition.x) / 2,
                    (previousPosition.y + currentPosition.y) / 2

                )
                previousPosition = currentPosition
            }

            MotionEvent.Up -> {
                if(previousPosition != Offset.Unspecified)
                path.lineTo(currentPosition.x, currentPosition.y)
                currentPosition = Offset.Unspecified
                previousPosition = currentPosition
                motionEvent = MotionEvent.Idle
            }

            else -> Unit
        }

        drawPath(
            color = Color.Red,
            path = path,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PixelArtTheme {
        Editor()
    }
}