package app.me.daiz.shokujincamera;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.net.HttpURLConnection;
import java.net.URL;

public class ShokujinClassifierAPI {
    private String apiBaseUrl = "https://shokujin-classifier.herokuapp.com/api/";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    ShokujinClassifierAPI () {
    }

    public String classify (String base64jpg) throws JSONException {
        JSONObject requestJSON = new JSONObject();
        requestJSON.put("jpg", base64jpg);
        String requestJSONstr = requestJSON.toString();
        Log.v("jsonstr", requestJSONstr);

        HttpURLConnection con = null;
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, requestJSONstr);
            Request request = new Request.Builder().url(this.apiBaseUrl).post(body).build();
            Response response = client.newCall(request).execute();
            Log.v("response", response.body().string());
            return "ok";
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "??";
    }

}
