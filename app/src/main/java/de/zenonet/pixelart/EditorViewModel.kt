package de.zenonet.pixelart

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class EditorViewModel : ViewModel() {
    var bitmap by mutableStateOf(Bitmap.createBitmap(64, 64, Bitmap.Config.RGB_565))
    var selectedColor by mutableStateOf(Color.Red)
}