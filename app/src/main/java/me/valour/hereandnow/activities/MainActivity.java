package me.valour.hereandnow.activities;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;

import me.valour.hereandnow.R;
import me.valour.hereandnow.constants.Himitsu;
import me.valour.hereandnow.fragments.CheckInFragment;
import me.valour.hereandnow.fragments.FindVenueFragment;
import me.valour.hereandnow.fragments.LoginFragment;
import me.valour.hereandnow.objects.AstraUploader;
import me.valour.hereandnow.objects.Venue;


public class MainActivity extends Activity implements
        LoginFragment.LoginFragmentListener,
        FindVenueFragment.FindVenueFragmentListener,
        CheckInFragment.OnFragmentInteractionListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener{

    FragmentManager fm;
    Location mCurrentLocation;
    LocationClient mLocationClient;

    private String FourSquareCheckinId = null;
    private Venue currentVenue = null;
    private String selfieFilePath = null;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    static final int CAMERA_ACTIVITY = 1001;


    public void launchCapture(){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, CAMERA_ACTIVITY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mLocationClient.isConnected()){
            mLocationClient.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(this, this, this);
        setContentView(R.layout.activity_main);
        fm  = getFragmentManager();
        String fsToken = getToken(Himitsu.FourSquare.propKey);
        if(fsToken!=null){
            launchFindVenue(fsToken);
        } else {
            if (savedInstanceState == null) {
                fm.beginTransaction()
                        .add(R.id.container, new LoginFragment())
                        .commit();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Set user token
     * @param token
     */
    public void setToken(String key, String token){
        SharedPreferences sp = this.getPreferences(this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,token);
        editor.apply();
    }

    /**
     * Get user token
     * @return
     */
    public String getToken(String key){
        SharedPreferences sp = this.getPreferences(this.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void setFourSquareToken(String token) {
        setToken(Himitsu.FourSquare.propKey, token);
        launchFindVenue(token);
    }

    @Override
    public Location getLocation(){
        if (servicesConnected() && mLocationClient.isConnected()) {
            mCurrentLocation = mLocationClient.getLastLocation();
            return mCurrentLocation;
        } else {
            return null;
        }
    }

    @Override
    public void setCheckinId(String checkinId){
        FourSquareCheckinId = checkinId;
        launchCapture();
    }

    @Override
    public void setCurrentVenue(Venue venue){
        currentVenue = venue;
    }

    public void launchFindVenue(String token){
        FindVenueFragment fragment = FindVenueFragment.newInstance(token);
        fm.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void launchCheckin(){
        CheckInFragment fragment = CheckInFragment.newInstance(FourSquareCheckinId,
                getToken(Himitsu.FourSquare.propKey) ,null, currentVenue.name);
        fm.beginTransaction().replace(R.id.container, fragment).commit();
    }

    public void uploadToAstra(String filepath){
        if(currentVenue==null || FourSquareCheckinId==null){
            return;
        }
        selfieFilePath = filepath;
        AstraUploader uploader = new AstraUploader(filepath, currentVenue.fourSquareId, FourSquareCheckinId, this);
        FutureCallback<JsonObject> callback = new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject jsonObject) {
                Log.d("test", new Gson().toJson(jsonObject));
                if(jsonObject.get("ok").getAsBoolean()){
                    Log.d("test", "upload success");
                    launchCheckin();
                }
            }
        };
        uploader.startUpload(callback);
    }


    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason.
            // resultCode holds the error code.
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(fm, "Location Updates");
            }
            return false;
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */

                        break;
                }
             break;

            case CAMERA_ACTIVITY:
                if(resultCode==Activity.RESULT_OK){
                    if(data.getExtras().containsKey("image_path")) {
                        String image_path = data.getStringExtra("image_path");
                        Log.d("test",image_path);
                        uploadToAstra(image_path);
                       // Toast.makeText(this, image_path, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
      //  mLocationClient.connect();
        getLocation();
    }

    @Override
    public void onDisconnected() {
      //  mLocationClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

}
