package me.valour.hereandnow.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import me.valour.hereandnow.R;

public class FindVenueFragment extends Fragment {
    private static final String ARG_PARAM1 = "fourSquareToken";

    // TODO: Rename and change types of parameters
    private String fourSquareToken;

    private FindVenueFragmentListener mListener;

    private EditText searchField;

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
     //   searchField.setHint(fourSquareToken);
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
        // TODO: Update argument type and name

    }

}
