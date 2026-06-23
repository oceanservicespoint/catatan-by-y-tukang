package com.indonesiaemas.note.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indonesiaemas.note.ui.components.WatermarkBackground
import com.indonesiaemas.note.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    viewModel: NoteViewModel? = null,
    onNoteClick: (Int) -> Unit = {}, // Parameter ini sekarang digunakan untuk trigger Email
    onNavigateBack: () -> Unit = {}
) {
    val totalNotes: Int
    val totalArchived: Int

    if (viewModel != null) {
        val uiState by viewModel.uiState.collectAsState()
        totalNotes = uiState.notes.size
        totalArchived = uiState.archivedNotes.size
    } else {
        totalNotes = 0
        totalArchived = 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tentang Aplikasi") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        WatermarkBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Aplikasi Catatan",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Powered by Ocean Services Point",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )

                Text(text = "Versi 1.0.1", fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$totalNotes", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(text = "Catatan", fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$totalArchived", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(text = "Arsip", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "Dalam waktu dekat kami akan mempublish Aplikasi untuk para Freelancers(Pekerja Serabutan) dan Profesional di Indonesia, tidak peduli apapun Pendidikanmu yang penting punya keahlian maka anda berhak mendapatkan Pekerjaan...",
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "Aplikasi Catatan adalah solusi untuk menjaga keamanan catatan anda",
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(20.dp))

                // Bagian yang bisa diklik untuk mengirim email
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNoteClick(0) } // Memanggil fungsi Intent di MainActivity
                        .padding(16.dp), // Memberi ruang klik yang lebih luas
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Developed by Ocean Services Point",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "© 2025",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "oceanservicespoint@gmail.com",
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AboutScreenPreview() {
    AboutScreen()
}