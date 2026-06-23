package com.indonesiaemas.note.ui.components


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import kotlin.math.ceil
import kotlin.math.sqrt

/**
 * Membungkus konten dengan watermark teks "Y-Tukang" yang diulang
 * secara diagonal di seluruh layar.
 *
 * @param text          Teks watermark (default "Y-Tukang")
 * @param color         Warna watermark
 * @param alpha         Transparansi 0f–1f (default 0.045f agar sangat subtle)
 * @param rotateDegrees Sudut rotasi teks (default -30f)
 * @param fontSize      Ukuran font watermark
 * @param content       Konten utama yang ditampilkan di atas watermark
 */
@Composable
fun WatermarkBackground(
    modifier: Modifier = Modifier,
    text: String = "Y-Tukang",
    color: Color = Color.Unspecified,
    alpha: Float = 0.045f,
    rotateDegrees: Float = -30f,
    fontSize: Float = 22f,
    content: @Composable BoxScope.() -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val resolvedColor = if (color == Color.Unspecified)
        MaterialTheme.colorScheme.onBackground
    else color

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // 1. CEK VALIDASI UKURAN
                // Jangan menggambar jika ukuran canvas belum siap (0)
                if (size.width <= 0f || size.height <= 0f) return@drawBehind

                val textStyle = TextStyle(
                    color = resolvedColor.copy(alpha = alpha),
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Bold
                )

                // 2. BERIKAN CONSTRAINTS PADA MEASURE
                val measured = textMeasurer.measure(
                    text = text,
                    style = textStyle,
                    // Pastikan maxWidth minimal 1 agar tidak memicu IllegalArgumentException
                    constraints = androidx.compose.ui.unit.Constraints(
                        maxWidth = size.width.toInt().coerceAtLeast(1)
                    )
                )

                val textWidth = measured.size.width.toFloat()
                val textHeight = measured.size.height.toFloat()

                // Hindari pembagian dengan nol jika teks kosong
                if (textWidth <= 0f || textHeight <= 0f) return@drawBehind

                val spacingX = textWidth * 2.2f
                val spacingY = textHeight * 3.5f

                val diagonal = sqrt(size.width * size.width + size.height * size.height)

                // 3. BATASI JUMLAH COLS/ROWS
                // Gunakan coerceAtMost untuk mencegah loop tak terhingga jika spacing sangat kecil
                val cols = (ceil(diagonal / spacingX).toInt() + 2).coerceAtMost(20)
                val rows = (ceil(diagonal / spacingY).toInt() + 2).coerceAtMost(50)

                for (row in -1..rows) {
                    for (col in -1..cols) {
                        val offsetX = col * spacingX + (row % 2) * (spacingX / 2)
                        val offsetY = row * spacingY

                        drawContext.canvas.save()

                        // Rotasi
                        drawContext.canvas.nativeCanvas.rotate(
                            rotateDegrees,
                            size.width / 2,
                            size.height / 2
                        )

                        // 4. GUNAKAN DRAWTEXT DENGAN HASIL MEASURE (LEBIH AMAN)
                        drawText(
                            textLayoutResult = measured, // Gunakan hasil measure yang sudah divalidasi
                            topLeft = androidx.compose.ui.geometry.Offset(
                                x = offsetX - (diagonal - size.width) / 2,
                                y = offsetY - (diagonal - size.height) / 2
                            )
                        )

                        drawContext.canvas.restore()
                    }
                }
            }
    ) {
        content()
    }
}