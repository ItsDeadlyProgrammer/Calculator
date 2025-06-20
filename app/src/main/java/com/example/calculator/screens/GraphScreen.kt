package com.example.calculator.screens

import android.annotation.SuppressLint
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.navigation.NavHostController
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.PI


@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreen(expr: String, navController: NavHostController) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val points = remember(expr) {
        val expression = ExpressionBuilder(expr).variable("x").build()
        List(1000) { i ->
            val x = lerp(-10f, 10f, i / 999f.toFloat()).toDouble()
            expression.setVariable("x", x)
            val y = runCatching { expression.evaluate() }.getOrNull() ?: Double.NaN
            x to y
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Graph: y = $expr") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(0.2f, 20f)
                            offset += pan
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val centerX = canvasWidth / 2f + offset.x
                val centerY = canvasHeight / 2f + offset.y


                fun toCanvasX(x: Float): Float = centerX + x * scale * 40f
                fun toCanvasY(y: Float): Float = centerY - y * scale * 40f

                drawLine(color = Color.Gray, start = Offset(0f, centerY), end = Offset(canvasWidth, centerY), strokeWidth = 2f)
                drawLine(color = Color.Gray, start = Offset(centerX, 0f), end = Offset(centerX, canvasHeight), strokeWidth = 2f)

                val labelPaint = Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 36f
                    textAlign = Paint.Align.CENTER
                }

                val xPiLabels = listOf(
                    -PI.toFloat() to "-π",
                    0f to "0",
                    PI.toFloat() to "π",
                    (2 * PI).toFloat() to "2π"
                )

                xPiLabels.forEach { (xVal, label) ->
                    val xPos = toCanvasX(xVal)
                    drawContext.canvas.nativeCanvas.drawText(label, xPos, centerY + 30f, labelPaint)
                }

                val path = Path()
                points.forEachIndexed { i, (x, y) ->
                    if (y.isFinite()) {
                        val px = toCanvasX(x.toFloat())
                        val py = toCanvasY(y.toFloat())
                        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                    }
                }

                drawPath(
                    path = path,
                    color = Color.Cyan,
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}