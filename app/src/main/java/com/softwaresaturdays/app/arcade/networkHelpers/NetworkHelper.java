package com.softwaresaturdays.app.arcade.networkHelpers;

import android.app.Activity;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NetworkHelper {

    public static void fetchGIF(final String searchText, final Activity context, final OnFetchSuccessListener listener) {

        // Async task tutorial: https://code.tutsplus.com/tutorials/android-from-scratch-using-rest-apis--cms-27117

        // Create an asynchronous network request
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    // Create URL with parameters
                    String url_GIPHY = "https://api.giphy.com/v1/gifs/translate" +
                            "?api_key=" + "V8yx6xS8lx22xE9C6PK9jDdMJj0aGA7j" + "&s=" + searchText + "&weirdness=5";

                    URL giphyEndpoint = new URL(url_GIPHY);

                    // Automatic caching
                    HttpResponseCache myCache = HttpResponseCache.install(context.getCacheDir(), 100000L);

                    // Create connection: Default GET Request
                    HttpsURLConnection myConnection =
                            (HttpsURLConnection) giphyEndpoint.openConnection();

                    if (myConnection.getResponseCode() == 200) {
                        // Success
                        // Further processing here
                        processResponse(myConnection.getInputStream(), listener);
                    } else {
                        // Error handling code goes here
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private static void processResponse(InputStream responseBody, OnFetchSuccessListener listener) {
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(
                    new InputStreamReader(responseBody, "UTF-8"));

            JsonObject gif = jsonObject.getAsJsonObject("data");
            String gifUrl = gif.get("url").getAsString();

            listener.onFetchedGifUrl(gifUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public interface OnFetchSuccessListener {
        void onFetchedGifUrl(String gifUrl);
    }
}
