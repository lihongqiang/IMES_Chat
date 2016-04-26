package com.iems5722.group9;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.security.auth.login.LoginException;

import com.iems5722.group9.GoogleMapActivity;
import com.iems5722.group9.HttpTask;
import com.iems5722.group9.ImageActivity;
import com.iems5722.group9.Knowledge;
import com.iems5722.group9.ListAdapter;
import com.iems5722.group9.PhotoTask;
import com.iems5722.group9.R;

//import gcm.play.android.samples.com.gcmquickstart.R;


public class ChatActivity extends AppCompatActivity  {

    private List<com.iems5722.group9.Knowledge> list = new ArrayList<com.iems5722.group9.Knowledge>();
    com.iems5722.group9.Knowledge knowledge;
    String page, all_page;
    com.iems5722.group9.ListAdapter adapter;
    Button imagesend;
    Button audiosend ;
    TextToSpeech textToSpeech;
    String message;
    ContextMenuDialogFragment mMenuDialogFragment;
    FragmentManager fragmentManager;
    String flag;

    double latitude = 0;
    double longitude = 0;
    String Id;

//    private static final String IP = "http://52.196.20.101";
    private static final String IP = "http://54.238.173.183";


    //常量
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final String APP_TAG = "AudioRecorder";

    //audio send
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = {
            MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP
    };
    private String file_exts[] = {
            AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP
    };
    private String filePath;//记录语音存储路径

    //音频播放
//    MediaPlayer mediaPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        Id = extras.getString("ChatRoom_ID");
        String chatroom_name = extras.getString("chatroom_name");

        adapter = new ListAdapter(ChatActivity.this, list);
        //------------------------------------------------------
        final AutoCompleteTextView chatedit = (AutoCompleteTextView) findViewById(R.id.et_content);

        getResources().getStringArray(R.array.countries_array);
        String[] countries = getResources().getStringArray(R.array.countries_array);
        ArrayAdapter<String> adapter_1 =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        chatedit.setAdapter(adapter_1);
        //-----------------------------------------------------------

        
        final ListView listView = (ListView) findViewById(R.id.lv_content);

        imagesend = (Button) findViewById(R.id.btn_image);
        audiosend = (Button) findViewById(R.id.btn_audio);

        listView.setAdapter(adapter);
        registerForContextMenu(listView);

