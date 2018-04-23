package id.yanuar.sample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;

import id.yanuar.pena.Pena;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image image = ImagePicker.getFirstImageOrNull(data);
            if (image != null) {
                Pena.create(MainActivity.this)
                        .filenamePrefix(getString(R.string.app_name))
                        .backgroundImage(image.getPath())
                        .fileDirectory(getString(R.string.app_name))
                        .orientation(Pena.PORTRAIT)
                        .toolbarTitle("Drawing On Image")
                        .start();
            }
        } else if (Pena.hasResult(requestCode, resultCode, data)) {
            String path = Pena.getFilePath(data);

            Glide.with(this)
                    .load(path)
                    .into(imageView);
        }
    }

    public void drawingOnImage(View view) {
        ImagePicker.create(this)
                .returnMode(ReturnMode.ALL)
                .single()
                .showCamera(true)
                .folderMode(true)
                .start();
    }

    public void drawing(View view) {
        Pena.create(MainActivity.this)
                .filenamePrefix(getString(R.string.app_name))
                .backgroundColor(Color.WHITE)
                .fileDirectory(getString(R.string.app_name))
                .toolbarTitle("Drawing Pad")
                .orientation(Pena.LANDSCAPE)
                .start();
    }
}
