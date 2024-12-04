package com.example.records

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.records.R
import com.example.records.database.FolderNoteJoin
import com.example.records.database.Note
import com.example.records.database.NoteDatabase
import kotlinx.coroutines.launch

class ViewNoteActivity : AppCompatActivity() {
    private lateinit var db: NoteDatabase
    private lateinit var savedNoteTitle: TextView
    private lateinit var savedNoteContent: TextView
    private var noteId: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_note)

        db = NoteDatabase.getDatabase(this)


        // Initialize views
        savedNoteTitle = findViewById(R.id.textViewTitle)
        savedNoteContent = findViewById(R.id.textViewContent)

        // Retrieve note details from intent
        noteId = intent.getIntExtra("noteId", 0)

        // Load note details
        loadNoteDetails()


        findViewById<Button>(R.id.backToNotesBtn).setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//            startActivity(intent)
            finish()
        }

        val optionsButton: Button = findViewById(R.id.view_notes_options)
        optionsButton.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenuView = layoutInflater.inflate(R.layout.popup_menu_layout,null)

        val editButton = popupMenuView.findViewById<Button>(R.id.edit)
        val deleteButton = popupMenuView.findViewById<Button>(R.id.delete)


        val popupWindow = PopupWindow(popupMenuView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        // Set button actions
        editButton.setOnClickListener {
            popupWindow.dismiss() // Close popup
            editNote() // Your edit action
        }

        deleteButton.setOnClickListener {
            popupWindow.dismiss() // Close popup
            AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure to delete this note?")
                .setPositiveButton("Delete") { _, _ ->
                    deleteNote()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        // Show the PopupWindow anchored to the view
        popupWindow.showAsDropDown(view, -50,10)
    }




    // Method to load note details from the database
    private fun loadNoteDetails() {
        lifecycleScope.launch {
            val note = db.noteDao().getNoteById(noteId)
            note?.let {
                savedNoteTitle.text = it.title
                savedNoteContent.text = it.content
            }
        }
    }


    private fun deleteNote() {
        lifecycleScope.launch{
            val note = db.noteDao().getNoteById(noteId)
            note?.let {
                db.noteDao().delete(note)
                finish()
            }
            db.folderNoteJoinDao().deleteByNoteId(noteId)
        }


    }

    private fun editNote() {
    val intent = Intent(this, AddNoteActivity::class.java).apply {
        putExtra("isEdit", true)
        putExtra("title", savedNoteTitle.text.toString())
        putExtra("content", savedNoteContent.text.toString())
        putExtra("noteId", noteId)
    }
    startActivity(intent)
    finish()
}
}