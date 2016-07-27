package app.me.daiz.shokujincamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class Utils {
    public static Camera.Size setCameraPreviewSize (Camera cam) {
        Camera.Parameters params = cam.getParameters();
        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        Camera.Size previewSize = previewSizes.get(0);
        params.setPictureSize(previewSize.width, previewSize.height);
        cam.setParameters(params);
        return previewSize;
    }

    public static Bitmap drawTextInPhoto (Bitmap bitmap, String text, int color) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(bitmap.getWidth() / 2);
        paint.setAntiAlias(true);
        float textWidth = paint.measureText(text);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float x = bitmap.getWidth() / 2;
        float y = bitmap.getHeight() / 2;
        x -= textWidth / 2;
        y -= (fontMetrics.ascent + fontMetrics.descent) / 2;
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawText(text, x, y, paint);
        return bitmap;
    }

    // 縦長写真の中央正方形部分を切り出す．
    public static Bitmap cropCenterSquare (Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int margin = (height - width) / 2;
        bitmap = Bitmap.createBitmap(bitmap, 0, margin, width, width);
        return bitmap;
    }

    // landscapeモードで撮影された画像を,portraitに変換する
    public static Bitmap getPortraitPhoto (Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return bitmap;
    }

    public static FrameLayout.LayoutParams getCameraLayoutViewSize (int width, Camera.Size size) {
        double rate = (double) size.height / width;
        int height = (int) (size.width * rate);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        return layoutParams;
    }

    public static void autoMediaScan (Context appContext, String[] paths) {
        String[] mimeTypes = {"image/jpeg"};
        MediaScannerConnection.scanFile(
                appContext, paths, mimeTypes, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.d("Scanned FilePath", "-> path=" + path);
                        Log.d("ContentProvider URI", "-> uri=" + uri);
                    }
                }
        );
    }

    public static void savePhotoToLocalStorage (Context appContext, Bitmap bitmap, String dirName, String fileName) {
        try {
            File dir = new File(Environment.getExternalStorageDirectory(), dirName);
            if (!dir.exists()) dir.mkdir();

            File f = new File(dir, fileName);
            String filePath = f.getAbsolutePath();
            FileOutputStream fs = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fs);
            fs.flush();
            fs.close();
            Toast.makeText(appContext, "カシャッッ", Toast.LENGTH_SHORT).show();

            // MediaScanner
            String[] paths = {filePath};
            Utils.autoMediaScan(appContext, paths);
        } catch (Exception e) {}
    }
}
