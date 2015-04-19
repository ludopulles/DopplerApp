package eu.ludiq.dopplerapp;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Loek on 19-4-2015.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
