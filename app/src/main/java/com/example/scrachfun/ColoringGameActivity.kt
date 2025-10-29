package com.example.scrachfun

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class ColoringGameActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView
    private lateinit var sizeSeekBar: SeekBar
    private lateinit var selectSketchButton: ImageButton
    private lateinit var sketchPanel: FrameLayout
    private lateinit var sketchGrid: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coloring_game)

        drawingView = findViewById(R.id.drawingView)
        sizeSeekBar = findViewById(R.id.sizeSeekBar)
        selectSketchButton = findViewById(R.id.select_sketch_button)
        sketchPanel = findViewById(R.id.sketch_panel)
        sketchGrid = findViewById(R.id.sketch_grid)

        val colorBlack: ImageButton = findViewById(R.id.color_black)
        val colorRed: ImageButton = findViewById(R.id.color_red)
        val colorGreen: ImageButton = findViewById(R.id.color_green)
        val colorBlue: ImageButton = findViewById(R.id.color_blue)
        val colorYellow: ImageButton = findViewById(R.id.color_yellow)
        val colorMagenta: ImageButton = findViewById(R.id.color_magenta)
        val colorCyan: ImageButton = findViewById(R.id.color_cyan)
        val colorOrange: ImageButton = findViewById(R.id.color_orange)
        val colorBrown: ImageButton = findViewById(R.id.color_brown)
        val brushButton: ImageButton = findViewById(R.id.brush_button)
        val eraserButton: ImageButton = findViewById(R.id.eraser_button)
        val resetButton: ImageButton = findViewById(R.id.reset_button)

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
        colorOrange.tag = "#FFA500"
        colorBrown.tag = "#A52A2A"

        colorBlack.setOnClickListener(colorClickListener)
        colorRed.setOnClickListener(colorClickListener)
        colorGreen.setOnClickListener(colorClickListener)
        colorBlue.setOnClickListener(colorClickListener)
        colorYellow.setOnClickListener(colorClickListener)
        colorMagenta.setOnClickListener(colorClickListener)
        colorCyan.setOnClickListener(colorClickListener)
        colorOrange.setOnClickListener(colorClickListener)
        colorBrown.setOnClickListener(colorClickListener)

        brushButton.setOnClickListener {
            drawingView.setColor("#000000") // Default back to black
        }

        eraserButton.setOnClickListener {
            drawingView.setEraser()
        }

        resetButton.setOnClickListener {
            drawingView.resetCanvas()
        }

        sizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                drawingView.setBrushSize(progress.toFloat())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // --- Sketch Panel Setup ---
        val sketchList = getSketchesFromResources()
        val sketchAdapter = SketchAdapter(this, sketchList)
        sketchGrid.adapter = sketchAdapter

        selectSketchButton.setOnClickListener {
            sketchPanel.visibility = View.VISIBLE
        }

        sketchGrid.setOnItemClickListener { _, _, position, _ ->
            val selectedSketch = sketchList[position]
            if (selectedSketch == "None") {
                drawingView.resetCanvas()
            } else {
                drawingView.loadImage(selectedSketch)
            }
            sketchPanel.visibility = View.GONE
        }
        
        sketchPanel.setOnClickListener {
            sketchPanel.visibility = View.GONE
        }
    }

    private fun getSketchesFromResources(): List<String> {
        return resources.getStringArray(R.array.sketch_paths).toList()
    }
}
