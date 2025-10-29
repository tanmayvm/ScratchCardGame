package com.example.scrachfun

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import java.io.IOException
import kotlin.math.max
import kotlin.math.min

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var currentPath: Path = Path()
    private var drawPaint: Paint = Paint()
    private var canvasPaint: Paint
    private var eraserCursorPaint: Paint
    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null
    private var brushSize: Float = 20f
    private var lastBrushSize: Float = 20f
    private var currentColor: Int = Color.BLACK

    // Pan and Zoom variables
    private var offsetX = 0f
    private var offsetY = 0f
    private var lastPanX = 0f
    private var lastPanY = 0f
    private var isPanning = false
    private var scaleFactor = 1.0f
    private val minScaleFactor = 0.5f
    private val maxScaleFactor = 5.0f
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    private var isErasing = false
    private var eraserX = 0f
    private var eraserY = 0f

    init {
        drawPaint.isAntiAlias = true
        drawPaint.strokeWidth = brushSize
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
        canvasPaint = Paint(Paint.DITHER_FLAG)

        eraserCursorPaint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = max(minScaleFactor, min(scaleFactor, maxScaleFactor))
            invalidate()
            return true
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (canvasBitmap == null) {
            val canvasWidth = w * 3
            val canvasHeight = h * 3
            canvasBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
            drawCanvas = Canvas(canvasBitmap!!)
        }
        resetCanvas(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(offsetX, offsetY)
        canvas.scale(scaleFactor, scaleFactor)
        canvas.drawBitmap(canvasBitmap!!, 0f, 0f, canvasPaint)
        canvas.drawPath(currentPath, drawPaint)
        canvas.restore()

        if (isErasing && eraserX != -1f) {
            canvas.drawCircle(eraserX, eraserY, brushSize, eraserCursorPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)

        val touchX = event.x
        val touchY = event.y

        if (isErasing) {
            eraserX = touchX
            eraserY = touchY
        }

        // Don't draw or pan while scaling
        if (scaleGestureDetector.isInProgress) {
            currentPath.reset()
            return true
        }
        
        val scaledX = (touchX - offsetX) / scaleFactor
        val scaledY = (touchY - offsetY) / scaleFactor

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isPanning = false
                currentPath.moveTo(scaledX, scaledY)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount > 1) {
                    isPanning = true
                    currentPath.reset() // Stop drawing
                    lastPanX = event.getX(0)
                    lastPanY = event.getY(0)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isPanning && event.pointerCount > 1) {
                    val dx = event.getX(0) - lastPanX
                    val dy = event.getY(0) - lastPanY
                    offsetX += dx
                    offsetY += dy
                    lastPanX = event.getX(0)
                    lastPanY = event.getY(0)
                } else if (!isPanning) {
                    currentPath.lineTo(scaledX, scaledY)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (!isPanning) {
                    drawCanvas?.drawPath(currentPath, drawPaint)
                }
                currentPath.reset()
                isPanning = false
                if (isErasing) {
                    eraserX = -1f // Hide cursor when not touching
                    eraserY = -1f
                }
            }
        }

        invalidate()
        return true
    }

    private fun resetCanvas(viewWidth: Int, viewHeight: Int) {
        drawCanvas?.drawColor(Color.WHITE)
        scaleFactor = 1.0f
        val canvasWidth = viewWidth * 3
        val canvasHeight = viewHeight * 3
        offsetX = (-(canvasWidth - viewWidth) / 2).toFloat()
        offsetY = (-(canvasHeight - viewHeight) / 2).toFloat()
        invalidate()
    }

    fun resetCanvas() {
        resetCanvas(width, height)
    }

    fun loadImage(assetPath: String) {
        try {
            resetCanvas()
            val canvasWidth = drawCanvas?.width ?: width
            val canvasHeight = drawCanvas?.height ?: height
            val bitmap = decodeSampledBitmapFromAssets(context, assetPath, canvasWidth, canvasHeight)

            if (bitmap != null) {
                val left = (canvasWidth - bitmap.width) / 2
                val top = (canvasHeight - bitmap.height) / 2
                val destRect = Rect(left, top, left + bitmap.width, top + bitmap.height)
                drawCanvas?.drawBitmap(bitmap, null, destRect, canvasPaint)
            }
            invalidate()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    @Throws(IOException::class)
    private fun decodeSampledBitmapFromAssets(context: Context, assetPath: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        context.assets.open(assetPath).use { inputStream ->
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(inputStream, null, options)
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false
            context.assets.open(assetPath).use { secondInputStream ->
                return BitmapFactory.decodeStream(secondInputStream, null, options)
            }
        }
    }

    fun setColor(newColor: String) {
        isErasing = false
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
        isErasing = true
        drawPaint.color = Color.WHITE
    }
}
