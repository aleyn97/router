package com.aleyn.lib_base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.aleyn.annotation.Autowired
import com.aleyn.router.LRouter
import java.lang.reflect.ParameterizedType

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

/**
 * 泛型测试
 */
abstract class BaseVbActivity<VB : ViewBinding> : BaseActivity() {

    private var _binding: VB? = null

    protected val binding: VB get() = _binding!!

    @Autowired
    var baseParam = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        setContentView(binding.root)
        Log.d("BaseVbActivity", "onCreate: $baseParam")
    }

    private fun getViewBinding(): VB? {
        var genericSuperclass = this.javaClass.genericSuperclass
        var superclass = this.javaClass.superclass
        while (superclass != null) {
            if (genericSuperclass is ParameterizedType) {
                try {
                    genericSuperclass.actualTypeArguments
                        .filterIsInstance<Class<VB>>()
                        .find {
                            ViewBinding::class.java.isAssignableFrom(it as Class<*>)
                        }?.let {
                            @Suppress("UNCHECKED_CAST")
                            return it.getMethod("inflate", LayoutInflater::class.java)
                                .invoke(null, layoutInflater) as VB
                        }
                } catch (e: Exception) {
                    return null
                }
            }
            genericSuperclass = superclass.genericSuperclass
            superclass = superclass.superclass
        }
        return null
    }
}