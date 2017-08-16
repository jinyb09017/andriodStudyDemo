package com.abbott.hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.abbott.proxy.EvilInstrumentation;
import com.abbott.studydemo.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Hook2Activity extends Activity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook2);
        context = this;

        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Hook2Activity.this, HookActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        attachContext1();


    }


    public void attachContext() {

        try {
            // 先获取到当前的ActivityThread对象
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);

            // 拿到原始的 mInstrumentation字段
            Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            Instrumentation mInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);

            // 创建代理对象
            Instrumentation evilInstrumentation = new EvilInstrumentation(mInstrumentation);

            // 偷梁换柱
            mInstrumentationField.set(currentActivityThread, evilInstrumentation);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void attachContext1() {

        try {
//            Class<?> activityClass = getClass().getSuperclass();
            Class<?> activityClass = Class.forName("android.app.Activity");
            Field mInstrumentationField = activityClass.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);

            Instrumentation mInstrumentation = (Instrumentation) mInstrumentationField.get(this);

            // 创建代理对象
            Instrumentation evilInstrumentation = new EvilInstrumentation(mInstrumentation);

            // 偷梁换柱
            mInstrumentationField.set(this, evilInstrumentation);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
