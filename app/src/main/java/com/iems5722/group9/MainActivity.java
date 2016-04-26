package com.iems5722.group9;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.iems5722.group9.*;
import com.iems5722.group9.ChatActivity;
import com.iems5722.group9.QuickstartPreferences;
import com.iems5722.group9.RegistrationIntentService;

//import org.json.JSONTokener;

public class MainActivity extends AppCompatActivity {

    //public static final String KEY_NAME = "key_name";


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;
    private boolean isReceiverRegistered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(com.iems5722.group9.QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };

        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        // Registering BroadcastReceiver
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, com.iems5722.group9.RegistrationIntentService.class);
            startService(intent);
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //HttpTask httpget = new HttpTask();
        //String data = httpget.HttpGetUrl("http://104.155.195.255/iems5722/get_chatrooms");
        //ParseJson parseJson = new ParseJson(data, 0);
        //ArrayList info = parseJson.parse();

        final ArrayList<String> chatroom_name = new ArrayList<>();
        final ArrayList<String> Id = new ArrayList<>();

//        chatroom_name.add(info.get(0).toString());
//        chatroom_name.add(info.get(2).toString());
//        Id.add(info.get(1).toString());
//        Id.add(info.get(3).toString());

        final ListView listView = (ListView) findViewById(R.id.lv_content);
        final ArrayAdapter chatroomAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,chatroom_name);
        listView.setAdapter(chatroomAdapter);
        Log.d("hello", "world");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // 设置点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, com.iems5722.group9.ChatActivity.class);
                intent.putExtra("chatroom_name", chatroom_name.get(position));
                intent.putExtra("ChatRoom_ID", Id.get(position));
                startActivity(intent);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HttpTask HttpGetUrl = new HttpTask();
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                String data = HttpGetUrl.HttpGetUrl("http://54.238.173.183/iems5722/get_chatrooms");
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    Log.d("array", jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Id.add(jsonArray.getJSONObject(i).getString("id"));
                        chatroom_name.add(jsonArray.getJSONObject(i).getString("name"));
                    }
                    Log.d("chatroom_name", chatroom_name.toString());
                    chatroomAdapter.notifyDataSetChanged();
                } catch (JSONException ex) {
                    Log.e("JSONException", ex.getMessage());
                }
            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(com.iems5722.group9.QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


}
