package com.example.jammy.pdf_demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Jammy on 2016/6/23.
 */
public class SignatureView extends View {
    Path path;
    Paint paint;

    private float clickX = 0, clickY = 0;
    private float startX = 0, startY = 0;

    public SignatureView(Context context) {
        super(context);
        init();
    }

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SignatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startX = event.getX();
        startY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                clickX = startX;
                clickY = startY;
                path.moveTo(startX, startY);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                path.quadTo(clickX, clickY, (clickX + startX) / 2, (clickY + startY) / 2);
                clickX = startX;
                clickY = startY;
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                return true;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void init() {
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);////////一定要设置这个才可以画直线
        paint.setStrokeWidth(8);
        paint.setAntiAlias(true);
//        this.setBackgroundColor(Color.BLUE);
    }

    /**
     * 清空画板
     */
    public void clear() {
        path.reset();
        invalidate();
    }
}
