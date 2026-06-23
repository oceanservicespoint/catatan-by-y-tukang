package com.indonesiaemas.note.ui.screens


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.automirrored.outlined.NoteAdd
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indonesiaemas.note.data.entity.Note
import com.indonesiaemas.note.ui.components.WatermarkBackground
import com.indonesiaemas.note.ui.theme.noteColors
import com.indonesiaemas.note.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: NoteViewModel,
    onAddNote: () -> Unit,
    onNoteClick: (Int) -> Unit,
    onArchiveClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Catatan Saya",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Cari")
                    }
                    IconButton(onClick = { viewModel.toggleViewMode() }) {
                        Icon(
                            if (uiState.isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                            contentDescription = "Alihkan Tampilan"
                        )
                    }
                    IconButton(onClick = onArchiveClick) {
                        Icon(Icons.Outlined.Archive, contentDescription = "Archive")
                    }
                    IconButton(onClick = onAboutClick) {
                        Icon(Icons.Default.Info, contentDescription = "Tentang Aplikasi")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNote,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        WatermarkBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Category filter chips
                if (uiState.categories.isNotEmpty()) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = uiState.selectedCategory == null,
                                onClick = { viewModel.onCategorySelected(null) },
                                label = { Text("All") }
                            )
                        }
                        items(uiState.categories) { category ->
                            FilterChip(
                                selected = uiState.selectedCategory == category,
                                onClick = { viewModel.onCategorySelected(category) },
                                label = { Text(category) }
                            )
                        }
                    }
                }

                if (uiState.notes.isEmpty()) {
                    EmptyState()
                } else {
                    if (uiState.isGridView) {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalItemSpacing = 12.dp,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.notes, key = { it.id }) { note ->
                                NoteCard(
                                    note = note,
                                    onClick = { onNoteClick(note.id) },
                                    onPin = { viewModel.togglePin(note) },
                                    onArchive = { viewModel.toggleArchive(note) },
                                    onDelete = { viewModel.deleteNote(note) }
                                )
                            }
                        }
                    } else {
                        androidx.compose.foundation.lazy.LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.notes, key = { it.id }) { note ->
                                NoteCard(
                                    note = note,
                                    onClick = { onNoteClick(note.id) },
                                    onPin = { viewModel.togglePin(note) },
                                    onArchive = { viewModel.toggleArchive(note) },
                                    onDelete = { viewModel.deleteNote(note) },
                                    isList = true
                                )
                            }
                        }
                    }
                }
            }
        }//Akhir Watermark
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onPin: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
    isList: Boolean = false
) {
    var showMenu by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()
    val palette = if (isDark) com.indonesiaemas.note.ui.theme.noteColorsDark else com.indonesiaemas.note.ui.theme.noteColors
    val bgColor = palette.getOrElse(note.color) { palette[0] }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    if (note.title.isNotBlank()) {
                        Text(
                            text = note.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = if (isList) 1 else 2,
                            overflow = TextOverflow.Ellipsis,
                            color = Color(0xFF1C1B1F),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (note.isPinned) {
                        Icon(
                            Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (note.title.isNotBlank() && note.content.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (note.content.isNotBlank()) {
                    Text(
                        text = note.content,
                        fontSize = 14.sp,
                        maxLines = if (isList) 2 else 6,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0xFF49454F)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category chip
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.08f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = note.category,
                            fontSize = 11.sp,
                            color = Color(0xFF49454F)
                        )
                    }

                    Text(
                        text = formatDate(note.updatedAt),
                        fontSize = 11.sp,
                        color = Color(0xFF79747E)
                    )
                }
            }

            // Dropdown menu
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(if (note.isPinned) "Unpin" else "Pin") },
                    onClick = { onPin(); showMenu = false },
                    leadingIcon = { Icon(Icons.Default.PushPin, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Archive") },
                    onClick = { onArchive(); showMenu = false },
                    leadingIcon = { Icon(Icons.Outlined.Archive, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                    onClick = { onDelete(); showMenu = false },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Outlined.NoteAdd,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            )
            Text(
                "No notes yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Text(
                "Tap + to create your first note",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
            )
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
    return sdf.format(Date(timestamp))
}