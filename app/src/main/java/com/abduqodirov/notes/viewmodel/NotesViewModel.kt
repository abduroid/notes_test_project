package com.abduqodirov.notes.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
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

    fun getNoteById(id: Long) = localDatabase.getNoteById(id)

    fun updateNote(updatingNote: Note) {

        Log.d("tyua", "scopedagisiga keldi title: ${updatingNote.title}")
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

        Log.d("tyua", "suspendga keldi title: ${note.title}")
        withContext(Dispatchers.IO) {
            localDatabase.update(note)
            Log.d("tyua", "update qildi")
        }

    }

}