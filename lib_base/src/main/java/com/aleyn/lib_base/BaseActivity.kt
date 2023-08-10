package com.aleyn.lib_base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aleyn.router.LRouter

/**
 * @author: Aleyn
 * @date: 2023/7/13 16:41
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LRouter.inject(this)
    }
}