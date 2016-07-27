package app.me.daiz.shokujincamera;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

import java.util.List;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    SurfaceView   sview;
    SurfaceHolder sholder;
    Camera camera;

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
            try {

            } catch (Exception e) {
            }
            Toast.makeText(getApplicationContext(), "カシャッッッ", Toast.LENGTH_SHORT).show();
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

