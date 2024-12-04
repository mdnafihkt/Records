package com.example.records

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.records.adapters.FolderAdapter
import com.example.records.database.Folder
import com.example.records.database.NoteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderActivity : AppCompatActivity() {
    private lateinit var folderAdapter: FolderAdapter
    private lateinit var db:NoteDatabase



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)

        // Initialize the database
        db = NoteDatabase.getDatabase(this)

        val allNotesTextView = findViewById<LinearLayout>(R.id.AllNotesLayout)
        val allnotesCount = findViewById<TextView>(R.id.allNotesCount)

        CoroutineScope(Dispatchers.Main).launch {
            val noteCount = db.folderNoteJoinDao().getNoteCountForFolder(0)
            allnotesCount.text = "$noteCount notes"
        }

        allNotesTextView.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("FOLDER_ID",0)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }



        folderAdapter = FolderAdapter(
            onClick = {  folder ->
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("FOLDER_ID",folder.id)
                startActivity(intent)},
            onLongClick = {folder ->
                showBottomSheet(folder)
            },
            getNoteCount = { folderId ->
                db.folderNoteJoinDao().getNoteCountForFolder(folderId)
            }
        )


        // Set up RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.FolderRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = folderAdapter

        loadFolders()

        findViewById<Button>(R.id.addFolderBtn).setOnClickListener {
            createNewFolderDialog()
            loadFolders()
        }
    }

    private fun showBottomSheet(folder: Folder) {
        val bottomSheet = FolderOptionsBottomSheet(
            onRenameClick = {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Rename Folder")

                val input = EditText(this)
                input.setText(folder.name)
                builder.setView(input)

                builder.setPositiveButton("Rename") { _, _ ->
                    val newName = input.text.toString()
                    if (newName.isNotEmpty()) {
                        lifecycleScope.launch {
                            folder.name = newName
                            db.folderDao().update(folder) // Update folder name in DB
                            loadFolders() // Refresh the folder list
                        }
                    }
                }

                builder.setNegativeButton("Cancel", null)
                builder.show() // Implement rename logic here
            },
            onDeleteClick = {
                AlertDialog.Builder(this)
                    .setTitle("Delete Folder")
                    .setMessage("Are you sure to Delete this folder ?")
                    .setPositiveButton("Delete") { _, _ ->
                        lifecycleScope.launch {
                            db.folderDao().delete(folder)
                            db.folderNoteJoinDao().deleteByFolderId(folder.id)
                        }
                        finish()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show() // Implement delete logic here
            }
        )
        bottomSheet.show(supportFragmentManager, "FolderOptionsBottomSheet")
    }


    private fun loadFolders() {
        lifecycleScope.launch {
            val allFolders = db.folderDao().getAllFolders()
            folderAdapter.submitList(allFolders)
        }
    }

    private fun createNewFolderDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("New Folder")
        val input = EditText(this)
        builder.setView(input)
        builder.setPositiveButton("create"){ _, _ ->
            val folderName = input.text.toString()
            if (folderName.isNotEmpty()){
            lifecycleScope.launch {
                val newFolder = Folder(0, name = folderName)
                db.folderDao().insert(newFolder)
                val allFolders = db.folderDao().getAllFolders()  // Fetch all folders again
                folderAdapter.submitList(allFolders)
            }
        }else {
                Toast.makeText(this, "Folder name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("cancel",null)
        builder.show()
    }

}