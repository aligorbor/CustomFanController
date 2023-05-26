package com.example.android.customfancontroller.followpatch

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.android.customfancontroller.R
import kotlin.math.abs
import kotlin.math.atan2


class FollowPatchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var paint: Paint = Paint()
    var paintText: Paint = Paint()

    var bm: Bitmap
    var bm_offsetX = 0
    var bm_offsetY = 0

    var animPath: Path = Path()
    var pathMeasure: PathMeasure = PathMeasure()
    var pathLength = 0f

    var step = 0f  //distance each step
    var distance = 0f //distance moved
    var curX = 0f
    var curY = 0f

    var curAngle = 0f  //current angle
    var targetAngle = 0f  //target angle
    var targetAngleRad = 0f  //target angle
    var stepAngle = 0f  //angle each step


    var pos: FloatArray = FloatArray(2)
    var tan: FloatArray = FloatArray(2)

    private var matrix: Matrix = Matrix()

    var touchPath: Path = Path()
    var lastTime: Long = System.currentTimeMillis()

    init {
        paint.color = Color.BLUE
        paint.strokeWidth = 2f
        paint.style = Paint.Style.STROKE

        bm = BitmapFactory.decodeResource(resources, R.drawable.car)
        bm_offsetX = bm.getWidth() / 2
        bm_offsetY = bm.getHeight() / 2

        step = 1f //default
        stepAngle = 3f //default

        paintText.setColor(Color.RED)
        paintText.setStrokeWidth(1F)
        paintText.setStyle(Paint.Style.FILL)
        paintText.setTextSize(26F)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (animPath.isEmpty()) {
            return
        }

        val startNanos = System.nanoTime()
        val startMillis = System.currentTimeMillis()

        canvas?.drawPath(animPath, paint)

        matrix.reset()
        val delt = abs(targetAngle - curAngle)
        if (delt > stepAngle) {
            if (targetAngle > curAngle) {
                if (delt <= 180) curAngle += stepAngle else curAngle -= stepAngle
            } else {
                if (delt <= 180) curAngle -= stepAngle else curAngle += stepAngle
            }
            curAngle = curAngle % 360
            matrix.postRotate(curAngle, bm_offsetX.toFloat(), bm_offsetY.toFloat())
            matrix.postTranslate(curX, curY)
            canvas?.drawBitmap(bm, matrix, null)
            invalidate()
        } else {
            curAngle = targetAngle
            if (distance < pathLength) {
                pathMeasure.getPosTan(distance, pos, tan)

                targetAngleRad = (atan2(tan[1].toDouble(), tan[0].toDouble())).toFloat()
                targetAngle = (targetAngleRad * 180.0 / Math.PI).toFloat()
                if (curAngle >= 0) {
                    if (targetAngle <= 0 && curAngle - targetAngle >= 180) {
                        targetAngle += 360
                    }
                } else {
                    if (targetAngle >= 0 && targetAngle - curAngle >= 180) {
                        targetAngle -= 360
                    }
                }
                targetAngle = targetAngle % 360

                matrix.postRotate(curAngle, bm_offsetX.toFloat(), bm_offsetY.toFloat())

                curX = pos[0] - bm_offsetX
                curY = pos[1] - bm_offsetY
                matrix.postTranslate(curX, curY)

                canvas?.drawBitmap(bm, matrix, null)

                distance += step

                invalidate()
            } else {
                matrix.postRotate(curAngle, bm_offsetX.toFloat(), bm_offsetY.toFloat())
                matrix.postTranslate(curX, curY)
                canvas?.drawBitmap(bm, matrix, null)
            }
        }

        val endNanos = System.nanoTime()
        val betweenFrame: Long = startMillis - lastTime
        val fps = (1000 / betweenFrame).toInt()

        val strProcessingTime = "Processing Time (ms) (ns=0.000001ms) = " + (endNanos - startNanos)/1000000f
        val strBetweenFrame = "Between Frame (ms) = $betweenFrame"
        val strFPS = "Frame Per Second (approximate) = $fps"

        lastTime = startMillis
        canvas?.drawText(strProcessingTime, 10F, 30F, paintText)
        canvas?.drawText(strBetweenFrame, 10F, 60F, paintText)
        canvas?.drawText(strFPS, 10F, 90F, paintText)
        canvas?.drawText(pathLength.toString(), 10F, 120F, paintText)

    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                touchPath.reset()
                touchPath.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> touchPath.lineTo(event.x, event.y)
            MotionEvent.ACTION_UP -> {
                touchPath.lineTo(event.x, event.y)
                animPath = Path(touchPath)
                pathMeasure = PathMeasure(animPath, false)
                pathLength = pathMeasure.length

                //step = 1;
                distance = 0f
                curX = 0f
                curY = 0f

                //stepAngle = 1;
                curAngle = 0f
                targetAngle = 0f
                invalidate()
            }
        }
        return true
    }

    fun setSpeed(sp: Int) {
        step = sp.toFloat()
        stepAngle = sp.toFloat()
    }


}
