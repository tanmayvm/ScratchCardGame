package com.example.scrachfun

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainMenuActivity : AppCompatActivity() {

    private val FOLDER_PICKER_CODE = 102
    private lateinit var prefs: SharedPreferences
    private lateinit var folderPathTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        folderPathTextView = findViewById(R.id.selectedFolderPath)

        val scratchGameButton: Button = findViewById(R.id.scratchGameButton)
        scratchGameButton.setOnClickListener {
            val intent = Intent(this, ScratchGameActivity::class.java)
            startActivity(intent)
        }

        val coloringGameButton: Button = findViewById(R.id.coloringGameButton)
        coloringGameButton.setOnClickListener {
            val intent = Intent(this, ColoringGameActivity::class.java)
            startActivity(intent)
        }

        val soundBoardButton: Button = findViewById(R.id.soundBoardButton)
        soundBoardButton.setOnClickListener {
            val intent = Intent(this, SoundBoardActivity::class.java)
            startActivity(intent)
        }

        val selectFolderButton: Button = findViewById(R.id.selectFolderButton)
        selectFolderButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            startActivityForResult(intent, FOLDER_PICKER_CODE)
        }

        updateFolderPathDisplay()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FOLDER_PICKER_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                // Persist the permission and the URI string
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                prefs.edit().putString("image_folder_uri", uri.toString()).apply()
                updateFolderPathDisplay()
            }
        }
    }

    private fun updateFolderPathDisplay() {
        val uriString = prefs.getString("image_folder_uri", null)
        if (uriString != null) {
            // A folder has been selected
            folderPathTextView.text = "Selected folder: ...${Uri.parse(uriString).lastPathSegment}"
        } else {
            folderPathTextView.text = "No folder selected for scratch game"
        }
    }
}
