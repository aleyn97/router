package com.aleyn.module_first.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.aleyn.annotation.Route
import com.aleyn.module_first.R

@Route("/First/ActivityLaunch")
class ResultActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        findViewById<View>(R.id.btn_back).setOnClickListener {
            val intent = Intent()
            intent.putExtra("Result", "Back Success")
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}