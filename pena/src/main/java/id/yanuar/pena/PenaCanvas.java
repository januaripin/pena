package id.yanuar.pena;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Yanuar Arifin
 * halo@yanuar.id
 */
public class PenaCanvas extends View {
    private Bitmap bitmap;
    private Canvas canvas;
    private int canvasBackgroundColor;
    private Bitmap canvasBackgroundBitmap;
    private Paint canvasPaint = new Paint(Paint.DITHER_FLAG);
    private Paint pathPaint;
    private Path path = new Path();

    public PenaCanvas(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        if (canvasBackgroundBitmap != null) {
            canvas.drawBitmap(canvasBackgroundBitmap, null, new RectF(0, 0, w, h), null);
        } else {
            canvas.drawColor(canvasBackgroundColor);
        }
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        c.drawBitmap(bitmap, 0, 0, canvasPaint);
        c.drawPath(path, pathPaint);
    }

    private float x = 0f;
    private float y = 0f;
    private static final float TOUCH_TOLERANCE = 4;

    private void touchStart(float fingerX, float fingerY) {
        path.reset();
        path.moveTo(fingerX, fingerY);
        x = fingerX;
        y = fingerY;
    }

    private void touchMove(float fingerX, float fingerY) {
        float dx = Math.abs(fingerX - x);
        float dy = Math.abs(fingerY - y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(x, y, (fingerX + x) / 2, (fingerY + y) / 2);
            x = fingerX;
            y = fingerY;
        }
    }

    private void touchUp() {
        path.lineTo(x, y);
        canvas.drawPath(path, pathPaint);
        path.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float fingerX = event.getX();
        float fingerY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                touchStart(fingerX, fingerY);
                invalidate();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                touchMove(fingerX, fingerY);
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                touchUp();
                invalidate();
                break;
            }
        }
        return true;
    }

    public void setPathPaint(Paint pathPaint) {
        this.pathPaint = pathPaint;
    }

    public void setCanvasBackgroundColor(int canvasBackgroundColor) {
        this.canvasBackgroundColor = canvasBackgroundColor;
    }

    public void setCanvasBackgroundBitmap(Bitmap canvasBackgroundBitmap) {
        this.canvasBackgroundBitmap = canvasBackgroundBitmap;
    }

    public void setPathPaintColor(int color) {
        pathPaint.setColor(color);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
