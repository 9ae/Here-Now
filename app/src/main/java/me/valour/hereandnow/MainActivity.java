package me.valour.hereandnow;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import me.valour.hereandnow.constants.Himitsu;
import me.valour.hereandnow.fragments.LoginFragment;


public class MainActivity extends Activity implements LoginFragment.LoginFragmentListener {

    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm  = getFragmentManager();
        if (savedInstanceState == null) {
            fm.beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
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

        fm.beginTransaction()
                .replace(R.id.container, new PlaceholderFragment())
                .commit();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
