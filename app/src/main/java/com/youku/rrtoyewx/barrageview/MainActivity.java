package com.youku.rrtoyewx.barrageview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.youku.rrtoyewx.barragelibrary.BarrageView;
import com.youku.rrtoyewx.barragelibrary.BaseBarrageItem;
import com.youku.rrtoyewx.barragelibrary.NormalBarrageItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BarrageView barrageView;
    List<BaseBarrageItem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        for (int i = 0; i < 1; i++) {
            items.add(new NormalBarrageItem("弹幕内容" + i));
        }
        barrageView.addItemList(items);
    }

    public void reset(View view) {
        barrageView.reset();
    }
}
