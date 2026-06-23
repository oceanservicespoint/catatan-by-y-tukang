package com.indonesiaemas.note.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indonesiaemas.note.data.entity.Note
import com.indonesiaemas.note.ui.components.WatermarkBackground
import com.indonesiaemas.note.ui.theme.noteColors
import com.indonesiaemas.note.viewmodel.NoteViewModel

val CATEGORIES = listOf("Umum", "Pekerjaan", "Pribadi", "Ide", "Belanja", "Kesehatan", "Keuangan")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    viewModel: NoteViewModel,
    noteId: Int? = null,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedColor by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf("Umum") }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var existingNote by remember { mutableStateOf<Note?>(null) }

    // Load existing note if editing
    LaunchedEffect(noteId) {
        noteId?.let { id ->
            viewModel.getNoteById(id)?.let { note ->
                existingNote = note
                title = note.title
                content = note.content
                selectedColor = note.color
                selectedCategory = note.category
            }
        }
    }

    val bgColor = noteColors.getOrElse(selectedColor) { noteColors[0] }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == null) "Catatan Baru" else "Edit Catatan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    // Category button
                    TextButton(onClick = { showCategoryDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.Label, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(selectedCategory)
                    }
                    // Save button
                    IconButton(
                        onClick = {
                            if (title.isNotBlank() || content.isNotBlank()) {
                                if (existingNote != null) {
                                    viewModel.updateNote(
                                        existingNote!!.copy(
                                            title = title.trim(),
                                            content = content.trim(),
                                            color = selectedColor,
                                            category = selectedCategory
                                        )
                                    )
                                } else {
                                    viewModel.insertNote(
                                        Note(
                                            title = title.trim(),
                                            content = content.trim(),
                                            color = selectedColor,
                                            category = selectedCategory
                                        )
                                    )
                                }
                                onNavigateBack()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Simpan")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
        containerColor = bgColor
    ) { padding ->
        WatermarkBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {
                // Color picker
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(noteColors) { index, color ->
                        ColorDot(
                            color = color,
                            isSelected = selectedColor == index,
                            onClick = { selectedColor = index }
                        )
                    }
                }

                // Title
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = {
                        Text(
                            "Judul",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1B1F)
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.Black.copy(alpha = 0.1f)
                )

                // Content
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = {
                        Text(
                            "Mulai menulis...",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = Color(0xFF1C1B1F),
                        lineHeight = 26.sp
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 400.dp)
                        .padding(horizontal = 8.dp)
                )
            }
        }//Akhir Background
    }

    // Category Dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Select Category") },
            text = {
                Column {
                    CATEGORIES.forEach { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCategory = category
                                    showCategoryDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (selectedCategory == category) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Spacer(modifier = Modifier.size(20.dp))
                            }
                            Text(category, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun ColorDot(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Color(0xFF6750A4) else Color.Black.copy(alpha = 0.2f),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF6750A4),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}