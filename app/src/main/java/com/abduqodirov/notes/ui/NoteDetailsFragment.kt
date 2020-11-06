package com.abduqodirov.notes.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.abduqodirov.notes.R
import com.abduqodirov.notes.adapter.ImagesAdapter
import com.abduqodirov.notes.database.NoteDatabase
import com.abduqodirov.notes.databinding.FragmentNoteDetailsBinding
import com.abduqodirov.notes.model.Note
import com.abduqodirov.notes.util.DateFormatter
import com.abduqodirov.notes.util.IMAGE_CHOOSE_REQUEST_CODE
import com.abduqodirov.notes.viewmodel.NotesViewModel
import com.abduqodirov.notes.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.ArrayList

class NoteDetailsFragment(private val pressedNote: Note) : Fragment() {

    private var _binding: FragmentNoteDetailsBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: NotesViewModel

    private var receivedImagePaths = ArrayList<String>()
    private var addedImagesPaths = ArrayList<String>()

    private lateinit var imagesAdapter: ImagesAdapter


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


        viewModel.clearImagesArray()

        val pressedNoteId = pressedNote.id

        imagesAdapter = ImagesAdapter(
            imageClickListener = ImagesAdapter.ImageClickListener {
            },
            imageRemoveClickListener = ImagesAdapter.ImageRemoveClickListener {
                viewModel.removeImage(it)
            }
        )

        binding.detailsImagesRecycler.apply {
            adapter = imagesAdapter
        }

        viewModel.images.observe(viewLifecycleOwner, Observer {
            imagesAdapter.submitList(it)
            imagesAdapter.notifyDataSetChanged()

            addedImagesPaths = it
        })

        binding.detailsAddImageButton.setOnClickListener {

            val chooseImageIntent = Intent(Intent.ACTION_PICK)
            chooseImageIntent.type = "image/*"
            startActivityForResult(chooseImageIntent, IMAGE_CHOOSE_REQUEST_CODE)

        }

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
                    binding.detailsDisclaimerLastEdited.visibility = View.VISIBLE
                    binding.detailsLastEditedDate.text =
                        DateFormatter().formatDate(activeNote.lastEditedDate)
                }

                receivedImagePaths = activeNote.imagePaths

                if (receivedImagePaths.isNotEmpty()) {

                    for (imagePath in receivedImagePaths) {
                        viewModel.addImage(imagePath)
                    }

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

            val newTitle = binding.detailsTitleInput.text.toString()
            val newFullText = binding.detailsFullInput.text.toString()

            val newLastEditedDate = Calendar.getInstance().time

//            var writingImages = receivedImagePaths
//
//            if (addedImagesPaths != receivedImagePaths) {
//
//                Log.d("ovua", "AddedImages bilan Receivedni farqi bor")
//                writingImages = addedImagesPaths
//
//            } else {
//                Log.d("ovua", "AddedImages bilan Receivedni farqi Yo'q")
//
//            }


            val updatingNote = Note(
                id = pressedNoteId,
                title = newTitle,
                fullText = newFullText,
                lastEditedDate = newLastEditedDate,
                imagePaths = addedImagesPaths,
                createdDate = pressedNote.createdDate
            )


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_CHOOSE_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
        ) {

            try {
                val imageUri = data?.data
                if (imageUri != null) {
                    val inputStream = requireActivity().contentResolver.openInputStream(imageUri)
                    val chosenImage = BitmapFactory.decodeStream(inputStream)


                    val addedImagePath = saveBitmapToAppStorage(chosenImage)

                    viewModel.addImage(addedImagePath)
                }

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Snackbar.make(requireView(), "Something went wrong", Snackbar.LENGTH_SHORT).show()
            }

        } else {
            Snackbar.make(requireView(), "You haven't picked an image", Snackbar.LENGTH_SHORT).show()
        }

    }

    private fun saveBitmapToAppStorage(chosenImage: Bitmap): String {

        val imageFileName = "img_${System.currentTimeMillis()}.jpg"

        requireContext().openFileOutput(imageFileName, Context.MODE_PRIVATE).use {
            chosenImage.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        return imageFileName
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