        fragmentManager = getSupportFragmentManager();
        initMenuFragment();


        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(Locale.UK);
                    }
                }
            });
        }

        //Click the button to choose the image from the album; Then relocate to imageactivity;
        findViewById(R.id.btn_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ChatActivity.this, com.iems5722.group9.ImageActivity.class);
                startActivityForResult(intent, 1000);

            }
        });

        //Keep holding the button to record the audio and then send it to the server;
        findViewById(R.id.btn_audio).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("AudioRecorder", "Start Recording");
                        filePath = startRecording();
                        Log.e("file_path", filePath);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("AudioRecorder", "stop Recording");
                        send_audio(filePath);
                        break;
                }
                return false;
            }

        });

        findViewById(R.id.btn_action).setOnClickListener(new View.OnClickListener() { // 发送按钮点击
            @Override
            public void onClick(View v) {
                EditText messageInput = (EditText) findViewById(R.id.et_content);
                String text = messageInput.getText().toString();
                // Empty text check
                if (text == null || text.isEmpty() || text.trim().isEmpty()) {
                    // Update an alert
                    new AlertDialog.Builder(ChatActivity.this).setMessage("Input should not be Empty! ").setPositiveButton("OK", null).show();
                    return;
                }
                if (chatedit.getText().length() > 0 ) {
                    Calendar calder = Calendar.getInstance();
                    String time = calder.get(Calendar.HOUR) + ":" + calder.get(Calendar.MINUTE);
                    flag = "0";
                    knowledge = new com.iems5722.group9.Knowledge("1155071415", "Rachel", chatedit.getText().toString(), time, flag, "0", "0");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            com.iems5722.group9.HttpTask HttpPostUrl = new com.iems5722.group9.HttpTask();
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                            if (HttpPostUrl.HttpPostUrl(IP + "/iems5722/send_message", knowledge, Id)) {
                                list.add(knowledge);
                                adapter.notifyDataSetChanged();
                                chatedit.getText().clear();
                            }
                        }
                    });
                }
            }
        });

        page = "1";
        all_page = "10000";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                com.iems5722.group9.HttpTask HttpGetUrl = new com.iems5722.group9.HttpTask();
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                String data = HttpGetUrl.HttpGetUrl(IP + "/iems5722/get_messages?chatroom_id=" + Id + "&page=" + page);
                Log.d("data", data);
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("messages");
                    all_page = jsonObject.getJSONObject("data").getString("total_pages");
                    Log.d("array", jsonArray.toString());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        String message = jsonArray.getJSONObject(i).getString("message");
                        String timestamp = jsonArray.getJSONObject(i).getString("timestamp");
                        String name = jsonArray.getJSONObject(i).getString("name");
                        String id = jsonArray.getJSONObject(i).getString("user_id");
                        String flag= jsonArray.getJSONObject(i).getString("flag");
                        String Know_latitude = jsonArray.getJSONObject(i).getString("latitude");
                        String Know_longitude = jsonArray.getJSONObject(i).getString("longitude");
                        list.add(0, new com.iems5722.group9.Knowledge(id, name, message, timestamp, flag, Know_latitude, Know_longitude));
                    }
                    adapter.notifyDataSetChanged();
                    //        first_page=true;
                } catch (JSONException ex) {
                    Log.e("JSONException", ex.getMessage());
                }
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView v, int first, int visible, int total) {

                if (first == 0) {
                    Log.d("first", "first");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            com.iems5722.group9.HttpTask HttpGetUrl = new com.iems5722.group9.HttpTask();
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                            if (Integer.valueOf(page) < Integer.valueOf(all_page)) {
                                page = String.valueOf(Integer.parseInt(page) + 1);
                                String data = HttpGetUrl.HttpGetUrl(IP + "/iems5722/get_messages?chatroom_id=" + Id + "&page=" + page);
                                try {
                                    JSONObject jsonObject = new JSONObject(data);
                                    JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("messages");

                                    Log.d("array", jsonArray.toString());

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        String message = jsonArray.getJSONObject(i).getString("message");
                                        String timestamp = jsonArray.getJSONObject(i).getString("timestamp");
                                        String name = jsonArray.getJSONObject(i).getString("name");
                                        String id = jsonArray.getJSONObject(i).getString("user_id");
                                        String flag = jsonArray.getJSONObject(i).getString("flag");
                                        String Know_latitude = jsonArray.getJSONObject(i).getString("latitude");
                                        String Know_longitute = jsonArray.getJSONObject(i).getString("longitude");
                                        list.add(0, new com.iems5722.group9.Knowledge(id, name, message, timestamp, flag, Know_latitude, Know_longitute));
                                    }
                                    Runnable r = new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                            listView.invalidateViews();
                                        }
                                    };
                                    Handler h = new Handler();
                                    h.postDelayed(r, 100);
                                } catch (JSONException ex) {
                                    Log.e("JSONException", ex.getMessage());
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                com.iems5722.group9.Knowledge knowledge = list.get(position);
                if(knowledge.getFlag() == "2"){     //对于谷歌图片可以点击查看
                    //show google maps position
                    Intent intent = new Intent();
                    intent.setClass(ChatActivity.this, com.iems5722.group9.GoogleMapActivity.class);
                    intent.putExtra("longitude", Double.parseDouble(knowledge.getLongitude()));
                    intent.putExtra("latitude", Double.parseDouble(knowledge.getLatitude()));
                    startActivity(intent);
                }
                if(knowledge.getFlag() == "3"){     //对于音频文件可以播放
                    byte[] decodedBytes = Base64.decode(list.get(position).getMessage(), 0);
                    playMp3(decodedBytes);
                }
            }
        });

        //获取当前坐标的一种方法
//        GPSTracker gps = new GPSTracker(ChatActivity.this);
//
//        // check if GPS enabled
//        if(gps.canGetLocation()){
//
//            latitude = gps.getLatitude();
//            longitude = gps.getLongitude();
//
//            // \n is for new line
//            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
//        }else{
//            // can't get location
//            // GPS or Network is not enabled
//            // Ask user to enable GPS/network in settings
//            gps.showSettingsAlert();
//        }
    }

    //get the result from the ImageActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == 1001)
        {
            adapter = new com.iems5722.group9.ListAdapter(ChatActivity.this, list);
            //final EditText chatedit = (EditText) findViewById(R.id.et_content);
            final ListView listView = (ListView) findViewById(R.id.lv_content);

            listView.setAdapter(adapter);
            registerForContextMenu(listView);

            String result_value = data.getStringExtra(com.iems5722.group9.ImageActivity.EXTRA_MESSAGE);

            postPhotoToServer(result_value, "1");

        }
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                LatLng latLng = place.getLatLng();
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

