package com.abduqodirov.notes.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.abduqodirov.notes.database.NoteDatabase
import com.abduqodirov.notes.databinding.FragmentNoteDetailsBinding
import com.abduqodirov.notes.model.Note
import com.abduqodirov.notes.viewmodel.NotesViewModel
import com.abduqodirov.notes.viewmodel.ViewModelFactory

class NoteDetailsFragment : Fragment() {

    private var _binding: FragmentNoteDetailsBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: NotesViewModel

    val args: NoteDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNoteDetailsBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataSource = NoteDatabase.getInstance(requireContext()).notesDao

        val viewModelFactory = ViewModelFactory(dataSource = dataSource)

        viewModel = ViewModelProviders.of(
            requireActivity(), viewModelFactory
        ).get(NotesViewModel::class.java)


        val pressedNoteId = args.pressedNoteId

        viewModel.getNoteById(pressedNoteId).observe(
            viewLifecycleOwner,
            Observer { activeNote: Note ->

                Log.d("tyua", "note yangilandi: $activeNote")

                //TODO shu yerda yangiliklarni textViewlarga ham, editlarga ham set qilamiz.

                binding.detailsTitleText.text = activeNote.title
                binding.detailsTitleInput.setText(activeNote.title)

                binding.detailsFullText.text = activeNote.fullText
                binding.detailsFullInput.setText(activeNote.fullText)

                binding.detailsCreatedDate.text = activeNote.createdDate
                binding.detailsLastEditedDate.text = activeNote.lastEditedDate

                //TODO images fix
                binding.detailsImagesText.text = activeNote.imagePaths


                readModeSwitcher(
                    binding.detailsTitleText,
                    binding.detailsTitleInput,
                    binding.detailsTitleToggleButton
                )

                readModeSwitcher(
                    binding.detailsFullText,
                    binding.detailsFullInput,
                    binding.detailsFullToggleButton
                )
            })

        binding.detailsTitleToggleButton.setOnClickListener {

            writeModeSwitcher(
                binding.detailsTitleText,
                binding.detailsTitleInput,
                binding.detailsTitleToggleButton
            )

        }

        binding.detailsFullToggleButton.setOnClickListener {

            writeModeSwitcher(
                binding.detailsFullText,
                binding.detailsFullInput,
                binding.detailsFullToggleButton
            )

        }

        binding.detailsSubmitButton.setOnClickListener {

            //TODO keyboard hide

            Log.d("tyua","submit bosildi")

            //TODO agar o'zgarish bo'lmasa eskisini olaverishi uchun edittextlarga old valuelarni berib chiqamiz.

            //TODO yangi datalarni TextViewlarga set qilamiz
            val newTitle = binding.detailsTitleInput.text.toString()
            val newFullText = binding.detailsFullInput.text.toString()
            val newImagePaths = "TODO new image paths" //TODO new images

            //TODO current dateni olamiz va lastEditeddatega yozamiz
            val newLastEditedDate = "yangilandi"

            val oldCreatedDate = "Bu yerda asli birinchi kiritilgan createdDate"

            val updatingNote = Note(
                id = args.pressedNoteId,
                title = newTitle,
                fullText = newFullText,
                lastEditedDate = newLastEditedDate,
                imagePaths = newImagePaths,
                createdDate = oldCreatedDate
            )

            Log.d("tyua", "uida title: $newTitle")

            viewModel.updateNote(updatingNote = updatingNote)

        }

    }

//    private fun swapVisibilityOfPairViews(textView: TextView, editText: EditText) {
//
//        if (textView.visibility == View.VISIBLE) {
//
//            textView.visibility = View.INVISIBLE
//            editText.visibility = View.VISIBLE
//        } else {
//            textView.visibility = View.VISIBLE
//            editText.visibility = View.INVISIBLE
//        }
//
//    }

    private fun readModeSwitcher(textView: TextView, editText: EditText, toggleButton: ImageView) {

        textView.visibility = View.VISIBLE
        editText.visibility = View.INVISIBLE

        toggleButton.visibility = View.VISIBLE

    }

    private fun writeModeSwitcher(textView: TextView, editText: EditText, toggleButton: ImageView) {

        textView.visibility = View.INVISIBLE
        editText.visibility = View.VISIBLE

        toggleButton.visibility = View.INVISIBLE

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}