package com.indonesiaemas.note.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indonesiaemas.note.data.entity.Note
import com.indonesiaemas.note.ui.components.WatermarkBackground
import com.indonesiaemas.note.ui.theme.noteColors
import com.indonesiaemas.note.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    viewModel: NoteViewModel,
    noteId: Int,
    onNavigateBack: () -> Unit,
    onEditNote: (Int) -> Unit
) {
    var note by remember { mutableStateOf<Note?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        note = viewModel.getNoteById(noteId)
    }

    // Observe changes
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.notes) {
        note = uiState.notes.find { it.id == noteId } ?: note
    }

    val bgColor = noteColors.getOrElse(note?.color ?: 0) { noteColors[0] }
    WatermarkBackground {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    note?.let { n ->
                        IconButton(onClick = { viewModel.togglePin(n) }) {
                            Icon(
                                if (n.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                contentDescription = "Pin",
                                tint = if (n.isPinned) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }
                        IconButton(onClick = { viewModel.toggleArchive(n); onNavigateBack() }) {
                            Icon(Icons.Outlined.Archive, contentDescription = "Archive")
                        }
                        IconButton(onClick = { onEditNote(n.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Hapus",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
        containerColor = bgColor
    ) { padding ->

            note?.let { n ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Category & date row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = { },
                            label = { Text(n.category, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.Label,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                        Text(
                            text = formatDetailDate(n.updatedAt),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    if (n.title.isNotBlank()) {
                        Text(
                            text = n.title,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C1B1F),
                            lineHeight = 34.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Divider
                    if (n.title.isNotBlank() && n.content.isNotBlank()) {
                        HorizontalDivider(color = Color.Black.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Content
                    if (n.content.isNotBlank()) {
                        Text(
                            text = n.content,
                            fontSize = 16.sp,
                            color = Color(0xFF49454F),
                            lineHeight = 26.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Created at
                    Text(
                        text = "Dibuat ${formatDetailDate(n.createdAt)}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Hapus Catatan") },
            text = { Text("Apakah Anda yakin ingin menghapus catatan ini secara permanen?") },
            confirmButton = {
                Button(
                    onClick = {
                        note?.let { viewModel.deleteNote(it) }
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

fun formatDetailDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMMM dd, yyyy • hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}