package com.indonesiaemas.note.ui.screens


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indonesiaemas.note.ui.components.WatermarkBackground

/**
 * Status yang mungkin muncul di LockScreen.
 */
enum class LockStatus {
    IDLE,       // Belum mencoba
    WAITING,    // Menunggu input sidik jari
    SUCCESS,    // Berhasil
    FAILED,     // Sidik jari tidak cocok
    ERROR       // Error lain (hardware, dll)
}

@Composable
fun LockScreen(
    lockStatus: LockStatus,
    errorMessage: String = "",
    onTryAgain: () -> Unit
) {
    // Animasi pulse pada ikon saat WAITING
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val isWaiting = lockStatus == LockStatus.WAITING

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1625), Color(0xFF2D1B4E))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        WatermarkBackground(
            color = Color.White,
            alpha = 0.04f,
            fontSize = 20f
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(40.dp)
                ) {
                    // Icon lock di atas
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = Color(0xFFD0BCFF).copy(alpha = 0.6f),
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    // Nama app
                    Text(
                        text = "Aplikasi Catatan",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Catatan Anda terlindungi",
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    // Tombol / ikon sidik jari
                    Box(
                        modifier = Modifier
                            .size(88.dp) // 1. Tetapkan ukuran tetap (Fixed Size)
                            .graphicsLayer { // 2. Gunakan graphicsLayer untuk animasi
                                if (isWaiting) {
                                    scaleX = pulseScale
                                    scaleY = pulseScale
                                }
                            }
                            .clip(CircleShape)
                            .background(
                                when (lockStatus) {
                                    LockStatus.SUCCESS -> Color(0xFF4CAF50).copy(alpha = 0.25f)
                                    LockStatus.FAILED, LockStatus.ERROR -> Color(0xFFE53935).copy(alpha = 0.25f)
                                    else -> Color(0xFF6750A4).copy(alpha = 0.25f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Fingerprint,
                            contentDescription = "Fingerprint",
                            tint = when (lockStatus) {
                                LockStatus.SUCCESS -> Color(0xFF81C784)
                                LockStatus.FAILED, LockStatus.ERROR -> Color(0xFFEF9A9A)
                                else -> Color(0xFFD0BCFF)
                            },
                            modifier = Modifier.size(52.dp)
                        )
                    }

                    // Status teks
                    AnimatedContent(
                        targetState = lockStatus,
                        transitionSpec = {
                            fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                        },
                        label = "statusText"
                    ) { status ->
                        Text(
                            text = when (status) {
                                LockStatus.IDLE -> "Ketuk untuk membuka"
                                LockStatus.WAITING -> "Tempelkan jari Anda..."
                                LockStatus.SUCCESS -> "Berhasil dibuka!"
                                LockStatus.FAILED -> "Sidik jari tidak cocok"
                                LockStatus.ERROR -> errorMessage.ifBlank { "Autentikasi gagal" }
                            },
                            fontSize = 15.sp,
                            color = when (status) {
                                LockStatus.SUCCESS -> Color(0xFF81C784)
                                LockStatus.FAILED, LockStatus.ERROR -> Color(0xFFEF9A9A)
                                else -> Color.White.copy(alpha = 0.7f)
                            },
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Tombol aksi
                    when (lockStatus) {
                        LockStatus.IDLE, LockStatus.FAILED, LockStatus.ERROR -> {
                            Button(
                                onClick = onTryAgain,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6750A4),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Fingerprint,
                                    null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (lockStatus == LockStatus.IDLE) "Buka dengan Sidik Jari"
                                    else "Coba Lagi",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        LockStatus.WAITING -> {
                            OutlinedButton(
                                onClick = onTryAgain,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White.copy(
                                        alpha = 0.6f
                                    )
                                ),
                                modifier = Modifier.fillMaxWidth().height(52.dp)
                            ) {
                                Text("Menunggu...", fontSize = 15.sp)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}