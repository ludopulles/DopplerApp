package eu.ludiq.dopplerapp;

import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


public class SettingsActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        if(savedInstanceState == null) {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.preferenceView, new SettingsFragment()).commit();
        }
    }
}
