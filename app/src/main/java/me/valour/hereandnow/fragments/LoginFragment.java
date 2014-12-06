package me.valour.hereandnow.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;

import me.valour.hereandnow.R;
import me.valour.hereandnow.constants.Himitsu;


public class LoginFragment extends Fragment {

    private LoginFragmentListener mListener;

    public static final int REQUEST_CODE_FSQ_CONNECT = 1101;
    public static final int REQUEST_CODE_FSQ_TOKEN_EXCHANGE = 1102;


    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_login, container, false);

        Button login = (Button) rootView.findViewById(R.id.btn_login4sq);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCode();
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (LoginFragmentListener) activity;
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

    private void requestCode(){
        Intent intent = FoursquareOAuth.getConnectIntent(getActivity().getApplicationContext(), Himitsu.FourSquare.clientId);
        startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
    }

    private void requestToken(String code){
        Intent intent = FoursquareOAuth.getTokenExchangeIntent(getActivity().getApplicationContext(),
                Himitsu.FourSquare.clientId, Himitsu.FourSquare.clientSecret, code);
        startActivityForResult(intent, REQUEST_CODE_FSQ_TOKEN_EXCHANGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_FSQ_CONNECT:
                AuthCodeResponse codeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data);
                requestToken(codeResponse.getCode());
                break;

            case REQUEST_CODE_FSQ_TOKEN_EXCHANGE:
                AccessTokenResponse tokenResponse = FoursquareOAuth.getTokenFromResult(resultCode, data);
                mListener.setFourSquareToken(tokenResponse.getAccessToken());
                break;
        }
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
    public interface LoginFragmentListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);

        public void setFourSquareToken(String token);
    }

}