//                获取google static map picture
                if(latLng != null){
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;
                    getGoogleMapPhoto(latLng);
                }
            }
        }
    }

    void postPhotoToServer(String result_value, String image_flag){
        Calendar calder = Calendar.getInstance();
        String time = calder.get(Calendar.HOUR) + ":" + calder.get(Calendar.MINUTE);
//        flag = "1";
        knowledge = new com.iems5722.group9.Knowledge("1155071415", "Rachel_left", result_value, time, image_flag, ""+latitude, ""+longitude);
        Log.e("PostToServer", "" + latitude + ", "+longitude);
        //Log.e("result: ",result_value);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                com.iems5722.group9.HttpTask HttpPostUrl = new com.iems5722.group9.HttpTask();
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Log.e("postToServer", "postToServer");
                if (HttpPostUrl.HttpPostUrl(IP + "/iems5722/send_message", knowledge, Id)) {
                    Log.e("postToServer", "postToServer");
                    list.add(knowledge);
                    adapter.notifyDataSetChanged();
                    Log.e("postToServer", "postToServer");
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lv_content ) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            //是文本才能转换成语音
            if(list.get(info.position).getFlag() == "0"){
                message = list.get(info.position).getMessage();
                menu.setHeaderTitle("Operations");
                String[] menuItems = getResources().getStringArray(R.array.menu);
                for (int i = 0; i < menuItems.length; i++) {
                    menu.add(Menu.NONE, i, i, menuItems[i]);
                }
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        //choose text to speech
        if (menuItemIndex == 0) {
            Log.e("choose text", "choose text");
            Log.e("speak message", message);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        } else if (menuItemIndex == 1) {   //choose cancel
            return true;
        }
        return true;
    }

    @Override
    protected void onPause() {
//        mediaPlayer.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        }

//        if(mediaPlayer != null){
//            mediaPlayer.stop();
//        }
        super.onDestroy();
    }

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(View clickedView, int position) {
                if (position == 0) {          //refresh
                    page = "0";
                    list.clear();
                    adapter.notifyDataSetChanged();
                } else if (position == 1) {

                    //Google Places API for Android
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                    try {
                        startActivityForResult(builder.build(ChatActivity.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }

                } else if (position == 2) {    //close

                }
            }
        });
    }

    private List<MenuObject> getMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject refresh = new MenuObject("Refresh Message");
        refresh.setResource(R.drawable.refresh);

        MenuObject send = new MenuObject("Show Location");
        send.setResource(R.drawable.icn_1);

        MenuObject close = new MenuObject("Close Menu");
        close.setResource(R.drawable.icn_close);


        menuObjects.add(refresh);
        menuObjects.add(send);
        menuObjects.add(close);

        return menuObjects;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // 生成获取相应图片的uri,启动获取图片的异步任务
    public void getGoogleMapPhoto(LatLng latLng) {
        Log.e("getGoogleMapPhoto", "getGoogleMapPhoto");
        int width = 200*2;
        int height = 150*2;

        Log.e("mImageView", width + ", " + height);

        String uriString = "https://maps.googleapis.com/maps/api/staticmap?" +
                "markers=size:big%7Ccolor:red%7C" + latLng.latitude + "," + latLng.longitude +
                "&zoom=15" +
                "&size=" + width + "x" + height +
                "&scale=2" +
                "&format=png" +
                "&maptype=roadmap" +
                "&key=AIzaSyBdw0JQ7MvKHky8m0bEQCk1kDJpzUpb6AI";
        Log.e("uriString", uriString);
        startPhotosTask(uriString);
    }

    // 开启获取图片的异步任务
    private void startPhotosTask(String uri) {

        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        new com.iems5722.group9.PhotoTask() {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
            }

            @Override
            protected void onPostExecute(String encodedImage) {
                if (encodedImage != null && !encodedImage.equals("")) {
                    postPhotoToServer(encodedImage, "2");
                }
            }
        }.execute(uri);
    }

    //--------------------------------------------------------------------------------------
    // When click down the button, start to record;
    private String startRecording(){
        String path = getFilename();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(path);
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    // get name of file;
    private String getFilename(){
        String dirpath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(dirpath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Log.i(APP_TAG, "Error: " + what + ", " + extra);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Log.i(APP_TAG, "Warning: " + what + ", " + extra);
        }
    };

    public void send_audio(String path) {
        stopRecording();
        // release the button,then send the result audio to server;
        Log.e("path", path);
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String encodedaudio = Base64.encodeToString(bytes,0);
        Calendar calder = Calendar.getInstance();
        String time = calder.get(Calendar.HOUR) + ":" + calder.get(Calendar.MINUTE);
        knowledge = new com.iems5722.group9.Knowledge("1155071415", "Rachel", encodedaudio, time, "3", "0", "0");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                com.iems5722.group9.HttpTask HttpPostUrl = new com.iems5722.group9.HttpTask();
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                if (HttpPostUrl.HttpPostUrl("http://54.238.173.183/iems5722/send_message", knowledge, Id)) {
                    list.add(knowledge);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    //stop recording
    private void stopRecording(){
        if(null != recorder){
            recorder.stop();
            recorder.reset();
            recorder.release();

            recorder = null;

        }
    }

    //音频播放，将byte数组转换成音频后播放
    private void playMp3(byte[] mp3SoundByteArray) {
        try {
            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("kurchina", "mp4", getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();

            // Tried reusing instance of media player
            // but that resulted in system crashes...
//            if(mediaPlayer == null){
//                mediaPlayer = new MediaPlayer();
//            }
            MediaPlayer mediaPlayer = new MediaPlayer();

            // Tried passing path directly, but kept getting
            // "Prepare failed.: status=0x1"
            // so using file descriptor instead
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());

            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }

}
