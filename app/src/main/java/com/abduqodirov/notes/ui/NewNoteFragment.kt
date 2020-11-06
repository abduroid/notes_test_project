package com.abduqodirov.notes.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.abduqodirov.notes.R
import com.abduqodirov.notes.database.NoteDatabase
import com.abduqodirov.notes.databinding.FragmentNewNoteBinding
import com.abduqodirov.notes.model.Note
import com.abduqodirov.notes.util.EXTERNAL_STORAGE_READ_WRITE_PERMISSION_REQUEST_CODE
import com.abduqodirov.notes.util.IMAGE_CHOOSE_REQUEST_CODE
import com.abduqodirov.notes.viewmodel.NotesViewModel
import com.abduqodirov.notes.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_notes.*
import java.io.FileNotFoundException
import java.util.*

class NewNoteFragment : Fragment() {

    private var _binding: FragmentNewNoteBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: NotesViewModel

    private lateinit var sharedPref: SharedPreferences

    private var addedImagePath = ""

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

        sharedPref = requireActivity().getSharedPreferences(
            getString(R.string.pref_key),
            Context.MODE_PRIVATE
        )

        val dataSource = NoteDatabase.getInstance(requireContext()).notesDao

        val viewModelFactory = ViewModelFactory(dataSource = dataSource)

        viewModel = ViewModelProviders.of(
            requireActivity(), viewModelFactory
        ).get(NotesViewModel::class.java)

        binding.newNoteImage.setOnClickListener {

            val chooseImageIntent = Intent(Intent.ACTION_PICK)
            chooseImageIntent.type = "image/*"
            startActivityForResult(chooseImageIntent, IMAGE_CHOOSE_REQUEST_CODE)

        }

        binding.newNoteSubmitButton.setOnClickListener {

            val newTitle = binding.newNoteTitleInput.text.toString()
            val newFullText = binding.newNoteFullTextInput.text.toString()
            val newCreatedDate = Calendar.getInstance().time

            var newImages = ""

            if (addedImagePath.isNotEmpty()) {

                newImages = addedImagePath

            }


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

    private fun checkReadWritePermission(): Boolean {
        val write = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        //TODO API 15
        val read = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadWriteStoragePermission() {

        with(sharedPref.edit()) {
            putBoolean(getString(R.string.key_read_write_storage_is_asked), true)
            apply()
        }

        this.requestPermissions(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), EXTERNAL_STORAGE_READ_WRITE_PERMISSION_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_CHOOSE_REQUEST_CODE
            && resultCode == RESULT_OK) {
            Log.d("tyua", "image keldi buyoqqa")

            try {
                val imageUri = data?.data
                if (imageUri != null) {
                    val inputStream = requireActivity().contentResolver.openInputStream(imageUri)
                    val chosenImage = BitmapFactory.decodeStream(inputStream)
                    binding.newNoteImage.setImageBitmap(chosenImage)
                    binding.newNoteImage.setColorFilter(Color.TRANSPARENT)

                    addedImagePath = saveBitmapToAppStorage(chosenImage)
                    //TODO add savedImage paths to Note object

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

        val imageFileName = "img_${System.currentTimeMillis()}"

        requireContext().openFileOutput(imageFileName, Context.MODE_PRIVATE).use {
            chosenImage.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        return imageFileName
    }

    private fun navigateBackToNotesList() {

        //TODO klaviaturani yopish kerak

        val fragmentManager = requireActivity().supportFragmentManager

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}