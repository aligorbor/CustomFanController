package com.example.android.customfancontroller.spotlight

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.example.android.customfancontroller.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.random.Random

class SpotLightImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private var paint = Paint()
    private var shouldDrawSpotLight = false
    private var gameOver = false

    private lateinit var screenRect: RectF

    private val bitmapTarget = BitmapFactory.decodeResource(resources, R.drawable.android)
    private val spotlight = BitmapFactory.decodeResource(resources, R.drawable.mask)

    private var targetCurX = 0f
    private var targetCurY = 0f
    private var targetOffsetX = bitmapTarget.width / 2f
    private var targetOffsetY = bitmapTarget.height / 2f
    private var targetStep = 10f
    private var targetStepX = 0f
    private var targetStepY = 0f
    private var targetStepAngle = 10f
    private var targetCurAngle = 0f
    private var targetNewAngle = 0f
    private var targetNewAngleRad = 0f

    private var shader: Shader
    private val shaderMatrix = Matrix()
    private val targetMatrix = Matrix()


    init {
        val bitmap = Bitmap.createBitmap(spotlight.width, spotlight.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Draw a black rectangle.
        shaderPaint.color = Color.BLACK
        canvas.drawRect(
            0.0f,
            0.0f,
            spotlight.width.toFloat(),
            spotlight.height.toFloat(),
            shaderPaint
        )

        // Use the DST_OUT compositing mode to mask out the spotlight from the black rectangle.
        shaderPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        canvas.drawBitmap(spotlight, 0.0f, 0.0f, shaderPaint)

        shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
  //      paint.alpha = 100    //for testing

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Color the background yellow.
        //   canvas?.drawColor(Color.YELLOW)
//        shaderMatrix.setTranslate(
//            100f,
//            550f
//        )
        //   shader.setLocalMatrix(shaderMatrix)

        //   canvas?.drawRect(0.0f, 0.0f,spotlight.width.toFloat(), spotlight.height.toFloat(), paint)
        //  canvas?.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), paint)
        //   canvas?.drawCircle( width.toFloat()/2, height.toFloat()/2, width.toFloat()/2,  paint)

        canvas?.drawColor(Color.WHITE)

        targetMatrix.reset()
        if ((targetNewAngle - targetCurAngle) > targetStepAngle) {
            targetCurAngle += targetStepAngle
            targetMatrix.postRotate(targetCurAngle, targetOffsetX, targetOffsetY)
            targetMatrix.postTranslate(targetCurX, targetCurY)
            canvas?.drawBitmap(bitmapTarget, targetMatrix, paint)
            invalidate()
        } else
            if ((targetCurAngle - targetNewAngle) > targetStepAngle) {
                targetCurAngle -= targetStepAngle
                targetMatrix.postRotate(targetCurAngle, targetOffsetX, targetOffsetY)
                targetMatrix.postTranslate(targetCurX, targetCurY)
                canvas?.drawBitmap(bitmapTarget, targetMatrix, paint)
                invalidate()
            } else {
                targetCurAngle = targetNewAngle
                targetMatrix.postRotate(targetCurAngle, targetOffsetX, targetOffsetY)

                targetCurX += targetStepX
                targetCurY += targetStepY
                if (!correctTargetNewAngle()) {
                    targetMatrix.postTranslate(targetCurX, targetCurY)
                    canvas?.drawBitmap(bitmapTarget, targetMatrix, paint)
                }
                invalidate()
            }

        if (!gameOver) {
            if (shouldDrawSpotLight) {
                canvas?.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), paint)
            } else {
                   canvas?.drawColor(Color.BLACK)
            }
        }
    }

    private fun correctTargetNewAngle() =
        if (targetCurX < screenRect.left || targetCurX + bitmapTarget.width > screenRect.right
            || targetCurY < screenRect.top || targetCurY + bitmapTarget.height > screenRect.bottom
        ) {
            when {
                targetCurX < screenRect.left && targetCurAngle < 0 -> {
                    targetNewAngleRad += Math.PI.toFloat() / 2f
                }
                targetCurX < screenRect.left && targetCurAngle >= 0 -> {
                    targetNewAngleRad -= Math.PI.toFloat() / 2f

                }
                targetCurX + bitmapTarget.width > screenRect.right && targetCurAngle < 0 -> {
                    targetNewAngleRad -= Math.PI.toFloat() / 2f
                }
                targetCurX + bitmapTarget.width > screenRect.right && targetCurAngle >= 0 -> {
                    targetNewAngleRad += Math.PI.toFloat() / 2f
                }


                targetCurY < screenRect.top && targetCurAngle < -Math.PI.toFloat() / 2f -> {
                    targetNewAngleRad = -targetNewAngleRad
                }
                targetCurY < screenRect.top && targetCurAngle >= -Math.PI.toFloat() / 2f -> {
                    targetNewAngleRad = Math.PI.toFloat() / 2f + targetNewAngleRad
                }
                targetCurY + bitmapTarget.height > screenRect.bottom && targetCurAngle > Math.PI.toFloat() / 2f -> {
                    targetNewAngleRad = -targetNewAngleRad
                }
                targetCurY + bitmapTarget.height > screenRect.bottom && targetCurAngle <= Math.PI.toFloat() / 2f -> {
                    targetNewAngleRad = -Math.PI.toFloat() / 2f - targetNewAngleRad
                }
            }

            targetCurX -= targetStepX
            targetCurY -= targetStepY
            targetNewAngle = targetNewAngleRad * 180.0f / Math.PI.toFloat()
            targetStepX = (targetStep * cos(targetNewAngleRad.toDouble())).toFloat()
            targetStepY = (targetStep * sin(targetNewAngleRad.toDouble())).toFloat()
            true
        } else false


    private fun randomTargetRect() {
        targetCurX = floor(Random.nextFloat() * (width - bitmapTarget.width))
        targetCurY = floor(Random.nextFloat() * (height - bitmapTarget.height))
        screenRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
    }

    private fun calcTargetNewAngle(event: MotionEvent) {
        val offst=60
        if (!(targetCurX < screenRect.left+offst || targetCurX + bitmapTarget.width > screenRect.right-offst
            || targetCurY < screenRect.top+offst || targetCurY + bitmapTarget.height > screenRect.bottom-offst
                    )) {
            val catetX = targetCurX + targetOffsetX - event.x
            val catetY = targetCurY + targetOffsetY - event.y
            targetNewAngleRad = atan2(catetY.toDouble(), catetX.toDouble()).toFloat()
            targetNewAngle = targetNewAngleRad * 180.0f / Math.PI.toFloat()
            targetNewAngle += (targetStepAngle *2)
            targetNewAngleRad = targetNewAngle / 180.0f * Math.PI.toFloat()
            targetStepX = (targetStep * cos(targetNewAngleRad.toDouble())).toFloat()
            targetStepY = (targetStep * sin(targetNewAngleRad.toDouble())).toFloat()
        }
//        if (event.historySize > 0) {
//            var deltaX = (event.x - event.getHistoricalX(0)) * 0.75f
//            var deltaY = (event.y - event.getHistoricalY(0)) * 0.75f
//
//
//            targetCurX = targetCurX + deltaX
//            targetCurY = targetCurY + deltaY
//        }

    }

    private fun isGameOver(x: Float, y: Float) =
        (x > targetCurX + bitmapTarget.width / 4 && x < targetCurX + bitmapTarget.width / 4 * 3 && y > targetCurY + bitmapTarget.height / 4 && y < targetCurY + bitmapTarget.height / 4 * 3)


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        randomTargetRect()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val motionEventX: Float = (event?.x ?: 0) as Float
        val motionEventY: Float = (event?.y ?: 0) as Float
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                shouldDrawSpotLight = true
                if (gameOver) {
                    gameOver = false
                    randomTargetRect()
                }
                calcTargetNewAngle(event)
            }
            MotionEvent.ACTION_UP -> {
                shouldDrawSpotLight = false
                gameOver = isGameOver(motionEventX, motionEventY)
            }
            MotionEvent.ACTION_MOVE -> {
                calcTargetNewAngle(event)
            }
        }
        shaderMatrix.setTranslate(
            motionEventX - spotlight.width / 2.0f,
            motionEventY - spotlight.height / 2.0f
        )
        shader.setLocalMatrix(shaderMatrix)
        invalidate()
        return true
    }

}