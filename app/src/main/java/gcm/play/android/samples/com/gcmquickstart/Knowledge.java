package gcm.play.android.samples.com.gcmquickstart;

import android.util.Log;

/**
 * Created by Rachel on 24/02/2016.
 */
public class Knowledge {
    public String UId;
    public String UName;
    public String message;
    public String time;
    public Boolean left;
    public String flag;
    public double latitude;
    public double longitude;

    public Knowledge(String uid, String uname, String message,String time,String flag){
        this.UId = uid;
        this.UName = uname;
        this.message = message;
        this.time = time;
        this.flag = flag;
        this.latitude = 0;
        this.longitude = 0;


        if (uname.contentEquals("Rachel")){
            this.left = false;
        }
        else
            this.left = true;
        Log.e("left", String.valueOf(left));
    }

    public Knowledge(String uid, String uname, String message,String time,String flag,double latitude,double longitude){
        this.UId = uid;
        this.UName = uname;
        this.message = message;
        this.time = time;
        this.flag = flag;
        this.latitude = latitude;
        this.longitude = longitude;


        if (uname.contentEquals("Rachel")){
            this.left = false;
        }
        else
            this.left = true;
        Log.e("left", String.valueOf(left));
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMessage() {
        return message;
    }
    public String getTime() {return time;}
    public String getFlag() {return flag;}

    public void setMessage(String message){
        this.message = message;
    }

    public void setTime(String time){
        this.time = time;
    }
}
