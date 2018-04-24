package id.yanuar.pena;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Yanuar Arifin
 * halo@yanuar.id
 */
public class PenaActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION_STORAGE = 9182;
    private boolean mPermissionsDenied = true;

    private PenaConfig mConfig;
    private PenaCanvas mPenaCanvas;
    private Toolbar mToolbar;
    private Bitmap.CompressFormat mFileFormat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pena);

        mToolbar = findViewById(R.id.toolbar);
        mPenaCanvas = findViewById(R.id.canvas);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent() == null || getIntent().getExtras() == null) {
            finish();
            return;
        }

        if (mConfig == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle == null) {
                finish();
                return;
            }
            mConfig = bundle.getParcelable(PenaConfig.class.getSimpleName());
        }

        setupScreen();
        initCanvas();
        setupProperties();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPermissionsDenied) {
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionsDenied = false;
                    initCanvas();
                }
                break;
            }
        }
    }

    private void initCanvas() {
        if (TextUtils.isEmpty(mConfig.getBackgroundImage())) {
            mPenaCanvas.setBackgroundColor(mConfig.getBackgroundColor());
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(mConfig.getBackgroundImage());
            mPenaCanvas.setBackgroundBitmap(bitmap);
        }
    }

    private void setupScreen() {
        mToolbar.setTitle(mConfig.getToolbarTitle());
        setRequestedOrientation(mConfig.getOrientation());
    }

    private void setupProperties() {
        if (mConfig.getFileFormat() == Pena.PNG) {
            mFileFormat = Bitmap.CompressFormat.PNG;
        } else if (mConfig.getFileFormat() == Pena.JPEG) {
            mFileFormat = Bitmap.CompressFormat.JPEG;
        } else {
            throw new IllegalArgumentException("Only support JPEG or PNG!");
        }
    }

    private void save() {
        try {
            //initCanvas directory
            String path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES) + File.separator + mConfig.getFileDirectory().concat("/");

            File outDir = new File(path);
            outDir.mkdirs();

            Long timestamp = System.currentTimeMillis() / 1000;
            StringBuilder filename = new StringBuilder();
            filename.append(mConfig.getFilenamePrefix());
            filename.append("_");
            filename.append(timestamp);

            if (mFileFormat == Bitmap.CompressFormat.PNG) {
                filename.append(".png");
            } else if (mFileFormat == Bitmap.CompressFormat.JPEG) {
                filename.append(".jpg");
            } else {
                throw new IllegalArgumentException("Only support JPEG or PNG!");
            }

            File file = new File(path + filename.toString());

            FileOutputStream fos = new FileOutputStream(file);

            mPenaCanvas.getBitmap().compress(mFileFormat, 100, fos);

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
}
