package com.abduqodirov.notes.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abduqodirov.notes.model.Note

@Dao
interface NotesDao {

    @Insert
    fun insertNewNote(newNote: Note)

    @Update
    fun updateNote(updatingNote: Note)

    @Delete
    fun deleteNote(deletingNote: Note)

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotesLive(): LiveData<List<Note>>

    @Query("DELETE FROM notes")
    fun deleteAllNotes()
}