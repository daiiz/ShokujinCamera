package app.me.daiz.shokujincamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShokujinClassifierAPI {
    private String apiBaseUrl = "https://shokujin-classifier.herokuapp.com/api/";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public String requestJSONstr;
    private Bitmap photo;
    private Context appContext;
    private String CAM_DIR = "ShokujinCamera";

    ShokujinClassifierAPI (Context app, Bitmap bitmap) {
        photo = bitmap;
        appContext = app;
    }

    public void classify () throws JSONException {
        String base64jpg = Utils.base64encode(photo, true);
        JSONObject requestJSON = new JSONObject();
        requestJSON.put("jpg", base64jpg);
        requestJSONstr = requestJSON.toString();
        Log.v("jsonstr", requestJSONstr);

        new AsyncTask<Void, Void, String> () {
            @Override
            protected String doInBackground(Void... params) {
                String teishokuNo = "??";
                RequestBody body = RequestBody.create(JSON, requestJSONstr);
                Request request = new Request.Builder().url(apiBaseUrl + "classify").post(body).build();
                OkHttpClient client = new OkHttpClient();
                try {
                    Response response = client.newCall(request).execute();
                    JSONObject resJson = new JSONObject(response.body().string());
                    teishokuNo = resJson.getString("description").split("-")[1];
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return teishokuNo;
            }

            @Override
            protected void onPostExecute(String teishokuNo) {
                // 写真の中央に文字を追加する
                photo = Utils.drawTextInPhoto(photo, teishokuNo, Color.WHITE);
                // 写真をローカルに保存
                String fname = "img"+Math.floor(Math.random() * 10000)+".jpg";
                Utils.savePhotoToLocalStorage(appContext, photo, CAM_DIR, fname);
                Log.v("Congratulations!!!", teishokuNo);
                Toast.makeText(appContext, "保存しました", Toast.LENGTH_LONG).show();
                MainActivity.progress = false;
            }
        }.execute();
    }

}
