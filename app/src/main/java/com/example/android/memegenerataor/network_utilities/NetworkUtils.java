package com.example.android.memegenerataor.network_utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NetworkUtils {
    public static final String URL_STRING = "https://meme-api.herokuapp.com/gimme/wholesomememes/1";

    public static String fetchEarthquakeData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        jsonResponse = makeHttpRequest(url);

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        String memeUrl = extractEarthquakes(jsonResponse);

        // Return the list of {@link Earthquake}s
        return memeUrl;
    }

    public static String extractEarthquakes(String json) {

        // Create an empty ArrayList that we can start adding earthquakes to
        String memeUrl = null;

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject root = new JSONObject(json);
            JSONArray memeArray = root.getJSONArray("memes");
            for (int i = 0;i<memeArray.length();i++){
                JSONObject memeData = memeArray.getJSONObject(i);
                memeUrl = memeData.getString("url");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("QueryUtils", "Problem parsing the meme JSON results", e);
        }

        // Return the list of earthquakes
        return memeUrl;
    }
    public static String makeHttpRequest(URL url){
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        }catch (IOException e){
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            // function must handle java.io.IOException here
            if (inputStream != null) {
                urlConnection.disconnect();
            }
        }
        return jsonResponse;
    }
    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e("malformed", "Error with creating URL", exception);
            return null;
        }
        return url;
    }
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

}
