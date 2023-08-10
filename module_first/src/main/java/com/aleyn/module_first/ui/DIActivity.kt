package com.aleyn.module_first.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import com.aleyn.annotation.Autowired
import com.aleyn.annotation.Route
import com.aleyn.lib_base.BaseActivity
import com.aleyn.lib_base.provider.IAppProvider
import com.aleyn.lib_base.provider.IMainProvider
import com.aleyn.module_first.R
import com.aleyn.router.inject.inject
import com.aleyn.router.inject.paramOf
import com.aleyn.router.inject.qualifier.sq

/**
 * 列举了 注入 的各种写法
 */
@Route("/First/DI")
class DIActivity : BaseActivity() {

    /**
     * 使用注解注入
     */
    @Autowired
    lateinit var appProvider: IAppProvider

    /**
     * 懒加载注入 (获取的 实例跟上边是同一个，IAppProvider的实现类是 @Singleton 单例)
     */
    private val appProvider2 by inject<IAppProvider>()


    private val mainProvider by inject<IMainProvider>()

    /**
     * 这个实例跟上个不同 @Factory 每次都创建新对象
     */
    private val main2Provider by inject<IMainProvider>()

    /**
     * 注入 [IMainProvider] main1 实例
     */
    @Autowired(name = "main1")
    var mainProvider1: IMainProvider? = null

    /**
     * 懒加载 注入 [IMainProvider] main2 实例
     */
    private val mainProvider2 by inject<IMainProvider>(sq("main2"))


    /**
     * 懒加载  注入带参数 [IMainProvider] main3 实例
     *
     */
    private val mainProvider3 by inject<IMainProvider>(sq("main3"), paramOf("Aleyn"))


    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_di)

        findViewById<TextView>(R.id.tv_di_info).text = """
            appProvider::getInfo-->${appProvider.getAppInfo()}
            
            appProvider2::getInfo-->${appProvider2.getAppInfo()}
            
            appProvider == appProvider2--> ${appProvider == appProvider2}
            
            mainProvider::getName-->${mainProvider.getName()}
            
            mainProvider1::getName-->${mainProvider1?.getName()}
            
            mainProvider2::getName-->${mainProvider2.getName()}
            
            mainProvider3::getName-->${mainProvider3.getName()}
            
            mainProvider == main2Provider-->${main2Provider == mainProvider}
        """.trimIndent()

    }

}