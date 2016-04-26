package com.iems5722.group9;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.iems5722.group9.*;
import com.iems5722.group9.ChatActivity;
import com.iems5722.group9.PictureUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Rachel on 14/04/2016.
 */
public class ImageActivity extends Activity{
    ImageView Imageprev,chatimage;
    Button imagechoose,imageup;
    boolean flag = false;

    private static final int RESULT_LOAD_IMAGE = 1;
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_main);

        //imagesend = (Button) findViewById(R.id.btn_image);
        imagechoose = (Button) findViewById(R.id.btn_click);
        imageup = (Button) findViewById(R.id.btn_up);
        Imageprev = (ImageView) findViewById(R.id.Imageprev);
        chatimage = (ImageView) findViewById(R.id.chatimage);

        findViewById(R.id.btn_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseimage();
            }
        });

        //when we click the "ok" button, we will send the encodedImage to ChatActivity
        //and then upload it to the server
        findViewById(R.id.btn_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!flag){
                    Log.e("choose", "choose");
                    Toast.makeText(getBaseContext(), "Please choose picture", Toast.LENGTH_LONG).show();
                    return;
                }
                Imageprev.buildDrawingCache();
                Bitmap image = Imageprev.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                //image is the bitmap object
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                //get the encodedImage

                //send the encodedImage to ChatActivity
                Intent intent = new Intent();
                intent.setClass(ImageActivity.this, com.iems5722.group9.ChatActivity.class);
                intent.putExtra(EXTRA_MESSAGE, encodedImage);
                setResult(1001, intent);
                finish();

            }
        });
    }

    public void chooseimage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            String path = getRealPathFromURI(uri);
            Log.e("choosePicPath", path);
//            selectedImage = data.getData();
//            Imageprev.setImageURI(selectedImage);
            flag = true;
            Imageprev.setImageBitmap(com.iems5722.group9.PictureUtil.getSmallBitmap(path));
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(contentUri, proj, null,
                null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

}
