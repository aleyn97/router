package com.aleyn.module_first.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aleyn.annotation.Autowired
import com.aleyn.annotation.Route
import com.aleyn.lib_base.BaseVbActivity
import com.aleyn.lib_base.pojo.UserData
import com.aleyn.module_first.R
import com.aleyn.module_first.databinding.ActivityParamBinding
import com.aleyn.module_first.ui.`ParamActivity__LRouter$$Autowired`.autowiredInject

@Route("/First/Simple")
class SimpleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple)
    }
}


@Route("/First/Param")
class ParamActivity : BaseVbActivity<ActivityParamBinding>() {

    @Autowired("nickname")
    var name = ""

    @Autowired
    var age = 0

    @Autowired
    var sex = -1 // 这个是 URL参数

    @Autowired
    var userData: UserData? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("", "ParamActivity:" + intent.extras?.getString("nickname"))
        Log.d("", "ParamActivity:" + intent.extras?.getInt("age"))
        Log.d("", "ParamActivity:" + intent.extras?.getString("sex"))
        binding.tvParams.text = """
            name:${name}
            age:${age}
            sex:${sex}
            userData:${userData}
        """.trimIndent()
    }
}