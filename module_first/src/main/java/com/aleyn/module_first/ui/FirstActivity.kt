package com.aleyn.module_first.ui

import android.os.Bundle
import com.aleyn.annotation.Route
import com.aleyn.lib_base.BaseActivity
import com.aleyn.module_first.R

@Route("/First/Home")
class FirstActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
    }

}