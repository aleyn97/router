package com.aleyn.module_first.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.aleyn.annotation.Route
import com.aleyn.module_first.R

/**
 * @author: Aleyn
 * @date: 2023/8/9 12:07
 */
@Route("/First/TestFragment")
class TestFragment : Fragment(R.layout.fragemnt_test) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnClickListener {}
    }
}