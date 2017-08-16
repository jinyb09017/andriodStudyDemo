package com.abbott.hook;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.abbott.studydemo.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * hook onclickLisenter的案例
 */

public class HookActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook);

        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("onclick", "onClick is called");
            }
        });
        Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("onclick", "onClick2 is called");
            }
        });

        hookGroup(getWindow().getDecorView());
    }


    private void hookGroup(View v) {
        if (v instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) v;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View view = viewGroup.getChildAt(i);
                hookGroup(view);
            }
        } else {
            hookOnClickListenerInfo(v);
        }

    }


    private void hookOnClickListenerInfo(View view) {
        try {
            //得到ListenerInfo对象
            Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo");
            getListenerInfo.setAccessible(true);
            //调用指定view的listener的监听对象
            Object listenerInfo = getListenerInfo.invoke(view);

            //得到原始的OnClickListner对象
            Class<?> listenerInfoClz = Class.forName("android.view.View$ListenerInfo");
            Field mOnClickListener = listenerInfoClz.getDeclaredField("mOnClickListener");
            mOnClickListener.setAccessible(true);
            View.OnClickListener originOnClickLisenter = (View.OnClickListener) mOnClickListener.get(listenerInfo);
            HookedOnClickListener hookedOnClickListener = new HookedOnClickListener(originOnClickLisenter);
            mOnClickListener.set(listenerInfo, hookedOnClickListener);

        } catch (Exception e) {
            Log.e("onclick", "hook clickListener failed!");
            e.printStackTrace();
        }
    }


    class HookedOnClickListener implements View.OnClickListener {
        private View.OnClickListener origin;

        HookedOnClickListener(View.OnClickListener origin) {
            this.origin = origin;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(HookActivity.this, "hook click", Toast.LENGTH_SHORT).show();
            Log.i("onclick", "Before click, do what you want to to.");
            if (origin != null) {
                origin.onClick(v);
            }
            Log.i("onclick", "After click, do what you want to to.");
        }
    }

}
