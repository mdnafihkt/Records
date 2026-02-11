package com.example.records

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.records.ui.screen.FolderScreen
import com.example.records.ui.theme.RecordsTheme
import com.example.records.viewmodel.FolderViewModel

class FolderActivity : AppCompatActivity() {
    
    private lateinit var folderViewModel: FolderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup Window Transitions (Legacy - might need adjustment for full Compose but keeping for safety)
        val moveTransition = TransitionInflater.from(this).inflateTransition(android.R.transition.move)
        window.sharedElementReenterTransition = moveTransition
        window.sharedElementExitTransition = moveTransition
        window.requestFeature(android.view.Window.FEATURE_CONTENT_TRANSITIONS)

        folderViewModel = ViewModelProvider(this)[FolderViewModel::class.java]

        setContent {
            RecordsTheme {
                FolderScreen(
                    folderViewModel = folderViewModel,
                    onFolderClick = { folderId ->
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("FOLDER_ID", folderId)
                        // Clearing top to avoid stack buildup if navigating back and forth
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        overridePendingTransition(R.transition.slide_left, R.transition.slide_right)
                    }
                )
            }
        }
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.transition.slide_left, R.transition.slide_right)
    }
}