package com.indonesiaemas.note.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.indonesiaemas.note.data.NoteRepository
import com.indonesiaemas.note.data.database.NoteDatabase
import com.indonesiaemas.note.data.entity.Note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class NoteUiState(
    val notes: List<Note> = emptyList(),
    val archivedNotes: List<Note> = emptyList(),
    val categories: List<String> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val isGridView: Boolean = true,
    val isLoading: Boolean = false,
    val snackbarMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _isGridView = MutableStateFlow(false)
    private val _snackbarMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<NoteUiState>

    init {
        val db = NoteDatabase.getDatabase(application)
        repository = NoteRepository(db.noteDao())

        val notesFlow = combine(_searchQuery, _selectedCategory) { query, category ->
            Pair(query, category)
        }.flatMapLatest { (query, category) ->
            when {
                query.isNotBlank() -> repository.searchNotes(query)
                category != null -> repository.getNotesByCategory(category)
                else -> repository.allNotes
            }
        }

        uiState = combine(
            notesFlow,
            repository.archivedNotes,
            repository.allCategories,
            _searchQuery,
            _selectedCategory,
            _isGridView,
            _snackbarMessage
        ) { args ->
            @Suppress("UNCHECKED_CAST")
            NoteUiState(
                notes = args[0] as List<Note>,
                archivedNotes = args[1] as List<Note>,
                categories = args[2] as List<String>,
                searchQuery = args[3] as String,
                selectedCategory = args[4] as String?,
                isGridView = args[5] as Boolean,
                snackbarMessage = args[6] as String?
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NoteUiState()
        )
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }

    fun toggleViewMode() {
        _isGridView.value = !_isGridView.value
    }

    fun insertNote(note: Note) {
        viewModelScope.launch {
            repository.insertNote(note)
            showSnackbar("Catatan tersimpan")
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
            showSnackbar("Catatan diupdate")
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
            showSnackbar("Catatan dihapus")
        }
    }

    fun togglePin(note: Note) {
        viewModelScope.launch {
            repository.togglePin(note.id, !note.isPinned)
            showSnackbar(if (!note.isPinned) "Catatan disematkan" else "Catatan dilepaskan")
        }
    }

    fun toggleArchive(note: Note) {
        viewModelScope.launch {
            repository.toggleArchive(note.id, !note.isArchived)
            showSnackbar(if (!note.isArchived) "Catatan diarsipkan" else "Catatan dikembalikan")
        }
    }

    suspend fun getNoteById(id: Int): Note? = repository.getNoteById(id)

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}