package com.abduqodirov.notes.viewmodel

import androidx.lifecycle.ViewModel
import com.abduqodirov.notes.database.NotesDao
import com.abduqodirov.notes.model.Note
import kotlinx.coroutines.*

class NotesViewModel(
    private val localDatabase: NotesDao
) : ViewModel() {

    private var viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun getAllNotesFromLocalDatabase() = localDatabase.getAllNotesLive()

    fun updateNote(updatingNote: Note) {

        viewModelScope.launch {
            updateOnRoom(note = updatingNote)
        }

    }

    fun addNewNote(newNote: Note) {

        viewModelScope.launch {
            insertNoteToRoom(note = newNote)
        }

    }

    private suspend fun insertNoteToRoom(note: Note) {

        withContext(Dispatchers.IO) {
            localDatabase.insertNewNote(note)
        }

    }

    private suspend fun updateOnRoom(note: Note) {

        withContext(Dispatchers.IO) {
            localDatabase.updateNote(note)
        }

    }

}