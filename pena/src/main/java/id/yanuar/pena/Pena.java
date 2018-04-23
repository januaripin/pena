package id.yanuar.pena;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * Created by Yanuar Arifin
 * halo@yanuar.id
 */
public abstract class Pena {
    private PenaConfig config;

    public abstract void start();

    public abstract void start(int requestCode);

    public void init() {
        config = new PenaConfig();
        config.setBackgroundColor(Color.WHITE);
        config.setBackgroundImage("");
        config.setFilenamePrefix("Pena");
        config.setFileDirectory("/");
        config.setToolbarTitle("Pena Editor");
        config.setOrientation(Pena.PORTRAIT);
        config.setFileFormat(Pena.JPEG);
    }

    public static class DrawingPenWithActivity extends Pena {
        private Activity activity;

        public DrawingPenWithActivity(Activity activity) {
            this.activity = activity;
            init();
        }

        @Override
        public void start() {
            activity.startActivityForResult(getIntent(activity), PenaConstants.RC_DRAWING_PEN);
        }

        @Override
        public void start(int requestCode) {
            activity.startActivityForResult(getIntent(activity), requestCode);
        }
    }

    public static class DrawingPenWithFragment extends Pena {
        private Fragment fragment;

        public DrawingPenWithFragment(Fragment fragment) {
            this.fragment = fragment;
            init();
        }

        @Override
        public void start() {
            fragment.startActivityForResult(getIntent(fragment.getActivity()), PenaConstants.RC_DRAWING_PEN);
        }

        @Override
        public void start(int requestCode) {
            fragment.startActivityForResult(getIntent(fragment.getActivity()), requestCode);
        }
    }

    public static class DrawingPenWithSupportFragment extends Pena {
        private android.support.v4.app.Fragment fragment;

        public DrawingPenWithSupportFragment(android.support.v4.app.Fragment fragment) {
            this.fragment = fragment;
            init();
        }

        @Override
        public void start() {
            fragment.startActivityForResult(getIntent(fragment.getActivity()), PenaConstants.RC_DRAWING_PEN);
        }

        @Override
        public void start(int requestCode) {
            fragment.startActivityForResult(getIntent(fragment.getActivity()), requestCode);
        }
    }

    public static DrawingPenWithActivity create(Activity activity) {
        return new DrawingPenWithActivity(activity);
    }

    public static DrawingPenWithFragment create(Fragment fragment) {
        return new DrawingPenWithFragment(fragment);
    }

    public static DrawingPenWithSupportFragment create(android.support.v4.app.Fragment fragment) {
        return new DrawingPenWithSupportFragment(fragment);
    }

    public Pena filenamePrefix(String prefix) {
        config.setFilenamePrefix(prefix);
        return this;
    }

    public Pena backgroundImage(String path) {
        config.setBackgroundImage(path);
        return this;
    }

    public Pena backgroundColor(int color) {
        config.setBackgroundColor(color);
        return this;
    }

    public Pena fileDirectory(String path) {
        config.setFileDirectory(path);
        return this;
    }

    public Pena toolbarTitle(String title) {
        config.setToolbarTitle(title);
        return this;
    }

    public Pena orientation(int orientation) {
        config.setOrientation(orientation);
        return this;
    }

    public Pena fileFormat(int fileFormat) {
        config.setFileFormat(fileFormat);
        return this;
    }

    public Intent getIntent(Context context) {
        if (config.getOrientation() > 1) {
            throw new IllegalArgumentException("Only support PORTRAIT or LANDSCAPE!");
        }
        if (config.getFileFormat() > 1){
            throw new IllegalArgumentException("Only support JPEG or PNG!");
        }
        Intent intent = new Intent(context, PenaActivity.class);
        intent.putExtra(PenaConfig.class.getSimpleName(), config);
        return intent;
    }

    public static boolean hasResult(int requestCode, int resultCode, Intent data) {
        return requestCode == PenaConstants.RC_DRAWING_PEN
                && resultCode == Activity.RESULT_OK
                && data != null;
    }

    public static String getFilePath(Intent intent) {
        if (intent == null) {
            return null;
        }

        return intent.getStringExtra(PenaConstants.EXTRA_FILE_PATH);
    }

    public static int LANDSCAPE = 0;
    public static int PORTRAIT = 1;

    public static int JPEG = 0;
    public static int PNG = 1;
}
