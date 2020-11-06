package com.abduqodirov.notes.viewmodel

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

    val images = MutableLiveData<ArrayList<String>>()

    fun addImage(imagePath: String) {

        var oldImages = images.value
        if (oldImages == null) {
            oldImages = ArrayList()
        }

        val isAlreadyAdded = oldImages.any { path -> path == imagePath }
        if (!isAlreadyAdded && imagePath.isNotEmpty()) {
            oldImages.add(imagePath)
        }

        images.value = oldImages

    }

    fun removeImage(imagePath: String) {

        val removingImage = images.value?.find { path -> path == imagePath }

        if (removingImage != null) {

            val oldImages = images.value

            if (oldImages != null) {
                oldImages.remove(imagePath)

                images.value = oldImages
            }



        }

    }

    fun clearImagesArray() {
        images.value = ArrayList<String>()
    }

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

    fun deleteNote(deletingNote: Note) {

        viewModelScope.launch {
            deleteNoteFromRoom(deletingNote)
        }

    }

    fun deleteAllNotes() {

        viewModelScope.launch {
            deleteAllNotesFromRoom()
        }

    }

    private suspend fun insertNoteToRoom(note: Note) {

        withContext(Dispatchers.IO) {
            localDatabase.insertNewNote(note)
        }

    }

    private suspend fun updateOnRoom(note: Note) {

        withContext(Dispatchers.IO) {
            localDatabase.update(note)
        }

    }

    private suspend fun deleteNoteFromRoom(deletingNote: Note) {

        withContext(Dispatchers.IO) {
            localDatabase.deleteNote(deletingNote)
        }

    }

    private suspend fun deleteAllNotesFromRoom() {

        withContext(Dispatchers.IO) {
            localDatabase.deleteAllNotes()
        }

    }

}