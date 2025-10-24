package com.example.scrachfun

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.MenuItem
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ScratchGameActivity : AppCompatActivity() {

    private lateinit var scratchImageView: ScratchImageView
    private lateinit var resetButton: Button
    private lateinit var sizeSeekBar: SeekBar
    private var imageUris: MutableList<Uri> = mutableListOf()
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scratch_game)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        scratchImageView = findViewById(R.id.scratchImageView)
        resetButton = findViewById(R.id.resetButton)
        sizeSeekBar = findViewById(R.id.sizeSeekBar)

        prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)

        loadImagesFromSelectedFolder()

        resetButton.setOnClickListener {
            scratchImageView.reset()
            loadRandomImage()
        }

        sizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                scratchImageView.setBrushSize(progress.toFloat())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun loadImagesFromSelectedFolder() {
        val uriString = prefs.getString("image_folder_uri", null)
        if (uriString == null) {
            Toast.makeText(this, "Please select an image folder from the main menu first!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        try {
            val folderUri = Uri.parse(uriString)
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(folderUri, DocumentsContract.getTreeDocumentId(folderUri))

            contentResolver.query(childrenUri, null, null, null, null)?.use { cursor ->
                val idColumn = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                val mimeTypeColumn = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)

                if (idColumn == -1 || mimeTypeColumn == -1) {
                    Toast.makeText(this, "Error reading folder contents.", Toast.LENGTH_SHORT).show()
                    return
                }

                while (cursor.moveToNext()) {
                    val docId = cursor.getString(idColumn)
                    val mimeType = cursor.getString(mimeTypeColumn)
                    if (mimeType != null && mimeType.startsWith("image/")) {
                        val docUri = DocumentsContract.buildDocumentUriUsingTree(folderUri, docId)
                        imageUris.add(docUri)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: Could not read the selected folder.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (imageUris.isEmpty()) {
            Toast.makeText(this, "No images found in the selected folder.", Toast.LENGTH_LONG).show()
        } else {
            loadRandomImage()
        }
    }

    private fun loadRandomImage() {
        if (imageUris.isNotEmpty()) {
            val randomUri = imageUris.random()
            try {
                contentResolver.openInputStream(randomUri)?.use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    scratchImageView.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
