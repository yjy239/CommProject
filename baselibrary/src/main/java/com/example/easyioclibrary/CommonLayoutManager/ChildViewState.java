package com.example.easyioclibrary.CommonLayoutManager;

/**
 * Created by yjy on 2018/4/12.
 */

public class ChildViewState {
    private boolean mIsForcus;


    private int left = 0;

    private int top = 0;

    private int bottom = 0;

    private int right = 0;

    private int width = 0;

    private int height = 0;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }



    public boolean ismIsForcus() {
        return mIsForcus;
    }

    public void setmIsForcus(boolean mIsForcus) {
        this.mIsForcus = mIsForcus;
    }


    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }


}
