package com.rickreation.magcompass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class FrameView extends View {
    private int mWidth;
    private int mHeight;

    private Paint framePaint;

    private double mRotation;

    int frameX = 0;
    int frameY = 10;

    int targetFrameX = 0;
    double dx = 0;
    double vx = 0;

    double easing = 0.8;

    int frameWidth = 100;
    int frameHeight = 100;
    
    private Rect r;
    public FrameView(Context context) {
        super(context);
        initializeView();
    }

    public FrameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initializeView();
    }

    private void initializeView() {
        framePaint = new Paint();
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setColor(Color.WHITE);
        
        framePaint.setAntiAlias(true);
        r = new Rect(frameX, frameY, 100, 100);
    }

    public void setRotation(double rotation) {
        mRotation = rotation;
        targetFrameX = (int)((mRotation / 360) * (mWidth * 6));
        dx = targetFrameX - frameX;
        vx = dx * easing;
        frameX += vx;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        mWidth = w;
        mHeight = h;
        super.onSizeChanged(w, h, oldW, oldH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        r.set(frameX, 0, frameX + frameWidth, frameHeight);
        canvas.drawRect(r, framePaint);
    }
}
