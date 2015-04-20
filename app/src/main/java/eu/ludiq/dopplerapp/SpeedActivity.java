package eu.ludiq.dopplerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import eu.ludiq.dopplerapp.audio.SpeedCalculator;


public class SpeedActivity extends ActionBarActivity {

    public static final String EXTRA_FREQUENCY_APPROACHING = "approaching_frequency";
    public static final String EXTRA_FREQUENCY_LEAVING = "leaving_frequency";

    private SpeedCalculator calculator = new SpeedCalculator();
    private TextView tvApproaching, tvLeaving, tvSpeedOfSound, tvSpeedResultMS, tvSpeedResultKMH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);

        // get all text views
        this.tvApproaching = (TextView) findViewById(R.id.approaching_freq);
        this.tvLeaving = (TextView) findViewById(R.id.leaving_freq);
        this.tvSpeedOfSound = (TextView) findViewById(R.id.speed_of_sound);
        this.tvSpeedResultMS = (TextView) findViewById(R.id.speed_result_m_s);
        this.tvSpeedResultKMH = (TextView) findViewById(R.id.speed_result_km_h);

        // get the extras
        double frequencyApproaching = getIntent().getDoubleExtra(EXTRA_FREQUENCY_APPROACHING, -1.0);
        double frequencyLeaving = getIntent().getDoubleExtra(EXTRA_FREQUENCY_LEAVING, -1.0);

        // get the speed of sound setting
        double speedOfSound = SettingsActivity.getSpeedOfSound(this, -1.0);

        if (frequencyApproaching == -1.0 || frequencyLeaving == -1.0) {
            // nothing selected, stop
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        if (speedOfSound == -1.0) {
            // setting must be set first
            startActivity(new Intent(this, SettingsActivity.class));
        }

        // set appropriate value
        setText(this.tvApproaching, R.string.approaching_freq_text, frequencyApproaching);
        setText(this.tvLeaving, R.string.leaving_freq_text, frequencyLeaving);
        setText(this.tvSpeedOfSound, R.string.speed_of_sound_text, speedOfSound);

        // calculate speed
        calculator.setSpeedOfSound(speedOfSound);
        double resultMS = calculator.getSpeedOfObject(frequencyApproaching, frequencyLeaving);
        double resultKMH = resultMS * 3.6;

        // set speed
        setText(this.tvSpeedResultMS, R.string.result_m_s_text, resultMS);
        setText(this.tvSpeedResultKMH, R.string.result_km_h_text, resultKMH);
    }

    private void setText(TextView tv, int resId, double value) {
        tv.setText(String.format(getString(resId), value));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_speed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
