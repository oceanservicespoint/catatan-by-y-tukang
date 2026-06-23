package com.indonesiaemas.note.data.dao


import androidx.room.*
import com.indonesiaemas.note.data.entity.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isArchived = 1 ORDER BY updatedAt DESC")
    fun getArchivedNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    @Query("SELECT * FROM notes WHERE isArchived = 0 AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%')")
    fun searchNotes(query: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE category = :category AND isArchived = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getNotesByCategory(category: String): Flow<List<Note>>

    @Query("SELECT DISTINCT category FROM notes WHERE isArchived = 0")
    fun getAllCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)

    @Query("UPDATE notes SET isPinned = :isPinned, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updatePinStatus(id: Int, isPinned: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET isArchived = :isArchived, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateArchiveStatus(id: Int, isArchived: Boolean, updatedAt: Long = System.currentTimeMillis())
}