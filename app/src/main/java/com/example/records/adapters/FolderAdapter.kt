package com.example.records.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.records.R
import com.example.records.database.Folder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderAdapter(
    private val onClick: (Folder) -> Unit,
    private val onLongClick: (Folder) -> Unit,
    private val getNoteCount: suspend (Int) -> Int
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    private var folders: List<Folder> = listOf()

    fun submitList(newFolders: List<Folder>) {
        folders = newFolders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.folder_item, parent, false)
        return FolderViewHolder(view, getNoteCount)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.bind(folder, onClick, onLongClick)
    }

    class FolderViewHolder(itemView: View,private val getNoteCount: suspend (Int) -> Int) : RecyclerView.ViewHolder(itemView) {
        private val folderNameView: TextView = itemView.findViewById(R.id.folderNameTextView)
        private val noteCountTextView: TextView = itemView.findViewById(R.id.noteCount)

        fun bind(folder: Folder, onClick: (Folder) -> Unit, onLongClick: (Folder) -> Unit){
            folderNameView.text = folder.name

            // Fetch note count in a coroutine
            CoroutineScope(Dispatchers.Main).launch {
                val noteCount = getNoteCount(folder.id)
                noteCountTextView.text = "$noteCount notes"
            }

            itemView.setOnClickListener { onClick(folder) }
            itemView.setOnLongClickListener {
                onLongClick(folder)
                true
            }
        }

    }

    override fun getItemCount(): Int = folders.size

}
