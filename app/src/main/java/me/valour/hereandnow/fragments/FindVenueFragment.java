package me.valour.hereandnow.fragments;

import android.app.Activity;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import me.valour.hereandnow.R;
import me.valour.hereandnow.adapters.VenuesAdapter;
import me.valour.hereandnow.constants.Himitsu;
import me.valour.hereandnow.objects.Venue;

public class FindVenueFragment extends Fragment {
    private static final String ARG_PARAM1 = "fourSquareToken";

    private String fourSquareToken;

    private FindVenueFragmentListener mListener;

    private ArrayList<Venue> foundVenues;

    private EditText searchField;
    private ListView listView;
    private VenuesAdapter listAdapter;

    private String currentCoords = null;

    // TODO: Rename and change types and number of parameters
    public static FindVenueFragment newInstance(String token) {
        FindVenueFragment fragment = new FindVenueFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, token);
        fragment.setArguments(args);
        return fragment;
    }

    public FindVenueFragment() {
        // Required empty public constructor
    }

    public void updateCoords(double lat, double lon){
        currentCoords = String.format("%f,%f", lat, lon);
    }

    public void getVenues(String query){
        if(currentCoords==null){
            return;
        }
        String url = "https://api.foursquare.com/v2/venues/search?";
        url+="client_id="+ Himitsu.FourSquare.clientId;
        url+="&client_secret="+Himitsu.FourSquare.clientSecret;
        url+="&v="+Himitsu.FourSquare.version;
        url+="&limit=50";
        url+="&intent=checkin";
        url+="&ll="+currentCoords;
        if(query!=null){
            url+="&query="+query;
        }
        Log.d("url", url);

        Ion.with(this).load(url).asJsonObject().setCallback(new FutureCallback<JsonObject>(){
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                foundVenues.clear();
                if(result==null){
                    return;
                }
                JsonObject response = result.get("response").getAsJsonObject();
                JsonArray venues = response.get("venues").getAsJsonArray();
                Log.d("test","venues returned "+venues.size());
                for(int i=0; i<venues.size(); i++){
                    JsonObject jsonVenue = venues.get(i).getAsJsonObject();
                    Venue venue = new Venue(jsonVenue);
                    foundVenues.add(venue);
                }
                if(foundVenues.size()>0){
                    listAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    public void prepareCheckin(Venue venue){
        mListener.setCurrentVenue(venue);

        String url = "https://api.foursquare.com/v2/checkins/add";
        url+="?oauth_token="+fourSquareToken;
        url+="&venueId="+venue.fourSquareId;
        url+="&v="+Himitsu.FourSquare.version;
        Log.d("test",url);

        Ion.with(this)
                .load(url)
                .setJsonObjectBody(new JsonObject())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(result==null){
                            return;
                        }
                        JsonObject response = result.get("response").getAsJsonObject();
                        JsonObject checkinObject = response.get("checkin").getAsJsonObject();
                        String checkinId = checkinObject.get("id").getAsString();

                        Log.d("test","your checkin id is "+checkinId);
                        mListener.setCheckinId(checkinId);
                    }
                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fourSquareToken = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_find_venue, container, false);
        searchField = (EditText) view.findViewById(R.id.input_search_venue);
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    String queryValue = searchField.getText().toString();
                    if(!queryValue.isEmpty()){
                        getVenues(queryValue);
                    }
                    return false;
                } else {
                    return true;
                }
            }
        });

        listView = (ListView) view.findViewById(R.id.lv_venues);

        foundVenues = new ArrayList<Venue>();
        listAdapter = new VenuesAdapter(getActivity(), foundVenues);
        listAdapter.setNotifyOnChange(true);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                prepareCheckin(foundVenues.get(position));
            }
        });

        Location location = mListener.getLocation();
        if(location!=null){
            updateCoords(location.getLatitude(),location.getLongitude());
            getVenues(null);
        } else {
            currentCoords = null;
            Log.d("error","coordinates not found");
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FindVenueFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface FindVenueFragmentListener {
        public Location getLocation();

        public void setCheckinId(String checkinId);

        public void setCurrentVenue(Venue venue);
    }

}
