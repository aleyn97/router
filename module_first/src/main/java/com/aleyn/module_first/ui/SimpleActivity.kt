package com.aleyn.module_first.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aleyn.annotation.Autowired
import com.aleyn.annotation.Route
import com.aleyn.lib_base.BaseVbActivity
import com.aleyn.lib_base.pojo.UserData
import com.aleyn.module_first.R
import com.aleyn.module_first.databinding.ActivityParamBinding

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
    lateinit var userData: UserData


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tvParams.text = """
            name:${name}
            age:${age}
            sex:${sex}
            userData:${userData}
        """.trimIndent()
    }
}