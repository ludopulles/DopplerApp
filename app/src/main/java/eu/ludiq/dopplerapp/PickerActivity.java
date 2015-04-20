package eu.ludiq.dopplerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import java.io.Serializable;

import eu.ludiq.dopplerapp.audio.Frequency;
import eu.ludiq.dopplerapp.graphics.FrequencyGraph;
import eu.ludiq.dopplerapp.graphics.FrequencyPicker;
import eu.ludiq.dopplerapp.audio.AudioSample;

public class PickerActivity extends ActionBarActivity {

    private static final String TAG = "PickerActivity";
    public static final String EXTRA_SAMPLE_DATA = "sample_data";

    private FrequencyPicker[] pickers;

    @Override
    protected void onCreate(Bundle data) {
        super.onCreate(data);
        setContentView(R.layout.activity_picker);

        Serializable sampleData = getIntent().getSerializableExtra(EXTRA_SAMPLE_DATA);
        if (sampleData != null && sampleData instanceof AudioSample[]) {
            AudioSample[] samples = (AudioSample[]) sampleData;

            if (samples.length == 2) {
                this.pickers = new FrequencyPicker[]{

                        new FrequencyPicker(samples[0],
                                (FrequencyGraph) findViewById(R.id.approaching_graph),
                                (SeekBar) findViewById(R.id.approaching_seek_bar)),

                        new FrequencyPicker(samples[1],
                                (FrequencyGraph) findViewById(R.id.leaving_graph),
                                (SeekBar) findViewById(R.id.leaving_seek_bar)),

                };
            } else {
                Log.e(TAG, "Other amount of samples expected, found = " + samples.length);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_calculate) {
            // TODO: put data in the intent
            Intent intent = new Intent(this, SpeedActivity.class);

            Frequency freqA = this.pickers[0].getSelectedFrequency();
            Frequency freqL = this.pickers[1].getSelectedFrequency();

            if (freqA != null)
                intent.putExtra(SpeedActivity.EXTRA_FREQUENCY_APPROACHING, freqA.frequency);
            if (freqL != null)
                intent.putExtra(SpeedActivity.EXTRA_FREQUENCY_LEAVING, freqL.frequency);

            startActivity(intent);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
