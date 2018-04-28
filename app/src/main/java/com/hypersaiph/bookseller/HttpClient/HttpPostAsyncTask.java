package com.hypersaiph.bookseller.HttpClient;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpPostAsyncTask extends AsyncTask<String, String, String>{
    public AsyncResponse delegate = null;
    // This is the JSON body of the post
    JSONObject postData;
    // This is a constructor that allows you to pass in the JSON body
    public HttpPostAsyncTask(Map<String, String> postData) {
        if (postData != null) {
            this.postData = new JSONObject(postData);
        }
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            //Log.e("params", params[0]);
            //Log.e("postData", postData.toString());
            // This is getting the url from the string we passed in
            URL url = new URL(params[0]);
            // Create the urlConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            // OPTIONAL - Sets an authorization header
            //urlConnection.setRequestProperty("Authorization", "someAuthString");
            // Send the post body
            if (this.postData != null) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(postData.toString());
                writer.flush();
            }

            int statusCode = urlConnection.getResponseCode();
            String response = "";
            if (statusCode ==  200) {
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                response = convertStreamToString(inputStream);
                // From here you can convert the string to JSON with whatever JSON parser you like to use
                // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method
            } else {
                // Status code is not 200
                // Do something to handle the error
            }
            return response;
        } catch (Exception e) {
            Log.e("POST ERROR", e.getLocalizedMessage());
        }
        return "error";
    }
    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
