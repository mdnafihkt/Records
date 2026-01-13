
package com.example.records

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.records.adapters.NoteAdapter
import com.example.records.database.NoteDatabase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var db: NoteDatabase
    private var isSearchClosing = false
    private var folderId:Int =0
    private lateinit var closeButton: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainActivityView(
                onBackToFoldersClick = { backToFolders() },
                onAddNoteClick = { addNote() }
            )
        }

        db = NoteDatabase.getDatabase(this)
        folderId = intent.getIntExtra("FOLDER_ID", 0)


        setupRecyclerView()
        loadNotes(folderId) // Load notes specific to the folder

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        // Observer for searched notes only
        noteViewModel.searchedNotes.observe(this) { notes ->
            noteAdapter.submitList(notes) // Update filtered list dynamically
        }

        // Configure SearchView
        configureSearchView()
        closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn)
        // Hide the close button
        closeButton?.visibility = View.GONE
    }

    private fun addNote() {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("FOLDER_ID", folderId) // Pass the folder ID
        startActivity(intent)
    }

    private fun backToFolders() {
        val intent = Intent(this, FolderActivity::class.java)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            findViewById(R.id.backToFoldersBtnText), // Shared element
            "sharedFolderTitle" // Transition name
        )
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent, options.toBundle())
    }



    //ADDITIONAL FUNCTIONS

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter { note ->
            val intent = Intent(this, ViewNoteActivity::class.java)
            intent.putExtra("title", note.title)
            intent.putExtra("content", note.content)
            intent.putExtra("noteId", note.id)
            val notesTitle = findViewById<View>(R.id.appName)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                notesTitle,"sharedNotesTitle"
            )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent,options.toBundle())
        }
        val notesRecyclerView = findViewById<RecyclerView>(R.id.NotesRecyclerView)
        notesRecyclerView.layoutManager = LinearLayoutManager(this)
        notesRecyclerView.adapter = noteAdapter

    }

    // LOAD NOTES FOR FOLDER ID
    private fun loadNotes(folderId: Int) {
        lifecycleScope.launch {
            db.folderNoteJoinDao().getNotesForFolder(folderId).observe(this@MainActivity) { notes ->
                // Update the RecyclerView whenever the notes change
                noteAdapter.submitList(notes)
            }
        }
    }

    // Configures the SearchView with listeners
    private fun configureSearchView() {
        searchView = findViewById(R.id.searchVieww)
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(Color.WHITE)
        searchEditText.setHintTextColor(Color.rgb(109, 107, 107))
        searchView.setQuery("", false)
        searchView.queryHint = "Search"
        searchView.isIconified = false
//        searchView.setIconifiedByDefault(false)
        searchView.clearFocus()

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!isSearchClosing) {
                if (hasFocus) {
                    closeButton?.visibility = View.VISIBLE
                    activateSearchListeners()
                } else {
                    deactivateSearchListeners()
//                   loadNotes(folderId)
                }
            }
        }
        searchView.setOnCloseListener {
            isSearchClosing = true
            searchView.setQuery("", false)
            searchView.clearFocus()
            closeButton?.visibility = View.GONE
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchView.windowToken, 0)
            loadNotes(folderId) // Reload notes after closing search
            isSearchClosing = false
            true
        }
    }

        // SEARCH FOR NOTES
    private fun activateSearchListeners() {

        // Enable search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { noteViewModel.searchNotes("%$it%") }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    // Re-observe getAllNotes when query is cleared
                    noteViewModel.getAllNotes().observe(this@MainActivity) { notes ->
                        noteAdapter.submitList(notes)
                    }
                } else {
                    noteViewModel.searchNotes("%$newText%")
                }
                return true
            }
        })
    }

    private fun deactivateSearchListeners() {
        // Reset the SearchView and remove search observers
        searchView.setOnQueryTextListener(null)
        searchView.setQuery("",false)
        searchView.clearFocus()
//        noteViewModel.searchedNotes.removeObservers(this)
        loadNotes(folderId)
    }
}
