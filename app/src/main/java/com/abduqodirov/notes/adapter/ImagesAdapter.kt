package com.abduqodirov.notes.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abduqodirov.notes.databinding.ItemImageBinding
import java.io.File

class ImagesAdapter(
    private val imageClickListener: ImageClickListener,
    private val imageRemoveClickListener: ImageRemoveClickListener
) :  ListAdapter<String, ImagesAdapter.ImageViewHolder>(ImageDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        Log.d("ovua", "Recycler onBindViewholder: ${getItem(position)}")

        holder.bind(
            imagePath = getItem(position),
            imageClickListener = imageClickListener,
            imageRemoveClickListener = imageRemoveClickListener
        )
    }

    class ImageViewHolder(private val itemImageBinding: ItemImageBinding) : RecyclerView.ViewHolder(itemImageBinding.root) {
        fun bind(
            imagePath: String,
            imageClickListener: ImageClickListener,
            imageRemoveClickListener: ImageRemoveClickListener
        ) {

            Log.d("ovua", "ViewHolder bind item: $imagePath")

            val imageFile = File(itemImageBinding.itemImage.context.filesDir, imagePath)

            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

            itemImageBinding.itemImage.setImageBitmap(bitmap)
            itemImageBinding.itemImage.setColorFilter(Color.TRANSPARENT)

            itemImageBinding.root.setOnClickListener {
                imageClickListener.imageClickListener(imagePath)
            }

            itemImageBinding.itemImageDeleteButton.setOnClickListener {

                imageRemoveClickListener.imageRemoveClickListener(imagePath)

            }

        }

        companion object {
            fun from(parent: ViewGroup): ImageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemImageBinding.inflate(layoutInflater, parent, false)
                return ImageViewHolder(binding)
            }
        }
    }

    class ImageClickListener(val imageClickListener: (image: String) -> Unit) {
        fun onClickImage(image: String) = imageClickListener(image)
    }

    class ImageRemoveClickListener(val imageRemoveClickListener: (image: String) -> Unit) {
        fun onRemoveImage(image: String) = imageRemoveClickListener(image)
    }

}

class ImageDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        Log.d("ovua", "$newItem areItemsTheSame? ${oldItem == newItem}")
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        Log.d("ovua", "$newItem areContentsTheSame? ${oldItem == newItem}")
        return oldItem == newItem
    }

}