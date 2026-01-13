package com.example.records

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
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
    private lateinit var searchView: SearchView
    private var noteText : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(android.view.Window.FEATURE_CONTENT_TRANSITIONS)
        setContentView(R.layout.activity_view_note)

        db = NoteDatabase.getDatabase(this)
        val backButton = findViewById<TextView>(R.id.backToNotesBtnText)
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_fade)
        backButton.startAnimation(animation)


        // Initialize views
        savedNoteTitle = findViewById(R.id.textViewTitle)
        savedNoteContent = findViewById(R.id.textViewContent)

        // Retrieve note details from intent
        noteId = intent.getIntExtra("noteId", 0)

        // Load note details
        loadNoteDetails()

        // Configure SearchView
        configureSearchView()

        searchView.setIconifiedByDefault(true)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                highlightText(noteText, newText)
                return true
            }
        })

        findViewById<LinearLayout>(R.id.backToNoteslayout).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                findViewById(R.id.backToNotesBtnText), // Shared element
                "sharedNotesTitle" // Transition name
            )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent,options.toBundle())
//            finish()
        }

        val optionsButton: Button = findViewById(R.id.view_notes_options)
        optionsButton.setOnClickListener { view ->
            showPopupMenu(view)
        }
        findViewById<TextView>(R.id.textViewContent).setOnClickListener{
            editNote()
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
                noteText = it.content
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

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.transition.slide_right, R.transition.slide_left)
    }

    private fun configureSearchView() {
        searchView = findViewById(R.id.searchView_view_note)
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(Color.WHITE)
    }

    private fun highlightText(noteText: String, query: String?) {
        if (query.isNullOrEmpty()) {
            // If query is empty, reset to original text
            savedNoteContent.text = noteText
            return
        }

        // Create a SpannableString from the note text
        val spannableString = SpannableString(noteText)
        val lowerCaseNote = noteText.lowercase()
        val lowerCaseQuery = query.lowercase()

        // Find and highlight all occurrences of the query
        var startIndex = lowerCaseNote.indexOf(lowerCaseQuery)
        while (startIndex >= 0) {
            val endIndex = startIndex + query.length
            spannableString.setSpan(
                BackgroundColorSpan(Color.rgb(196,142,31)), // Highlight color
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            startIndex = lowerCaseNote.indexOf(lowerCaseQuery, endIndex)
        }
        // Set the highlighted text back to the TextView
        savedNoteContent.text = spannableString
    }
}