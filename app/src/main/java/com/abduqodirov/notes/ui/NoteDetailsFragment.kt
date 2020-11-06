package com.abduqodirov.notes.ui

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.abduqodirov.notes.R
import com.abduqodirov.notes.database.NoteDatabase
import com.abduqodirov.notes.databinding.FragmentNoteDetailsBinding
import com.abduqodirov.notes.model.Note
import com.abduqodirov.notes.util.DateFormatter
import com.abduqodirov.notes.viewmodel.NotesViewModel
import com.abduqodirov.notes.viewmodel.ViewModelFactory
import java.io.File
import java.util.*

class NoteDetailsFragment(private val pressedNote: Note) : Fragment() {

    private var _binding: FragmentNoteDetailsBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: NotesViewModel

//    val args: NoteDetailsFragmentArgs by navArgs()


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


        val pressedNoteId = pressedNote.id

        viewModel.getNoteById(pressedNoteId).observe(
            viewLifecycleOwner,
            Observer { activeNote: Note ->

                Log.d("tyua", "note yangilandi: $activeNote")

                //TODO shu yerda yangiliklarni textViewlarga ham, editlarga ham set qilamiz.

                binding.detailsTitleText.text = activeNote.title
                binding.detailsTitleInput.setText(activeNote.title)

                binding.detailsFullText.text = activeNote.fullText
                binding.detailsFullInput.setText(activeNote.fullText)

                binding.detailsCreatedDate.text = DateFormatter().formatDate(activeNote.createdDate)


                if (activeNote.createdDate != activeNote.lastEditedDate) {
                    binding.detailsLastEditedDate.text =
                        DateFormatter().formatDate(activeNote.lastEditedDate)
                }

                //TODO images fix

                if (activeNote.imagePaths.isNotEmpty()) {
                    val imageFile = File(requireContext().filesDir, activeNote.imagePaths)

                    val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

                    binding.detailsImage.setImageBitmap(bitmap)
                    binding.detailsImage.setColorFilter(Color.TRANSPARENT)
                }




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

            Log.d("tyua", "submit bosildi")

            //TODO agar o'zgarish bo'lmasa eskisini olaverishi uchun edittextlarga old valuelarni berib chiqamiz.

            //TODO yangi datalarni TextViewlarga set qilamiz
            val newTitle = binding.detailsTitleInput.text.toString()
            val newFullText = binding.detailsFullInput.text.toString()
            val newImagePaths = "TODO new image paths" //TODO new images

            //TODO current dateni olamiz va lastEditeddatega yozamiz
            val newLastEditedDate = Calendar.getInstance().time

            val oldCreatedDate = "Bu yerda asli birinchi kiritilgan createdDate"

            val updatingNote = Note(
                id = pressedNoteId,
                title = newTitle,
                fullText = newFullText,
                lastEditedDate = newLastEditedDate,
                imagePaths = newImagePaths,
                createdDate = pressedNote.createdDate
            )

            Log.d("tyua", "uida title: $newTitle")

            viewModel.updateNote(updatingNote = updatingNote)

        }

        binding.detailsDeleteNoteButton.setOnClickListener {

            viewModel.deleteNote(pressedNote)

            navigateBackToNotesList()

        }

        activity?.let {

            it.onBackPressedDispatcher.addCallback {

                if (isAdded) {

                    if (isTablet()) {

                        requireActivity().finish()

                    } else {

                        val fragmentManager = requireActivity().supportFragmentManager

                        val fragmentTransaction = fragmentManager.beginTransaction()

                        val noteListFragment = NotesListFragment()

                        fragmentTransaction.replace(R.id.left_pane_container, noteListFragment)
                        fragmentTransaction.commit()

                    }
                }
            }

        }

    }


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

    private fun navigateBackToNotesList() {

        //TODO klaviaturani yopish kerak
        this.findNavController().popBackStack(R.id.noteDetailsFragment, true)

    }

    override fun onDestroyView() {
        super.onDestroyView()


        _binding = null
    }

    private fun isTablet(): Boolean {
        return requireActivity().resources.getBoolean(R.bool.is_tablet)
    }

}