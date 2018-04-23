package id.yanuar.pena;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.OnColorSelectionListener;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Yanuar Arifin
 * halo@yanuar.id
 */
public class PenaActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION_STORAGE = 9182;
    private boolean permissionsDenied = true;

    private PenaConfig config;
    private RelativeLayout layoutCanvas;
    private PenaCanvas penaCanvas;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pena);

        toolbar = findViewById(R.id.toolbar);
        layoutCanvas = findViewById(R.id.layout_canvas);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent() == null || getIntent().getExtras() == null) {
            finish();
            return;
        }

        if (config == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle == null) {
                finish();
                return;
            }
            config = bundle.getParcelable(PenaConfig.class.getSimpleName());
        }

        setupScreen();
        initCanvas();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (permissionsDenied) {
            enablePermissions();
        } else {
            initCanvas();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initCanvas();
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
            showColorPickerDialog();
        } else if (id == R.id.action_clear) {
            initCanvas();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionsDenied = false;
                    initCanvas();
                }
                break;
            }
        }
    }

    private void initCanvas() {
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

        layoutCanvas.removeAllViews();
        layoutCanvas.addView(penaCanvas);
    }

    private void setupScreen(){
        toolbar.setTitle(config.getToolbarTitle());
        setRequestedOrientation(config.getOrientation());
    }

    private void save() {
        try {
            //initCanvas directory
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

    private void enablePermissions() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_STORAGE);
            }
        } else {
            initCanvas();
        }
    }

    private void showColorPickerDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.color_picker, null);
        HSLColorPicker hslColorPicker = view.findViewById(R.id.color_picker);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setNeutralButton("OK", null);
        builder.create().show();
    }
}
