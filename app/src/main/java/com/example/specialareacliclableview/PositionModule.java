package com.example.specialareacliclableview;

import android.graphics.Point;

import androidx.annotation.ColorRes;

public class PositionModule {

    enum Style {

        Rect(0), Cycle(1);
        private int mStyle;

        Style(int i) {
            mStyle = i;
        }

        public int getStyle() {
            return mStyle;
        }
    }

    /**
     * 图形中心x坐标
     */
    private int X;
    /**
     * 图形中心y坐标
     */
    private int Y;
    /**
     * 所绘制的类型
     * */
    private Style style;
    /**
     * 如果是Cycle
     * */
    private float radius;
    /**
     * 如果Style是Rect 宽
     * */
    private float width;
    /**
     * 如果Style是Rect 高
     */
    private float height;
    /**
     * 颜色
     * */
    @ColorRes
    private int color;

    public PositionModule(int x, int y, Style style, float radius, int color) {
        X = x;
        Y = y;
        this.style = style;
        this.radius = radius;
        this.color = color;
    }

    public PositionModule(int x, int y, Style style, float width, float height, int color) {
        X = x;
        Y = y;
        this.style = style;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PositionModule point = (PositionModule) o;

        if (point.getX() != point.getX()) return false;
        if (point.getY() != point.getY()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = getX();
        result = 31 * result + getY();
        return result;
    }

    @Override
    public String toString() {
        return "PositionModule{" +
                "X=" + X +
                ", Y=" + Y +
                ", style=" + style +
                ", radius=" + radius +
                ", width=" + width +
                ", height=" + height +
                ", color=" + color +
                '}';
    }
}
