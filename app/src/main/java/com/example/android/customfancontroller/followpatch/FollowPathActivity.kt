package com.example.android.customfancontroller.followpatch

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.android.customfancontroller.R


class FollowPathActivity : AppCompatActivity() {
    lateinit var followPatchView: FollowPatchView
    lateinit var speedBar: SeekBar

    var speedBarOnSeekBarChangeListener: OnSeekBarChangeListener =
        object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int,
                fromUser: Boolean
            ) {
                //offset from SeekBar 0~19 to step 1~10
                followPatchView.setSpeed(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow_path)

        followPatchView = findViewById(R.id.follow_patch_view)
        speedBar = findViewById(R.id.speedbar)
        speedBar.setOnSeekBarChangeListener(speedBarOnSeekBarChangeListener)
        followPatchView.setSpeed(speedBar.getProgress()) //set default speed

        val textDispInfo = findViewById<View>(R.id.dispinfo) as TextView
        //get display dpi
        val metrics = resources.displayMetrics
        textDispInfo.text = "xdpi = ${metrics.xdpi}\nydpi = ${metrics.ydpi}"

    }



}