package com.example.android.customfancontroller.clipping

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ClippingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val clippedView = ClippedView(this)
        setContentView(clippedView)
        //      setContentView(R.layout.activity_clipping)
    }
}