package com.example.scrachfun

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import kotlin.random.Random

class ScratchImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private data class GlitterParticle(var x: Float, var y: Float, var alpha: Int, var radius: Float, var lifetime: Int)

    // Drawing components
    private var mErasePath: Path = Path()
    private lateinit var mErasePaint: Paint
    private lateinit var mGlowPaint: Paint
    private lateinit var mBorderPaint: Paint // New paint for the cursor border
    private var mScratchBitmap: Bitmap? = null
    private var mScratchCanvas: Canvas? = null
    private val mBitmapPaint = Paint(Paint.DITHER_FLAG)

    // State tracking
    private var mCurrentTouchX: Float = 0f
    private var mCurrentTouchY: Float = 0f
    private var mIsTouching: Boolean = false

    // Glitter components
    private val glitterParticles = mutableListOf<GlitterParticle>()
    private val glitterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val random = Random
    private var currentBrushSize = 100f // Start at max size

    private val vibrantCyanColor = ContextCompat.getColor(context, R.color.vibrant_cyan)

    init {
        setBrushSize(currentBrushSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            mScratchBitmap?.recycle()
            mScratchBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            mScratchCanvas = Canvas(mScratchBitmap!!)
            mScratchCanvas?.drawColor(vibrantCyanColor)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mScratchBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, mBitmapPaint)
        }

        if (mIsTouching) {
            // Draw the border first
            canvas.drawCircle(mCurrentTouchX, mCurrentTouchY, currentBrushSize / 2, mBorderPaint)
            // Draw the glow on top of the border
            canvas.drawCircle(mCurrentTouchX, mCurrentTouchY, currentBrushSize / 2, mGlowPaint)
        }

        val iterator = glitterParticles.iterator()
        while (iterator.hasNext()) {
            val particle = iterator.next()
            particle.lifetime--
            particle.alpha = (255 * (particle.lifetime / 20f)).toInt().coerceIn(0, 255)

            if (particle.lifetime <= 0) {
                iterator.remove()
            } else {
                glitterPaint.alpha = particle.alpha
                canvas.drawCircle(particle.x, particle.y, particle.radius, glitterPaint)
            }
        }

        if (mIsTouching || glitterParticles.isNotEmpty()) {
            invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mCurrentTouchX = event.x
        mCurrentTouchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mIsTouching = true
                mErasePath.moveTo(mCurrentTouchX, mCurrentTouchY)
            }
            MotionEvent.ACTION_MOVE -> {
                mErasePath.lineTo(mCurrentTouchX, mCurrentTouchY)
                mScratchCanvas?.drawPath(mErasePath, mErasePaint)
                mErasePath.reset()
                mErasePath.moveTo(mCurrentTouchX, mCurrentTouchY)
                spawnGlitter(mCurrentTouchX, mCurrentTouchY)
            }
            MotionEvent.ACTION_UP -> {
                mIsTouching = false
                mErasePath.reset()
            }
            else -> return false
        }
        invalidate()
        return true
    }

    private fun spawnGlitter(x: Float, y: Float) {
        for (i in 0 until 5) {
            val offsetX = (random.nextFloat() - 0.5f) * currentBrushSize
            val offsetY = (random.nextFloat() - 0.5f) * currentBrushSize
            glitterParticles.add(GlitterParticle(x + offsetX, y + offsetY, 255, random.nextFloat() * 4f + 1f, random.nextInt(10, 20)))
        }
    }

    fun setBrushSize(newSize: Float) {
        currentBrushSize = newSize.coerceAtLeast(1f)

        mErasePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = currentBrushSize
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            maskFilter = BlurMaskFilter(currentBrushSize / 4, BlurMaskFilter.Blur.NORMAL)
        }

        mGlowPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            alpha = 70
            maskFilter = BlurMaskFilter(currentBrushSize / 2, BlurMaskFilter.Blur.NORMAL)
        }
        
        // New border paint
        mBorderPaint = Paint().apply {
            style = Paint.Style.STROKE
            color = Color.BLACK
            strokeWidth = 2f
            alpha = 90
        }
    }

    fun reset() {
        mScratchCanvas?.drawColor(vibrantCyanColor, PorterDuff.Mode.SRC_OVER)
        glitterParticles.clear()
        invalidate()
    }
}
