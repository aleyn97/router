package com.aleyn.module_main.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.aleyn.annotation.Route
import com.aleyn.lib_base.pojo.UserData
import com.aleyn.module_main.R
import com.aleyn.module_main.databinding.FragmentHomeBinding
import com.aleyn.router.LRouter
import com.aleyn.router.util.navigator

/**
 * @author: Aleyn
 * @date: 2023/7/20 14:05
 */
@Route(path = "/Main/Home")
class MainFragment : Fragment(R.layout.fragment_home) {

    private lateinit var activityLaunch: ActivityResultLauncher<Intent>

    private val binding by lazy {
        FragmentHomeBinding.bind(requireView())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val res = it.data?.getStringExtra("Result")
                    Log.d("MainFragment", ": ${it.data?.getStringExtra("Result")}")

                    Toast.makeText(requireContext(), "ActivityResult:${res}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnNav.setOnClickListener {
            LRouter.navigator("/First/Simple")
        }

        binding.btnParamNav.setOnClickListener {
            val userData = UserData("Aleyn", 25, 1)
            LRouter.build("/First/Param?sex=1")
                .withString("nickname", "Aleyn")
                .withInt("age", 26)
                .withAny("userData", userData) // Any 参数
                .navigation()
        }

        binding.btnFragment.setOnClickListener {
            LRouter.build("/First/TestFragment").getFragment()?.let {
                (requireActivity() as? MainActivity)?.addFragment(it)
            }
        }

        binding.btnActivityLaunch.setOnClickListener {
            LRouter.build("/First/ActivityLaunch")
                .withActivityLaunch(activityLaunch)
                .navigation(requireContext())
        }

        binding.btnIntercept.setOnClickListener {
            LRouter.navigator("/Main/Intercept")
        }

        binding.btnDyNav.setOnClickListener {
            LRouter.navigator("/Main/Room")
        }

        binding.btnDi.setOnClickListener {
            LRouter.navigator("/First/DI")
        }


        binding.btnJavaTwo.setOnClickListener {
            val userData = UserData("Aleyn", 25, 1)
            LRouter.build("/Two/Home?type=PC")
                .withString("game", "LOL")
                .withInt("role", 100)
                .withAny("userData", userData)
                .navigation()
        }
    }

}