package com.abduqodirov.notes.ui

import android.os.Bundle
import android.text.BoringLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.abduqodirov.notes.NotesAdapter
import com.abduqodirov.notes.R
import com.abduqodirov.notes.database.NoteDatabase
import com.abduqodirov.notes.databinding.FragmentNotesBinding
import com.abduqodirov.notes.model.Note
import com.abduqodirov.notes.viewmodel.NotesViewModel
import com.abduqodirov.notes.viewmodel.ViewModelFactory

class NotesListFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null

    private val binding get() = _binding!!


    private lateinit var viewModel: NotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataSource = NoteDatabase.getInstance(requireContext()).notesDao

        val viewModelFactory = ViewModelFactory(dataSource = dataSource)

        viewModel = ViewModelProviders.of(
            requireActivity(), viewModelFactory
        ).get(NotesViewModel::class.java)


        val notesAdapter = NotesAdapter(
            noteClickListener = NotesAdapter.NoteItemClickCallback { pressedNote: Note ->
                //TODO use clicked note

                navigateToNoteDetailsFragment(pressedNote)


            }
        )

        binding.notesRecycler.apply {
            adapter = notesAdapter
            setHasFixedSize(true)
        }

        viewModel.getAllNotesFromLocalDatabase().observe(viewLifecycleOwner, Observer {

            notesAdapter.submitList(it)

        })

        binding.notesAddNewButton.setOnClickListener {

            navigateToNewNoteFragment()

        }

        requireActivity().onBackPressedDispatcher.addCallback {
            if (isAdded) {
                requireActivity().finish()
            }
        }

    }

    private fun navigateToNewNoteFragment() {
        val fragmentManager = requireActivity().supportFragmentManager

        if (isTablet()) {

            val fragmentTransaction = fragmentManager.beginTransaction()
            val newNoteFragment = NewNoteFragment()
            fragmentTransaction.add(R.id.right_pane_container, newNoteFragment)
            fragmentTransaction.commit()
        } else {

            val fragmentTransaction = fragmentManager.beginTransaction()
            val newNoteFragment = NewNoteFragment()
            fragmentTransaction.add(R.id.left_pane_container, newNoteFragment)
            fragmentTransaction.commit()
        }

    }

    private fun navigateToNoteDetailsFragment(pressedNote: Note) {

        val fragmentManager = requireActivity().supportFragmentManager


        if (isTablet()) {

            val fragmentTransaction = fragmentManager.beginTransaction()
            val noteDetailsFragment = NoteDetailsFragment(pressedNote)
            fragmentTransaction.replace(R.id.right_pane_container, noteDetailsFragment)
            fragmentTransaction.commit()

        } else {

            val fragmentTransaction = fragmentManager.beginTransaction()
            val noteDetailsFragment = NoteDetailsFragment(pressedNote)
            fragmentTransaction.add(R.id.left_pane_container, noteDetailsFragment)
            fragmentTransaction.commit()
        }



    }

    private fun isTablet(): Boolean {

        return requireActivity().resources.getBoolean(R.bool.is_tablet)

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}