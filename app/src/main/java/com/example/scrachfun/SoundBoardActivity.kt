package com.example.scrachfun

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

data class SoundItem(val name: String, val resourceId: Int, val iconId: Int)

class SoundBoardActivity : AppCompatActivity() {

    private lateinit var soundGrid: GridView

    private val animalSounds = listOf(
        SoundItem("cat", R.raw.cat, R.drawable.ic_cat),
        // Add more animal sounds here, e.g.:
        // SoundItem("dog", R.raw.dog, R.drawable.ic_dog)
    )

    private val instrumentSounds = listOf(
        SoundItem("piano", R.raw.piano, R.drawable.ic_piano),
        // Add more instrument sounds here, e.g.:
        // SoundItem("guitar", R.raw.guitar, R.drawable.ic_guitar)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_board)

        soundGrid = findViewById(R.id.sound_grid)

        val animalsButton: Button = findViewById(R.id.animals_button)
        val instrumentsButton: Button = findViewById(R.id.instruments_button)

        animalsButton.setOnClickListener {
            updateSoundGrid(animalSounds)
        }

        instrumentsButton.setOnClickListener {
            updateSoundGrid(instrumentSounds)
        }

        // Start with animals by default
        updateSoundGrid(animalSounds)
    }

    private fun updateSoundGrid(sounds: List<SoundItem>) {
        soundGrid.adapter = SoundAdapter(this, sounds)
    }
}

class SoundAdapter(private val context: Context, private val sounds: List<SoundItem>) : BaseAdapter() {
    private var mediaPlayer: MediaPlayer? = null

    override fun getCount(): Int = sounds.size
    override fun getItem(position: Int): Any = sounds[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val soundItem = sounds[position]
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.sound_button, parent, false)
        val icon: ImageView = view.findViewById(R.id.sound_icon)
        icon.setImageResource(soundItem.iconId)

        view.setOnClickListener {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, soundItem.resourceId)
            mediaPlayer?.start()
        }
        return view
    }
}