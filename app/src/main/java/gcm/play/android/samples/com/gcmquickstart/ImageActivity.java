package gcm.play.android.samples.com.gcmquickstart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
    Uri selectedImage;

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

                if(selectedImage == null){
                    Log.e("choose", "choose");
                    Toast.makeText(getBaseContext(), "Please choose picture", Toast.LENGTH_LONG).show();
                    return;
                }
                Imageprev.buildDrawingCache();
                Bitmap image = Imageprev.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                //image is the bitmap object
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                //get the encodedImage

                //send the encodedImage to ChatActivity
                Intent intent = new Intent();
                intent.setClass(ImageActivity.this, ChatActivity.class);
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
            selectedImage = data.getData();
            Imageprev.setImageURI(selectedImage);
        }
    }
}
