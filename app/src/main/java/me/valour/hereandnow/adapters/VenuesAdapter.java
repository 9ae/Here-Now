package me.valour.hereandnow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.valour.hereandnow.R;
import me.valour.hereandnow.objects.Venue;

/**
 * Created by alice on 12/6/14.
 */
public class VenuesAdapter extends ArrayAdapter<Venue> {

    public VenuesAdapter(Context context, List<Venue> items) {
        super(context, me.valour.hereandnow.R.layout.list_item_venue, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Venue li = this.getItem(position);

        View returnView;

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            returnView = inflater.inflate(R.layout.list_item_venue, parent, false);
        } else {
            returnView = convertView;
        }

        ViewHolder holder = new ViewHolder();
        holder.venueName = (TextView) returnView.findViewById(R.id.venue_name);
        holder.venueAddress = (TextView) returnView.findViewById(R.id.venue_address);
        returnView.setTag(holder);

        holder.setValues(li);

        return returnView;
    }

    static class ViewHolder {
        TextView venueName;
        TextView venueAddress;
        int position;

        void setValues(Venue venue){
            venueName.setText(venue.name);
            venueAddress.setText(venue.address);
        }
    }

}
