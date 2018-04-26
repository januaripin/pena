package id.yanuar.pena;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yanuar Arifin
 * halo@yanuar.id
 */
public class PenaCanvas extends View {
    private Bitmap mCanvasBitmap;
    private Bitmap mBackgroundBitmap;

    private int mBackgroundColor = Color.WHITE;
    private int mStrokeColor = Color.RED;

    private float scaleWidth;
    private float scaleHeight;
    private float mStrokeWidth = 6f;

    public PenaCanvas(Context context) {
        super(context);
        init();
    }

    public PenaCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PenaCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PenaCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    protected void init() {
        pathLists.add(new Path());
        paintLists.add(createPaint());
        historyPointer++;
    }

    private Paint createPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getStrokeColor());
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(getStrokeWidth());

        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mBackgroundBitmap != null) {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int widthPixels = MeasureSpec.getSize(widthMeasureSpec);
            int heightPixels = MeasureSpec.getSize(heightMeasureSpec);

            if (mBackgroundBitmap != null) {
                float scale;
                if (mBackgroundBitmap.getHeight() > mBackgroundBitmap.getWidth()) {
                    scale = (float) heightPixels / mBackgroundBitmap.getHeight();
                } else {
                    scale = (float) widthPixels / mBackgroundBitmap.getWidth();
                }

                scaleWidth = scale * mBackgroundBitmap.getWidth();
                scaleHeight = scale * mBackgroundBitmap.getHeight();

                int scaleWidthSpec = MeasureSpec.makeMeasureSpec((int) scaleWidth, widthMode);
                int scaleHeightSpec = MeasureSpec.makeMeasureSpec((int) scaleHeight, heightMode);
                setMeasuredDimension(scaleWidthSpec, scaleHeightSpec);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBackgroundBitmap != null) {
            canvas.drawBitmap(mBackgroundBitmap, null, new RectF(0, 0, scaleWidth, scaleHeight), null);
        } else {
            canvas.drawColor(mBackgroundColor);
        }

        drawCanvas(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float fingerX = event.getX();
        float fingerY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                touchStart(fingerX, fingerY);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                touchMove(fingerX, fingerY);
                break;
            }
            case MotionEvent.ACTION_UP: {
                touchUp();
                break;
            }
        }

        invalidate();
        return true;
    }

    private float startX = 0F;
    private float startY = 0F;
    private static final float TOUCH_TOLERANCE = 4;

    private void touchStart(float fingerX, float fingerY) {
        startX = fingerX;
        startY = fingerY;
        Path path = new Path();
        path.moveTo(startX, startY);

        updateHistory(path);
    }

    private void touchMove(float fingerX, float fingerY) {
        Path path = getCurrentPath();

        float dx = Math.abs(fingerX - startX);
        float dy = Math.abs(fingerY - startY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(startX, startY, (fingerX + startX) / 2, (fingerY + startY) / 2);
            startX = fingerX;
            startY = fingerY;
        }
    }

    private void touchUp() {
        Path path = getCurrentPath();
        path.lineTo(startX, startY);
        startX = 0F;
        startY = 0F;
    }

    /**
     * @license CanvasView
     * Android Application Library
     * https://github.com/Korilakkuma/CanvasView
     * <p>
     * The MIT License
     * <p>
     * Copyright (c) 2014 Tomohiro IKEDA (Korilakkuma)
     * <p>
     * Permission is hereby granted, free of charge, to any person obtaining a copy
     * of this software and associated documentation files (the "Software"), to deal
     * in the Software without restriction, including without limitation the rights
     * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
     * copies of the Software, and to permit persons to whom the Software is
     * furnished to do so, subject to the following conditions:
     * <p>
     * The above copyright notice and this permission notice shall be included in
     * all copies or substantial portions of the Software.
     * <p>
     * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
     * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
     * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
     * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
     * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
     * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
     * THE SOFTWARE.
     */
    // ------------------------- START COPY or MODIFIED FROM CanvasView ----------------------------
    private int historyPointer = 0;
    private List<Path> pathLists = new ArrayList<>();
    private List<Paint> paintLists = new ArrayList<>();

    private Path getCurrentPath() {
        return pathLists.get(historyPointer - 1);
    }

    private void updateHistory(Path path) {
        if (historyPointer == pathLists.size()) {
            pathLists.add(path);
            paintLists.add(createPaint());
            historyPointer++;
        } else {
            // On the way of Undo or Redo
            pathLists.set(historyPointer, path);
            paintLists.set(historyPointer, createPaint());
            historyPointer++;

            for (int i = historyPointer, size = paintLists.size(); i < size; i++) {
                pathLists.remove(historyPointer);
                paintLists.remove(historyPointer);
            }
        }
    }

    private void drawCanvas(Canvas canvas) {
        canvas.drawBitmap(mCanvasBitmap, 0, 0, new Paint());

        for (int i = 0; i < historyPointer; i++) {
            canvas.drawPath(pathLists.get(i), paintLists.get(i));
        }
    }

    public void undo() {
        if (historyPointer > 1) {
            historyPointer--;
            invalidate();
        }
    }

    public void redo() {
        if (historyPointer < pathLists.size()) {
            historyPointer++;
            invalidate();
        }
    }

    public Bitmap getBitmap() {
        setDrawingCacheEnabled(false);
        setDrawingCacheEnabled(true);

        return Bitmap.createBitmap(getDrawingCache());
    }
    // --------------------------- END OF COPY OR MODIFIED FROM CanvasView -------------------------

    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    public void setBackgroundBitmap(Bitmap backgroundBitmap) {
        mBackgroundBitmap = backgroundBitmap;
    }

    public void setStrokeColor(int strokeColor) {
        mStrokeColor = strokeColor;
    }

    public int getStrokeColor() {
        return mStrokeColor;
    }

    public void setStrokeWidth(float strokeWidth) {
        mStrokeWidth = strokeWidth;
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

}
