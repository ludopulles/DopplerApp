package eu.ludiq.dopplerapp.graphics;

import android.util.Log;
import android.widget.SeekBar;

import eu.ludiq.dopplerapp.audio.AudioSample;
import eu.ludiq.dopplerapp.audio.Frequency;

public class FrequencyPicker {

    private static final String TAG = "FrequencyPicker";

    private AudioSample sample;
    private FrequencyGraph graph;
    private SeekBar seekBar;

    public FrequencyPicker(AudioSample sample, FrequencyGraph graph, SeekBar seekBar) {
        this.sample = sample;
        this.graph = graph;
        this.seekBar = seekBar;

        // TODO: add listeners

        setActiveSample(0);
        this.seekBar.setMax(this.sample.getSize() - 1);
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setActiveSample(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setActiveSample(int progress) {
//        Log.d(TAG, "Setting active sample to " + progress);
        this.graph.setFrequencies(this.sample.getSample(progress));
        this.graph.invalidate();
    }

    public Frequency getSelectedFrequency() {
        return this.graph.getSelectedFrequency();
    }

}
