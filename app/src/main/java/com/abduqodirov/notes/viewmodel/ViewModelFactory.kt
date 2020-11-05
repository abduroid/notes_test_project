package com.abduqodirov.notes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abduqodirov.notes.database.NotesDao

class ViewModelFactory(
    private val dataSource: NotesDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            return NotesViewModel(localDatabase = dataSource) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

}