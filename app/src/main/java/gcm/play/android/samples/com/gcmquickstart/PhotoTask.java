package gcm.play.android.samples.com.gcmquickstart;

/**
 * Created by lhq on 16/4/21.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lhq on 16/4/21.
 * 异步获取google static map photo
 */

public class PhotoTask extends AsyncTask<String, Void, String> {

    public PhotoTask() {}

    /**
     * Loads the first photo for a place id from the Geo Data API.
     * The place id must be the first (and only) parameter.
     */
    @Override
    protected String doInBackground(String... params) {
        if (params.length != 1) {
            return null;
        }
        final String uri = params[0];
        return getHttpBitmap(uri);
    }

    /**
     * 获取网落图片资源
     * @param url
     * @return
     */
    public static String getHttpBitmap(String url){
        URL myFileURL;
        String encodedImage = "";
        try{
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            //压缩图片
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            //image is the bitmap object
            byte[] b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            //关闭数据流
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return encodedImage;

    }
}

