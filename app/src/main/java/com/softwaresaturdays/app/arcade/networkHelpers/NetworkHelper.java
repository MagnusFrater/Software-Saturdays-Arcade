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

    public static void sendNotifications(final String message, final String userId, final Activity context) {
        // Create an asynchronous network request
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    // Create URL with parameters
                    String url_FCM = "https://fcm.googleapis.com/fcm/send";

                    URL fcmEndpoint = new URL(url_FCM);

                    // Automatic caching
                    HttpResponseCache myCache = HttpResponseCache.install(context.getCacheDir(), 100000L);

                    // Create connection: Default GET Request
                    HttpsURLConnection myConnection =
                            (HttpsURLConnection) fcmEndpoint.openConnection();

                    // Set POST request format
                    myConnection.setRequestMethod("POST");

                    // Enable writing
                    myConnection.setDoOutput(true);

                    // Add headers
                    myConnection.addRequestProperty("Content-Type", "application/json");
                    myConnection.addRequestProperty("Authorization", "key=AAAABtPUbQ4:APA91bE2bQB2U0R9lf18_vIKivQ8KdplyLWKbx88bsZDj4Id8qJkc7SWaLGDMr5otUGaPSCIT2_DjsFRzWmxG8d7kj_-tgKldm095m3YnOxjywl7QHOUrA09XYRazCVYFYXBlojLwTdB");

                    // Create the data
                    String myData = "{\n" +
                            "  \"to\": \"/topics/arcade\",\n" +
                            "  \"data\": {\n" +
                            "    \"message\": \"" + message + "\"\n" +
                            "    \"userId\": \"" + userId + "\"\n" +
                            "  }\n" +
                            "}";
                    // Write the data
                    myConnection.getOutputStream().write(myData.getBytes());

                    if (myConnection.getResponseCode() == 200) {
                        // Success
                        // Further processing here

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
            JsonObject images = gif.getAsJsonObject("images");

            String gifUrl = images.getAsJsonObject("fixed_height").getAsJsonPrimitive("url").getAsString();

            listener.onFetchedGifUrl(gifUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public interface OnFetchSuccessListener {
        void onFetchedGifUrl(String gifUrl);
    }
}
