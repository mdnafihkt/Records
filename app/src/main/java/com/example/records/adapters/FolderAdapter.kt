package com.example.records.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.records.R
import com.example.records.database.Folder

class FolderAdapter(private val onClick: (Folder) -> Unit, private val onLongClick: (Folder) -> Unit) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {
    private var folders: List<Folder> = listOf()

    fun submitList(newFolders: List<Folder>) {
        folders = newFolders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.folder_item, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.bind(folder, onClick, onLongClick)
    }

    override fun getItemCount() = folders.size

    class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val folderNameView: TextView = itemView.findViewById(R.id.folderNameTextView)

        fun bind(folder: Folder, onClick: (Folder) -> Unit, onLongClick: (Folder) -> Unit) {
            folderNameView.text = folder.name
            itemView.setOnClickListener { onClick(folder) }
            itemView.setOnLongClickListener {
                onLongClick(folder)
                true  // Return true to indicate the long click is handled
            }
        }
    }
}
