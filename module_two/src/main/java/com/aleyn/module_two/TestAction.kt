package com.aleyn.module_two

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.aleyn.annotation.Route
import com.aleyn.router.core.LRouterAction

/**
 * @author: Aleyn
 * @date: 2026/03/17 16:53
 */

@Route(path = "custom://action/test")
class TestAction : LRouterAction {
    override fun action(context: Context, arguments: Bundle) {
//        val game = arguments.getString("Game").orEmpty()
//        val role = arguments.getInt("role")

        Toast.makeText(context, "TestAction${arguments}", Toast.LENGTH_SHORT).show()
    }
}