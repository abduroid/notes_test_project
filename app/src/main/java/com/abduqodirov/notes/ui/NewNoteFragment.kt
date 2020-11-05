package com.abduqodirov.notes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.abduqodirov.notes.R
import com.abduqodirov.notes.database.NoteDatabase
import com.abduqodirov.notes.databinding.FragmentNewNoteBinding
import com.abduqodirov.notes.model.Note
import com.abduqodirov.notes.viewmodel.NotesViewModel
import com.abduqodirov.notes.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_notes.*
import java.util.*

class NewNoteFragment : Fragment() {

    private var _binding: FragmentNewNoteBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: NotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNewNoteBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataSource = NoteDatabase.getInstance(requireContext()).notesDao

        val viewModelFactory = ViewModelFactory(dataSource = dataSource)

        viewModel = ViewModelProviders.of(
            requireActivity(), viewModelFactory
        ).get(NotesViewModel::class.java)

        binding.newNoteSubmitButton.setOnClickListener {

            val newTitle = binding.newNoteTitleInput.text.toString()
            val newFullText = binding.newNoteFullTextInput.text.toString()
            val newCreatedDate = Calendar.getInstance().time
            val newImages = "MOCK images"

            val newNote = Note(
                title = newTitle,
                fullText = newFullText,
                createdDate = newCreatedDate,
                imagePaths = newImages,
                lastEditedDate = newCreatedDate
            )

            viewModel.addNewNote(newNote = newNote)

            navigateBackToNotesList()
        }

    }

    private fun navigateBackToNotesList() {

        //TODO klaviaturani yopish kerak
        this.findNavController().popBackStack(R.id.newNoteFragment, true)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}