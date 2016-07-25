package com.youku.rrtoyewx.barrageview;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

import com.youku.rrtoyewx.barragelibrary.BarrageView;
import com.youku.rrtoyewx.barragelibrary.BaseBarrageItem;
import com.youku.rrtoyewx.barragelibrary.NormalBarrageItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    BarrageView barrageView;
    List<BaseBarrageItem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        barrageView = (BarrageView) findViewById(R.id.barrageview);
    }

    public void start(View view) {
        barrageView.start();
    }

    public void pause(View view) {
        barrageView.pause();
    }

    public void resume(View view) {
        barrageView.resume();
    }

    public void stop(View view) {
        barrageView.stop();
    }

    public void addItem(View view) {
        items.clear();
        for (int i = 0; i < 10; i++) {
            items.add(new NormalBarrageItem("我是默认弹幕" + i));
            items.add(new NormalBarrageItem("我是白色色弹幕" + i, Color.WHITE, Utils.sp2px(this, 18)));
            items.add(new NormalBarrageItem("我是有加速度的弹幕" + i, Color.GREEN, Utils.sp2px(this, 20), 3, new AccelerateInterpolator()));
        }
        barrageView.addItemList(items);
    }

    public void reset(View view) {
        barrageView.reset();
    }
}
