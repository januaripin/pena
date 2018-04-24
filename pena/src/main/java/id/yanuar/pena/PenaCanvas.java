package id.yanuar.pena;

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

/**
 * Created by Yanuar Arifin
 * halo@yanuar.id
 */
public class PenaCanvas extends View {
    private Context mContext;
    private Bitmap mCanvasBitmap;
    private Canvas mCanvas;
    private int mBackgroundColor;
    private Bitmap mBackgroundBitmap;
    private Paint mCanvasPaint;
    private Paint mStrokePaint;
    private Path mStrokePath;

    public PenaCanvas(Context context) {
        super(context);
    }

    public PenaCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PenaCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PenaCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    protected void init(Context context) {
        mContext = context;
        mStrokePath = new Path();
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);

        mStrokePaint = new Paint();
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setDither(true);
        mStrokePaint.setColor(Color.RED);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeJoin(Paint.Join.ROUND);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        mStrokePaint.setStrokeWidth(6);
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

                int scaleWidthSpec = MeasureSpec.makeMeasureSpec((int) (scale * mBackgroundBitmap.getWidth()), widthMode);
                int scaleHeightSpec = MeasureSpec.makeMeasureSpec((int) (scale * mBackgroundBitmap.getHeight()), heightMode);
                setMeasuredDimension(scaleWidthSpec, scaleHeightSpec);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mCanvasBitmap);
        if (mBackgroundBitmap != null) {
            mCanvas.drawBitmap(mBackgroundBitmap, null, new RectF(0, 0, w, h), null);
        } else {
            mCanvas.drawColor(mBackgroundColor);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
        canvas.drawPath(mStrokePath, mStrokePaint);
    }

    private float x = 0f;
    private float y = 0f;
    private static final float TOUCH_TOLERANCE = 4;

    private void touchStart(float fingerX, float fingerY) {
        mStrokePath.reset();
        mStrokePath.moveTo(fingerX, fingerY);
        x = fingerX;
        y = fingerY;
    }

    private void touchMove(float fingerX, float fingerY) {
        float dx = Math.abs(fingerX - x);
        float dy = Math.abs(fingerY - y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mStrokePath.quadTo(x, y, (fingerX + x) / 2, (fingerY + y) / 2);
            x = fingerX;
            y = fingerY;
        }
    }

    private void touchUp() {
        mStrokePath.lineTo(x, y);
        mCanvas.drawPath(mStrokePath, mStrokePaint);
        mStrokePath.reset();
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

    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    public void setBackgroundBitmap(Bitmap backgroundBitmap) {
        mBackgroundBitmap = backgroundBitmap;
    }

    public void setStrokeColor(int strokeColor) {
        mStrokePaint.setColor(strokeColor);
    }

    public void setStrokeWidth(float strokeWidth) {
        mStrokePaint.setStrokeWidth(strokeWidth);
    }

    public Bitmap getBitmap() {
        return mCanvasBitmap;
    }
}
