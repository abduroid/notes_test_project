package com.abduqodirov.notes.viewmodel

import androidx.lifecycle.ViewModel
import com.abduqodirov.notes.database.NotesDao

class NotesViewModel(
    private val localDatabase: NotesDao
) : ViewModel() {

    fun getAllNotesFromLocalDatabase() = localDatabase.getAllNotesLive()

}