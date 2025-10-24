package com.example.scrachfun

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var currentPath: Path = Path()
    private var drawPaint: Paint = Paint()
    private var canvasPaint: Paint
    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null
    private var brushSize: Float = 20f
    private var lastBrushSize: Float = 20f
    private var currentColor: Int = Color.BLACK

    init {
        drawPaint.isAntiAlias = true
        drawPaint.strokeWidth = brushSize
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
        canvasPaint = Paint(Paint.DITHER_FLAG)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
        drawCanvas?.drawColor(Color.WHITE)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(canvasBitmap!!, 0f, 0f, canvasPaint)
        canvas.drawPath(currentPath, drawPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> currentPath.moveTo(touchX, touchY)
            MotionEvent.ACTION_MOVE -> currentPath.lineTo(touchX, touchY)
            MotionEvent.ACTION_UP -> {
                drawCanvas?.drawPath(currentPath, drawPaint)
                currentPath.reset()
            }
            else -> return false
        }
        invalidate()
        return true
    }

    fun setColor(newColor: String) {
        currentColor = Color.parseColor(newColor)
        drawPaint.color = currentColor
        brushSize = lastBrushSize
        drawPaint.strokeWidth = brushSize
    }

    fun setBrushSize(newSize: Float) {
        brushSize = newSize
        lastBrushSize = newSize
        drawPaint.strokeWidth = brushSize
    }

    fun setEraser() {
        drawPaint.color = Color.WHITE
        drawPaint.strokeWidth = brushSize * 2
    }
}
