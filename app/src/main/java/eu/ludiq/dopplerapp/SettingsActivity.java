package eu.ludiq.dopplerapp;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

public class SettingsActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.preferenceView, new SettingsFragment()).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static double getSpeedOfSound(Context context, double defValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

//        Set<String> keys = prefs.getAll().keySet();
//        Log.e("test", "num keys found = " + keys.size());
//        for (String s : keys) {
//            Log.e("test", "Key = " + s + ", value = " + prefs.getAll().get(s).toString() + "; class=" + prefs.getAll().get(s).getClass().getName());
//        }

        // although it is a numeric preference, it is saved as a string
        String key = context.getString(R.string.key_speed_of_sound);
        try {
            return Double.parseDouble(prefs.getString(key, null));
        } catch (NumberFormatException e) {
        } catch (NullPointerException e) {
        }
        return prefs.getFloat(key, (float) defValue);
    }
}
