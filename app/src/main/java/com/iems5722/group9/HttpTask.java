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

import com.iems5722.group9.*;
import com.iems5722.group9.Knowledge;

/**
 * Created by Rachel on 24/02/2016.
 */
public class HttpTask extends AsyncTask<String, Void, Boolean> {

    //private static final String TAG = "HTTP_TASK";

    @Override
    protected Boolean doInBackground(String... urls) {
        // perfomed on Background Thread
        return true;
    }

    public String HttpGetUrl(String url){
        String str = "";
        InputStream is = null;
        try {
            URL link = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) link.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();
            int response = connection.getResponseCode();
            is = connection.getInputStream();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null ){
                str += line;
            }
            if (is != null){
                is.close();
            }
        } catch (MalformedURLException e){
            Log.e("Failed", e.getMessage());
        } catch (IOException e){
            Log.e("Failed", e.getMessage());
        } finally {

        }
        return str;
    }

    public Boolean HttpPostUrl(String url, com.iems5722.group9.Knowledge knowledge, String Id){

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
            builder.appendQueryParameter("chatroom_id", Id);
            builder.appendQueryParameter("user_id", knowledge.UId);
            builder.appendQueryParameter("name", knowledge.UName);
            builder.appendQueryParameter("message", knowledge.message);
            builder.appendQueryParameter("flag",knowledge.flag);
            builder.appendQueryParameter("latitude",knowledge.latitude);
            builder.appendQueryParameter("longitude",knowledge.longitude);

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



