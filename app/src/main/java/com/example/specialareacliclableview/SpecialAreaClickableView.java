package com.example.specialareacliclableview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SpecialAreaClickableView extends View {

    /**
     * 是否要绘制点击的不同区域
     */
    private boolean mIsShowDifferentAreas;
    /**
     * view的宽高比
     */
    private float mRatio;
    /**
     * 是否开启固定宽高比
     */
    private boolean mRatioable = false;
    /**
     * 可点击的区域的中心点
     */
    private List<PositionModule> mClickAreas = new ArrayList<>();
    /**
     * 绘制触摸区域的画笔
     */
    private Paint mPaint;
    /**
     * 上一次点击的坐标的画笔
     * */
    private Paint mTrackerPaint;
    /**
     * 上一次点击的坐标
     * */
    private Point mTrackerPoint = new Point();
    /**
     * 是否可点击
     */
    private boolean mClickable = true;
    /***
     * 是都揭露所有隐藏区域
     */
    private boolean mShowAllHide = false;
    /**
     * 已经触发的绘制区域
     * */
    private HashSet<PositionModule> mClickedPosSet = new HashSet<>();
    /**
     * 相关回调监听
     * */
    private ClickListener mClickListener;

    public SpecialAreaClickableView(Context context) {
        this(context, null);
    }

    public SpecialAreaClickableView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpecialAreaClickableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SpecialAreaClickableView);
        mRatio = ta.getFloat(R.styleable.SpecialAreaClickableView_ratio, 1f);
        mIsShowDifferentAreas = ta.getBoolean(R.styleable.SpecialAreaClickableView_is_show_different_areas, true);
        ta.recycle();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.RED);
        mTrackerPaint = new Paint();
        mTrackerPaint.setAntiAlias(true);
        mTrackerPaint.setStyle(Paint.Style.FILL);
        mTrackerPaint.setColor(Color.parseColor("#88008577"));
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mRatioable) {
            int oldH = MeasureSpec.getSize(heightMeasureSpec);
            int w = MeasureSpec.makeMeasureSpec((int) (oldH * mRatio), MeasureSpec.getMode(widthMeasureSpec));
            int h = MeasureSpec.makeMeasureSpec(oldH, MeasureSpec.getMode(heightMeasureSpec));
            setMeasuredDimension(w, h);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mClickable) {
            return false;
        }
        // 只判断 点击事件
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return false;
        }
        if (mClickListener != null) {
            mClickListener.clicked();
        }
        float x = event.getX();
        float y = event.getY();
        if (mClickAreas.size() == 0) {
            return false;
        }
        PositionModule nearPos = isNearby(x, y);
        if (nearPos != null) {
            // 绘制不相同的区域
            mClickedPosSet.add(nearPos);
            if (mClickListener != null) {
                mClickListener.onClickHideArea(this, nearPos);
                if (mClickedPosSet.size() == mClickAreas.size()) {
                    mClickListener.complete();
                }
            }
            mTrackerPoint.x = -1;
            mTrackerPoint.y = -1;
            invalidate();
            // 消费事件
            return true;
        }else{
            // 绘制跟踪点
            mTrackerPoint.x = (int) event.getX();
            mTrackerPoint.y = (int) event.getY();
            invalidate();
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mIsShowDifferentAreas) {
            return;
        }
        if(mTrackerPoint.x > -1){
            drawLastPosition(canvas);
        }
        if (mShowAllHide) {
            drawAllHideAreas(canvas);
            mShowAllHide = false;
            return;
        }
        drawClickedArea(canvas);
    }

    private void drawLastPosition(Canvas canvas) {
        canvas.drawCircle(mTrackerPoint.x,mTrackerPoint.y,50,mTrackerPaint);
    }

    public void setmRatio(float mRatio) {
        this.mRatio = mRatio;
        invalidate();
    }

    public void setmRatioable(boolean state) {
        this.mRatioable = state;
    }

    private void drawClickedArea(Canvas canvas) {
        if (mClickedPosSet.size() > 0) {
            for (PositionModule p : mClickedPosSet) {
                mPaint.setColor(p.getColor());
                if (p.getStyle() == PositionModule.Style.Cycle) {
                    canvas.drawCircle(p.getX(), p.getY(), p.getRadius(), mPaint);
                } else if (p.getStyle() == PositionModule.Style.Rect) {
                    Rect rect = new Rect();
                    rect.left = (int) (p.getX() - p.getWidth() / 2);
                    rect.top = (int) (p.getY() - p.getHeight() / 2);
                    rect.right = (int) (p.getX() + p.getWidth() / 2);
                    rect.bottom = (int) (p.getY() + p.getHeight() / 2);
                    mPaint.setColor(p.getColor());
                    canvas.drawRect(rect, mPaint);
                }
            }
        }
    }

    private void drawAllHideAreas(Canvas canvas) {
        if (mClickAreas.size() > 0) {
            for (PositionModule p : mClickAreas) {
                mPaint.setColor(p.getColor());
                canvas.drawCircle(p.getX(), p.getY(), p.getRadius(), mPaint);
            }
        }
    }

    private PositionModule isNearby(float x, float y) {
        for (PositionModule p : mClickAreas) {
            if (p.getStyle() == PositionModule.Style.Cycle) {
                double a = Math.pow(p.getX() - x, 2);
                double b = Math.pow(p.getY() - y, 2);
                double distance = Math.sqrt(a + b);
                if (distance < p.getRadius() * 1.1) {
                    return p;
                }
            } else if (p.getStyle() == PositionModule.Style.Rect) {
                boolean flag = true;
                if (x < (p.getX() - p.getWidth() / 2) || x > (p.getX() + p.getWidth() / 2)) {
                    flag = false;
                }
                if (y < (p.getY() - p.getHeight() / 2) || y > (p.getY() + p.getHeight() / 2)) {
                    flag = false;
                }
                if (flag) {
                    return p;
                }
            }
        }
        return null;
    }

    public void reset() {
        mClickedPosSet.clear();
        mTrackerPoint.x = -1;
        mTrackerPoint.y = -1;
        invalidate();
    }

    public void showAllHideAreas() {
        mShowAllHide = true;
        invalidate();
    }

    public void addClickAreas(List<PositionModule> areas) {
        mClickAreas.clear();
        mClickAreas.addAll(areas);
        invalidate();
    }

    public void showSpecialHideArea(PositionModule point) {
        mClickedPosSet.add(point);
        invalidate();
    }

    public void setClickHideAreaListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        mClickable = clickable;
    }

    public interface ClickListener {

        void onClickHideArea(View view, PositionModule point);

        void clicked();

        void complete();

    }
}
