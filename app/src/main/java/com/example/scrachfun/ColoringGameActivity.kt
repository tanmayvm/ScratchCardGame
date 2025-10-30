package com.example.scrachfun

import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.ImageButton
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat

class ColoringGameActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView
    private lateinit var selectSketchButton: ImageButton
    private lateinit var sketchPanel: FrameLayout
    private lateinit var sketchGrid: GridView
    private lateinit var colorPickerButton: ImageButton

    private var selectedColor: Int = Color.BLACK

    private val colors = listOf(
        "#000000", "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF", "#FFA500", "#A52A2A"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coloring_game)

        drawingView = findViewById(R.id.drawingView)
        selectSketchButton = findViewById(R.id.select_sketch_button)
        sketchPanel = findViewById(R.id.sketch_panel)
        sketchGrid = findViewById(R.id.sketch_grid)
        colorPickerButton = findViewById(R.id.color_picker_button)

        val brushButton: ImageButton = findViewById(R.id.brush_button)
        val eraserButton: ImageButton = findViewById(R.id.eraser_button)
        val resetButton: ImageButton = findViewById(R.id.reset_button)

        colorPickerButton.setOnClickListener { view ->
            showColorPopup(view)
        }

        brushButton.setOnClickListener {
            // This can be repurposed to open a brush size panel in the future
        }

        eraserButton.setOnClickListener {
            drawingView.setEraser()
            updateColorPickerButton(Color.WHITE)
        }

        resetButton.setOnClickListener {
            drawingView.resetCanvas()
        }

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
        
        updateColorPickerButton(selectedColor)
    }

    private fun showColorPopup(anchorView: View) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.color_selection_panel, null)

        val colorGrid = popupView.findViewById<GridView>(R.id.color_grid)
        val colorAdapter = ColorAdapter(this, colors)
        colorGrid.adapter = colorAdapter

        val popupWindow = PopupWindow(popupView, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.elevation = 10.0f

        colorGrid.onItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
            val colorString = view.tag as String
            val color = Color.parseColor(colorString)
            drawingView.setColor(colorString)
            updateColorPickerButton(color)
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(anchorView)
    }

    private fun updateColorPickerButton(color: Int) {
        selectedColor = color
        val drawable = colorPickerButton.background as LayerDrawable
        val colorCircle = drawable.findDrawableByLayerId(R.id.color_preview_circle)
        DrawableCompat.setTint(colorCircle, color)
    }

    private fun getSketchesFromResources(): List<String> {
        return resources.getStringArray(R.array.sketch_paths).toList()
    }
}
