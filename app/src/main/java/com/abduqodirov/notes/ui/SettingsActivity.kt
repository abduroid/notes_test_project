package com.abduqodirov.notes.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.abduqodirov.notes.R
import com.abduqodirov.notes.database.NoteDatabase
import com.abduqodirov.notes.databinding.ActivitySettingsBinding
import com.abduqodirov.notes.viewmodel.NotesViewModel
import com.abduqodirov.notes.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: NotesViewModel

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataSource = NoteDatabase.getInstance(this).notesDao

        val viewModelFactory = ViewModelFactory(dataSource = dataSource)

        viewModel = ViewModelProviders.of(
            this, viewModelFactory
        ).get(NotesViewModel::class.java)

        binding.deleteCard.setOnClickListener {

            viewModel.deleteAllNotes()
            Snackbar.make(binding.root, "All notes deleted", Snackbar.LENGTH_SHORT).show()

            binding.deleteCard.isEnabled = false

        }

    }
}