package com.example.android.customfancontroller.tictactoe

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class TicTacToeFieldView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var fieldSize = DEFAULT_FIELD_SIZE
    private var fieldBackSize = MAX_BACK_FIELD_SIZE
    private var cellSize = DEFAULT_CELL_SIZE
    private var fieldLeft = 0f
    private var fieldTop = 0f
    private var fieldBackLeft = 0f
    private var fieldBackTop = 0f
    private var map = Array(MAX_FIELD_SIZE) { Array(MAX_FIELD_SIZE) { DOT_EMPTY } }
    private var fieldStrokeColor = fieldStrokeColorDefault
    private var fieldBackStrokeColor = backStrokeColorDefault
    private var backColor = backColorDefault
    private val fieldWH: Float
        get() = fieldSize * cellSize
    private val fieldBackWH: Float
        get() = fieldBackSize * cellSize

    private val paint = Paint().apply {
        strokeWidth = 4f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(backColor)
        drawField(canvas)
    }


    private fun drawField(canvas: Canvas) {

        paint.color = fieldBackStrokeColor
        paint.strokeWidth = 2f

        val fieldBackRight = fieldBackLeft + fieldBackWH
        val fieldBackBottom = fieldBackTop + fieldBackWH
        for (i in 0..fieldBackSize) {
            if (i == 0) {
                for (j in 0..fieldBackSize) {
                    val x = fieldBackLeft + cellSize * j
                    canvas.drawLine(
                        x, fieldBackTop,
                        x, fieldBackBottom, paint
                    )
                }
            }
            val y = fieldBackTop + cellSize * i
            canvas.drawLine(
                fieldBackLeft, y,
                fieldBackRight, y, paint
            )
        }


        paint.color = fieldStrokeColor
        paint.strokeWidth = 8f
        val fieldRight = fieldLeft + fieldWH
        val fieldBottom = fieldTop + fieldWH
        for (i in 0 until fieldSize) {
            if (i == 0) {
                for (j in 0 until fieldSize) {
                    val x = fieldLeft + cellSize * j
                    canvas.drawLine(
                        x, fieldTop,
                        x, fieldBottom, paint
                    )
                }
            }
            val y = fieldTop + cellSize * i
            canvas.drawLine(
                fieldLeft, y,
                fieldRight, y, paint
            )
        }
    }

    companion object {
        const val MAX_FIELD_SIZE = 50
        const val MAX_BACK_FIELD_SIZE = 100
        const val MIN_FIELD_SIZE = 3
        const val DEFAULT_FIELD_SIZE = 3
        const val MIN_CELL_SIZE = 50f
        const val DEFAULT_CELL_SIZE = 300f
        const val MAX_CELL_SIZE = MIN_CELL_SIZE * 10
        private const val DOT_EMPTY = '•'
        private const val DOT_X = 'X'
        private const val DOT_O = 'O'
        const val fieldStrokeColorDefault = Color.BLACK
        const val backColorDefault = Color.LTGRAY
        const val backStrokeColorDefault = Color.BLUE
    }

}