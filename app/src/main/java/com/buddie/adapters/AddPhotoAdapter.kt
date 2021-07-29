package com.buddie.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.buddie.R
import com.buddie.R.*
import com.buddie.R.color.*
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AddPhotoAdapter(
    private val photoList: ArrayList<String>,
    val listener: OnFabBtnClickListener
) : ListAdapter<String, AddPhotoAdapter.AddPhotoViewHolder>(DiffCallback()) {
    inner class AddPhotoViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        var photoImageView: ImageView = view.findViewById(id.iv_photo)
        var fabButton: FloatingActionButton = view.findViewById(id.fab_add_image)

        init {
            fabButton.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            listener.onFabBtnClick(absoluteAdapterPosition, fabButton.contentDescription.toString())
        }

    }

    interface OnFabBtnClickListener {
        fun onFabBtnClick(position: Int, content: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddPhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout.item_photo, parent, false)
        return AddPhotoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    override fun onBindViewHolder(holder: AddPhotoViewHolder, position: Int) {

        if (photoList[position] != (position + 1).toString() && photoList[position] != null) {
            var bitmap: Bitmap = BitmapFactory.decodeFile(photoList[position]) // load
            // preview image
            bitmap = Bitmap.createScaledBitmap(bitmap!!, 400, 400, false)
            // holder.fabButton.s(drawable.ic_delete)
            holder.fabButton.contentDescription = "DeleteImage"
            //holder.addButton.setBackgroundColor(jazz_berry_jam)
            holder.photoImageView.setImageBitmap(bitmap)
        }

    }
}

class DiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}

