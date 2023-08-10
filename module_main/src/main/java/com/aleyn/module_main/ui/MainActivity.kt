package com.aleyn.module_main.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.aleyn.lib_base.BaseActivity
import com.aleyn.module_main.R
import com.aleyn.router.LRouter
import com.aleyn.router.util.getFragment

class MainActivity : BaseActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LRouter.getFragment("/Main/Home")?.let {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fl_content, it)
                .commitAllowingStateLoss()
        }
    }

    fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fl_content, fragment)
            .addToBackStack(fragment::class.simpleName)
            .show(fragment)
            .commitAllowingStateLoss()
    }

}