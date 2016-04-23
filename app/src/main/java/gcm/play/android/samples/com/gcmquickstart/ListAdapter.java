package gcm.play.android.samples.com.gcmquickstart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Rachel on 24/02/2016.
 */
public class ListAdapter extends BaseAdapter {


        private Context mContext;
        private List<Knowledge> list;
        private Knowledge knowledge;
        private LayoutInflater inflater;
        //private TextView name,Id;

        public ListAdapter(Context context, List< Knowledge> list) {
            inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //if flag = 0, send the text message
            if (list.get(position).getFlag() == "0") {
                if (list.get(position).left) {
                    convertView = inflater.inflate( R.layout.left, null);
                } else {
                    //layout the list of text message
                    convertView = inflater.inflate( R.layout.item_content_list, null);
                }
                Object object = getItem(position);
                TextView chatname = (TextView)convertView.findViewById(R.id.chatname);
                chatname.setText(list.get(position).UName);
                TextView chatcontent = (TextView)convertView.findViewById(R.id.chatcontent);
                chatcontent.setText(list.get(position).getMessage());
                TextView chattime = (TextView)convertView.findViewById(R.id.chattime);
                chattime.setText(list.get(position).getTime());
                return convertView;
            } else {
                //flag = 1,send the image message
                if (list.get(position).left) {
                    convertView = inflater.inflate( R.layout.left, null);
                } else {
                    //layout the list of image message
                    convertView = inflater.inflate( R.layout.item_image_list, null);
                }
                Object object = getItem(position);
                TextView chatname = (TextView)convertView.findViewById(R.id.chatname);
                chatname.setText(list.get(position).UName);
                //get the image and show the image
                ImageView chatimage = (ImageView)convertView.findViewById(R.id.chatimage);
                byte[] decodedBytes = Base64.decode(list.get(position).getMessage(), 0);
                //Log.e("byte",list.get(position).getMessage());
                Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                chatimage.setImageBitmap(decodedImage);

                TextView chattime = (TextView)convertView.findViewById(R.id.chattime);
                chattime.setText(list.get(position).getTime());
                return convertView;
            }
        }


}
