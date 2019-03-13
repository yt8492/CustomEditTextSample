package com.yt8492.customedittextsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        limitableEditText.addTextChangedListener {
            textView.text = limitableEditText.getTextLengthDiff().toString()
        }
    }
}
