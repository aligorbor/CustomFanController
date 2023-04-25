package com.example.android.customfancontroller.minipaint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import com.example.android.customfancontroller.R

class MiniPaintActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
  val miniPaintView = MiniPaintView(this)
        miniPaintView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        miniPaintView.contentDescription = getString(R.string.canvasContentDescription)
        setContentView(miniPaintView)
  //      setContentView(R.layout.activity_mini_paint)
    }
}