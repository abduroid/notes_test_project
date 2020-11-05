package com.abduqodirov.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abduqodirov.notes.databinding.ItemNoteBinding
import com.abduqodirov.notes.model.Note
import com.abduqodirov.notes.util.DateFormatter

class NotesAdapter(
    private val noteClickListener: NoteItemClickCallback
) :
    ListAdapter<Note, NotesAdapter.NoteViewHolder>(NotesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position), noteClickListener)
    }

    class NoteViewHolder(private val itemNoteBinding: ItemNoteBinding) :
        RecyclerView.ViewHolder(itemNoteBinding.root) {

        fun bind(
            note: Note,
            noteClickListener: NoteItemClickCallback
        ) {

            itemNoteBinding.itemTitleText.text = note.title
            itemNoteBinding.itemShortDecText.text = note.fullText //TODO substring,  check if not asfa
            itemNoteBinding.itemCreatedDateText.text = DateFormatter().formatDate(note.createdDate)

            //Yaralgandan beri o'zgarganmi yo'qmi ikkalasini solishtirib tekshiradi.
            if (note.createdDate != note.lastEditedDate) {
                itemNoteBinding.itemLastEditedDateText.text = DateFormatter().formatDate(note.lastEditedDate)
            }

            //TODO load images and create ImageViews dynamically.

            itemNoteBinding.root.setOnClickListener {
                noteClickListener.onClickNote(note)
            }

        }

        companion object {
            fun from(parent: ViewGroup): NoteViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemNoteBinding.inflate(layoutInflater, parent, false)
                return NoteViewHolder(binding)
            }
        }

    }

    class NoteItemClickCallback(val noteClickListener: (note: Note) -> Unit) {
        fun onClickNote(note: Note) = noteClickListener(note)
    }

}

class NotesDiffCallback : DiffUtil.ItemCallback<Note>() {

    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem
    }


}