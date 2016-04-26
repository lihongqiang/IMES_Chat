package com.iems5722.group9;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by Rachel on 24/02/2016.
 */
public class httptasktoken extends AsyncTask<String, Void, Boolean> {

    //private static final String TAG = "HTTP_TASK";

    @Override
    protected Boolean doInBackground(String... urls) {
        // perfomed on Background Thread
        return true;
    }

//    public String HttpGetUrltoken(String url){
//        String str = "";
//        InputStream is = null;
//        try {
//            URL link = new URL(url);
//            HttpURLConnection connection = (HttpURLConnection) link.openConnection();
//            connection.setReadTimeout(10000);
//            connection.setConnectTimeout(15000);
//            connection.setRequestMethod("GET");
//            connection.setDoInput(true);
//
//            connection.connect();
//            int response = connection.getResponseCode();
//            is = connection.getInputStream();
//            String line;
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//
//            while ((line = br.readLine()) != null ){
//                str += line;
//            }
//            if (is != null){
//                is.close();
//            }
//        } catch (MalformedURLException e){
//            Log.e("Failed", e.getMessage());
//        } catch (IOException e){
//            Log.e("Failed", e.getMessage());
//        } finally {
//
//        }
//        return str;
//    }

    public Boolean HttpPostUrltoken(String url, String token, String user_id){

        try {
            URL link = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) link.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));

            Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter("user_id", user_id);
            builder.appendQueryParameter("token", token);

            String query = builder.build().getEncodedQuery();
            Log.d("query",query);
            bw.write(query);
            bw.flush();
            bw.close();

            os.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK){
                return true;
            } else
                return false;

        } catch (MalformedURLException e){
            Log.e("Failed", e.getMessage());
        } catch (IOException e){
            Log.e("Failed", e.getMessage());
        } finally {

        }
        return false;
    }


}



