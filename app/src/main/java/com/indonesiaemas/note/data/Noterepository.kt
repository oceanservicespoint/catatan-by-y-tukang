package com.indonesiaemas.note.data

import com.indonesiaemas.note.data.dao.NoteDao
import com.indonesiaemas.note.data.entity.Note

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()
    val archivedNotes: Flow<List<Note>> = noteDao.getArchivedNotes()
    val allCategories: Flow<List<String>> = noteDao.getAllCategories()

    fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotes(query)

    fun getNotesByCategory(category: String): Flow<List<Note>> = noteDao.getNotesByCategory(category)

    suspend fun getNoteById(id: Int): Note? = noteDao.getNoteById(id)

    suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)

    suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    suspend fun togglePin(id: Int, isPinned: Boolean) = noteDao.updatePinStatus(id, isPinned)

    suspend fun toggleArchive(id: Int, isArchived: Boolean) = noteDao.updateArchiveStatus(id, isArchived)
}