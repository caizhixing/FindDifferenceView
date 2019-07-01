package com.example.specialareacliclableview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SpecialAreaClickableView.ClickListener {

    private static final String TAG = "MainActivity";
    private SpecialAreaClickableView mSpecial, mMirror;
    private Button mResetButton, mTipButton;
    private ViewGroup viewGroup;
    /**
     * 最大的点击数量
     * */
    private int mMaxCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int halfW = 1080;

        mSpecial = findViewById(R.id.special);
        mMirror = findViewById(R.id.mirror);
        mResetButton = findViewById(R.id.reset);
        mTipButton = findViewById(R.id.tip);
        viewGroup = findViewById(R.id.wrapper);
        List<PositionModule> areas = new ArrayList<>();
        areas.add(new PositionModule(490, 770, PositionModule.Style.Rect,160f,90f, Color.GREEN));
        areas.add(new PositionModule(370, 110, PositionModule.Style.Cycle,100f, Color.BLACK));
        areas.add(new PositionModule(935,138, PositionModule.Style.Cycle,100f, Color.RED));
        mSpecial.postDelayed(() -> {
            mSpecial.addClickAreas(areas);
            mMirror.addClickAreas(areas);
        }, 200);
        mResetButton.setOnClickListener(this);
        mTipButton.setOnClickListener(this);
        mSpecial.setClickHideAreaListener(this);
        mMirror.setClickHideAreaListener(this);
        mSpecial.setTag("hide");
        mMirror.setTag("mirror");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.reset) {
            mSpecial.reset();
            mMirror.reset();
            mSpecial.setClickable(true);
            mMirror.setClickable(true);
            mMaxCount = 0;
        } else {
            mSpecial.showAllHideAreas();
        }
    }

    @Override
    public void onClickHideArea(View view, PositionModule point) {
        if (view.getTag().equals("mirror")) {
            mSpecial.showSpecialHideArea(point);
        }
    }

    @Override
    public void clicked() {
        mMaxCount++;
        if(mMaxCount > 10 ){
            mSpecial.setClickable(false);
            mMirror.setClickable(false);
            Log.d(TAG,"clickedCounter = "+mMaxCount+" and disClickable");
        }
        Log.d(TAG,"clickedCounter = "+mMaxCount);
    }

    @Override
    public void complete() {
        Log.d(TAG,"Game complete! Congratulations!");
        mSpecial.setClickable(false);
        mMirror.setClickable(false);
        // TODO show complete view
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 全屏
        viewGroup.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private int dp2px(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp * 1.0f, displayMetrics);
    }
}
