package com.hypersaiph.bookseller.HttpClient;

import android.content.Context;
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

public class HttpTask extends AsyncTask<String, String, String>{
    public ResponseInterface delegate = null;
    // This is the JSON body of the post
    private JSONObject postData;
    private String method;
    private String flag;
    private String token;
    // This is a constructor that allows you to pass in the JSON body
    public HttpTask(Map<String, String> postData, String method, String token, ResponseInterface responseInterface) {
        this.delegate = responseInterface;
        this.method = method;
        this.token = token;
        if (postData != null) {
            this.postData = new JSONObject(postData);
        }
    }
    public HttpTask(Map<String, String> postData, String method, String flag, String token, ResponseInterface responseInterface) {
        this.delegate = responseInterface;
        this.method = method;
        this.flag = flag;
        this.token = token;
        if (postData != null) {
            this.postData = new JSONObject(postData);
        }
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            //Log.e("params", params[0]);
            Log.e("url", params[0]);
            // This is getting the url from the string we passed in
            URL url = new URL(params[0]);
            // Create the urlConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(15000);
            if(method.equals("GET")){
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Authorization", "Bearer "+token);
            }else{
                if(method.equals("POST") || method.equals("PUT") || method.equals("PATCH")){
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Authorization", "Bearer "+token);
                    // Send the post body
                    if (this.postData != null) {
                        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                        writer.write(postData.toString());
                        writer.flush();
                    }
                }
            }
            int statusCode = urlConnection.getResponseCode();
            String response = "error";
            if (statusCode ==  200) {
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                response = convertStreamToString(inputStream);
            }
            return response;
        } catch (Exception e) {
            Log.e("POST ERROR", e.getLocalizedMessage());
        }
        return "error";
    }
    @Override
    protected void onPostExecute(String result) {
        delegate.requestFinished(result, method, flag);
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
