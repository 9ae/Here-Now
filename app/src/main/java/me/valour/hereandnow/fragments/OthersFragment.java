package me.valour.hereandnow.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import me.valour.hereandnow.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OthersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OthersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OthersFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "venue_id";


    // TODO: Rename and change types of parameters
    private String venueId;
    private ArrayList<String> imageURLs;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment OthersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OthersFragment newInstance(String param1) {
        OthersFragment fragment = new OthersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    public OthersFragment() {
        // Required empty public constructor
    }

    public String constructAstraStreamUrl(String checkinId){
        return "https://api.astra.io/v0/bucket/"+venueId+"/stream/"+checkinId;
    }

    public void getOtherCheckinsList(){
        imageURLs = new ArrayList<String>();
        Ion.with(getActivity()).load("http://hereandnowserver.appspot.com/checkin?venue_id="+venueId)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject jsonObject) {
                JsonArray idsArray = jsonObject.get("checkin_list").getAsJsonArray();
                for(int i=0; i<idsArray.size(); i++){
                    String id = idsArray.get(i).getAsString();
                    imageURLs.add( constructAstraStreamUrl(id) );
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            venueId = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_others, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
           // mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
       // public void onFragmentInteraction(Uri uri);
    }

}
