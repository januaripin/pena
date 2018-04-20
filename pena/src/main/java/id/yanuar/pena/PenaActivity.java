package id.yanuar.pena;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.OnColorSelectionListener;

import java.io.File;
import java.io.FileOutputStream;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Yanuar Arifin
 * halo@yanuar.id
 */
public class PenaActivity extends AppCompatActivity {
    private PenaConfig config;
    private RelativeLayout layoutCanvas;
    private PenaCanvas penaCanvas;
    private Toolbar toolbar;
    private HSLColorPicker hslColorPicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pena);

        toolbar = findViewById(R.id.toolbar);
        layoutCanvas = findViewById(R.id.layout_canvas);
        hslColorPicker = findViewById(R.id.color_picker);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent() == null || getIntent().getExtras() == null) {
            finish();
            return;
        }

        init();

        hslColorPicker.setColor(Color.BLACK);
        hslColorPicker.setColorSelectionListener(new OnColorSelectionListener() {
            @Override
            public void onColorSelected(int i) {
                penaCanvas.setPathPaintColor(i);
            }

            @Override
            public void onColorSelectionStart(int i) {

            }

            @Override
            public void onColorSelectionEnd(int i) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_pena, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_save) {
            save();
        } else if (id == R.id.action_pencil) {
            int visibility = hslColorPicker.getVisibility();
            if (visibility == VISIBLE) {
                hslColorPicker.setVisibility(GONE);
            } else {
                hslColorPicker.setVisibility(VISIBLE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        if (config == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle == null) {
                finish();
                return;
            }
            config = bundle.getParcelable(PenaConfig.class.getSimpleName());
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(6);

        penaCanvas = new PenaCanvas(this);
        penaCanvas.setPathPaint(paint);
        if (TextUtils.isEmpty(config.getBackgroundImage())) {
            penaCanvas.setCanvasBackgroundColor(config.getBackgroundColor());
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(config.getBackgroundImage());

            int width = (int) (getScale(bitmap) * bitmap.getWidth());
            int height = (int) (getScale(bitmap) * bitmap.getHeight());
            penaCanvas.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
            penaCanvas.setCanvasBackgroundBitmap(bitmap);
        }

        layoutCanvas.addView(penaCanvas);
    }

    private void save() {
        try {
            //init directory
            String path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES) + File.separator + config.getFileDirectory().concat("/");

            File outDir = new File(path);
            outDir.mkdirs();

            Long timestamp = System.currentTimeMillis() / 1000;
            String filename = config.getFilenamePrefix() + "_" + timestamp + ".jpg";

            File file = new File(path + filename);

            FileOutputStream fos = new FileOutputStream(file);

            penaCanvas.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fos);

            //flush output stream
            fos.flush();
            fos.close();

            Intent i = getIntent();
            i.putExtra(PenaConstants.EXTRA_FILE_PATH, file.getAbsolutePath());
            setResult(RESULT_OK, i);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float getScale(Bitmap bitmap) {
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels - toolbar.getHeight();

        float scale;
        if (bitmap.getHeight() > bitmap.getWidth()) {
            scale = (float) screenHeight / bitmap.getHeight();
        } else {
            scale = (float) screenWidth / bitmap.getWidth();
        }

        return scale;
    }
}
