package com.orient.test.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.orient.test.R;

/**
 * Author WangJie
 * Created on 2019/1/14.
 */
public class DragView extends View {

    private Bitmap mBitmap;
    private RectF rectF = new RectF();
    private Matrix matrix;
    private Paint mDefaultPaint = new Paint();

    private boolean canDrag = false;
    private PointF mLastPoint = new PointF(0, 0);


    public DragView(Context context) {
        this(context, null);
    }

    public DragView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = 480 / 2;
        options.outHeight = 400 / 2;

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.th, options);
        matrix = new Matrix();
        rectF = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerId(event.getActionIndex()) == 0 && rectF.contains(event.getX(), event.getY())) {
                    canDrag = true;
                    mLastPoint.set(event.getX(), event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                if (event.getPointerId(event.getActionIndex()) == 0)
                    canDrag = false;
                break;

            case MotionEvent.ACTION_MOVE:
                int index = event.findPointerIndex(0);
                float deltaX = event.getX() - mLastPoint.x;
                float deltaY = event.getY() - mLastPoint.y;
                matrix.postTranslate(deltaX, deltaY);
                mLastPoint.set(event.getX(index), event.getY(index));

                // 更新图片区域
                rectF = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
                matrix.mapRect(rectF);

                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawBitmap(mBitmap, matrix, mDefaultPaint);
    }
}
