package com.example.scrachfun

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class ColoringGameActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView
    private lateinit var sizeSeekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coloring_game)

        drawingView = findViewById(R.id.drawingView)
        sizeSeekBar = findViewById(R.id.sizeSeekBar)

        val colorBlack: ImageButton = findViewById(R.id.color_black)
        val colorRed: ImageButton = findViewById(R.id.color_red)
        val colorGreen: ImageButton = findViewById(R.id.color_green)
        val colorBlue: ImageButton = findViewById(R.id.color_blue)
        val colorYellow: ImageButton = findViewById(R.id.color_yellow)
        val colorMagenta: ImageButton = findViewById(R.id.color_magenta)
        val colorCyan: ImageButton = findViewById(R.id.color_cyan)
        val brushButton: ImageButton = findViewById(R.id.brush_button)
        val eraserButton: ImageButton = findViewById(R.id.eraser_button)

        val colorClickListener = View.OnClickListener { v ->
            val colorTag = v.tag as String
            drawingView.setColor(colorTag)
        }

        colorBlack.tag = "#000000"
        colorRed.tag = "#FF0000"
        colorGreen.tag = "#00FF00"
        colorBlue.tag = "#0000FF"
        colorYellow.tag = "#FFFF00"
        colorMagenta.tag = "#FF00FF"
        colorCyan.tag = "#00FFFF"

        colorBlack.setOnClickListener(colorClickListener)
        colorRed.setOnClickListener(colorClickListener)
        colorGreen.setOnClickListener(colorClickListener)
        colorBlue.setOnClickListener(colorClickListener)
        colorYellow.setOnClickListener(colorClickListener)
        colorMagenta.setOnClickListener(colorClickListener)
        colorCyan.setOnClickListener(colorClickListener)

        brushButton.setOnClickListener {
            drawingView.setColor("#000000") // Default back to black
        }

        eraserButton.setOnClickListener {
            drawingView.setEraser()
        }

        sizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                drawingView.setBrushSize(progress.toFloat())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}
