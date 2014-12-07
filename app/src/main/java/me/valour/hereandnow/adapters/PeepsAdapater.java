package me.valour.hereandnow.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import me.valour.hereandnow.R;
import me.valour.hereandnow.constants.Himitsu;

/**
 * Created by alice on 12/7/14.
 */
public class PeepsAdapater extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> imageUrls;

    public PeepsAdapater(Context c, ArrayList<String> urls){
        mContext = c;
        imageUrls = urls;
    }

    public int getCount(){
        return imageUrls.size();
    }

    public String getItem(int position){
        return imageUrls.get(position);
    }

    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        CardHolder holder = new CardHolder();

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item_image, null);

            holder.img = (ImageView) v.findViewById(R.id.imageView);
            holder.self = v;

            v.setTag(holder);
        }
        else {
            holder = (CardHolder) v.getTag();
        }


        String url = imageUrls.get(position);
        Log.d("url", url);
        Ion.with(mContext)
                .load(url)
                .setHeader(Himitsu.Astra.authName,Himitsu.Astra.secret)
                .withBitmap()
                .placeholder(android.R.drawable.btn_star_big_on)
                .error(android.R.drawable.btn_star_big_off)
                .intoImageView(holder.img);

        return v;
    }

    private static class CardHolder{
        public ImageView img;
        public View self;
    }

}