package app.me.daiz.shokujincamera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class Grid extends View {
    float[] mesh;
    Paint paint;

    public Grid(Context context, int w, int h) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.DKGRAY);
        paint.setStrokeWidth(3);

        int m = (h - w) / 2;
        Log.v("w", ""+h);

        mesh = new float[]{
            0, m, w, m,
            0, (m+w), w, (m+w)
        };
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawLines(mesh, paint);
    }
}
