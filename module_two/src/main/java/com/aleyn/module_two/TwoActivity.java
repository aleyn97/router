package com.aleyn.module_two;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.aleyn.annotation.Autowired;
import com.aleyn.annotation.Route;
import com.aleyn.lib_base.BaseActivity;
import com.aleyn.lib_base.pojo.UserData;
import com.aleyn.lib_base.provider.IMainProvider;
import com.aleyn.router.LRouter;
import com.aleyn.router.inject.qualifier.StringQualifier;

/**
 * Java 类测试
 */
@Route(path = "/Two/Home")
public class TwoActivity extends BaseActivity {

    @Autowired
    String type;
    @Autowired
    String game;

    @Autowired
    int role;

    @Autowired
    UserData userData;

    @Autowired
    IMainProvider roomProvider;

    @Autowired(name = "main2")
    IMainProvider roomProvider2;

    IMainProvider roomProvider3 = LRouter.getByJava(IMainProvider.class, new StringQualifier("main2"));

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        TextView tvInfo = findViewById(R.id.tv_info);

        tvInfo.setText(
                "game:" + game + ";role:" + role + ";type:" + type
                        + "\n\nuserData:" + userData +
                        "\n\ngetName:" + roomProvider.getName() +
                        "\n\ngetName2:" + roomProvider2.getName() +
                        "\n\ngetName3:" + roomProvider3.getName()
        );
    }

}