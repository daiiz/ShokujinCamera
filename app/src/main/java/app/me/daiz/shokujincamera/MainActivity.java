package app.me.daiz.shokujincamera;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    SurfaceView   sview;
    SurfaceHolder sholder;
    Camera camera;
    String CAM_DIR = "ShokujinCamera";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        FrameLayout frame = new FrameLayout(this);
        setContentView(frame);

        sview = new SurfaceView(this);
        sholder = sview.getHolder();
        sholder.addCallback(new SurfaceHolderCallback());

        Button btn = new Button(this);
        btn.setText("カシャ");
        btn.setLayoutParams(new LayoutParams(200, 150));
        btn.setOnClickListener(new TakePhotoClickListener());

        frame.addView(btn);
        frame.addView(sview);

    }

    class TakePhotoClickListener implements View.OnClickListener {
        @Override
        public void onClick (View view) {
            camera.takePicture(null, null, new TakePhoto());
        }
    }

    class TakePhoto implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String fname = "img"+Math.floor(Math.random()*10000)+".jpg";
            try {
                File dir = new File(Environment.getExternalStorageDirectory(), CAM_DIR);
                if (!dir.exists()) {
                    Toast.makeText(getApplicationContext(), "Oops!", Toast.LENGTH_SHORT).show();
                    dir.mkdir();
                }

                File f = new File(dir, fname);
                String filePath = f.getAbsolutePath();
                FileOutputStream fs = new FileOutputStream(f);
                fs.write(data);
                fs.close();
                Toast.makeText(getApplicationContext(), "カシャッッ", Toast.LENGTH_SHORT).show();

                // MediaScanner
                String[] paths = {filePath};
                String[] mimeTypes = {"image/jpeg"};
                MediaScannerConnection.scanFile(
                    getApplicationContext(), paths, mimeTypes, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d("スキャンしたファイルのパス", "-> path=" + path);
                            Log.d("ContentProviderのURI", "-> uri=" + uri);
                        }
                    }
                );

            } catch (Exception e) {
            }
            camera.startPreview();
        }
    }

    class SurfaceHolderCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            camera = Camera.open();
            Parameters param = camera.getParameters();
            List<Size> ss = param.getSupportedPictureSizes();
            Size pictureSize = ss.get(0);
            param.setPictureSize(pictureSize.width, pictureSize.height);
            camera.setParameters(param);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
            try {
                camera.setDisplayOrientation(0);
                camera.setPreviewDisplay(sview.getHolder());

                Parameters param = camera.getParameters();
                List<Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();
                Size preview = previewSizes.get(0);
                param.setPreviewSize(preview.width, preview.height);

                LayoutParams lp = new LayoutParams(preview.width, preview.height);
                sview.setLayoutParams(lp);

                camera.setParameters(param);
                camera.startPreview();

            } catch (Exception e) {
                System.out.print(e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            camera.stopPreview();
            camera.release();
        }
    }
}

