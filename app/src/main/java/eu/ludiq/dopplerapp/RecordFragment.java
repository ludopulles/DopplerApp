package eu.ludiq.dopplerapp;

import android.app.Activity;
import android.app.Fragment;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import eu.ludiq.dopplerapp.audio.AudioRecorder;
import eu.ludiq.dopplerapp.graphics.FrequencyGraph;
import eu.ludiq.dopplerapp.audio.AudioSample;
import eu.ludiq.dopplerapp.audio.Frequency;

public class RecordFragment extends Fragment {

    private static final String TAG = "RecordFragment";

    private Activity activity;
    private AudioRecorder currentTask;

    private AudioSample[] samples = new AudioSample[]{new AudioSample(), new AudioSample()};
    private int sampleInsertIndex = 0;

    private FrequencyGraph graph;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.activity != null) {
            this.graph = (FrequencyGraph) this.activity.findViewById(R.id.freq_graph);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        clearRecordData();
        sampleInsertIndex = 0;

        // start recording immediately
        currentTask = new AudioRecorderImpl();
        currentTask.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // stop recording
        currentTask.cancel(true);
    }

    public void startNextStage() {
        if (sampleInsertIndex + 1 < samples.length) {
            sampleInsertIndex++;
        } else {
            Log.w(TAG, "Sample insert index has already been increased");
        }
    }

    public void clearRecordData() {
        for (AudioSample s : samples) {
            s.clearSamples();
        }
    }

    public AudioSample[] getRecordData() {
        return samples;
    }

    private class AudioRecorderImpl extends AudioRecorder {

        @Override
        protected void onProgressUpdate(Frequency... values) {
            if (graph != null) {
                graph.setFrequencies(values);
                graph.invalidate();
            }
            samples[sampleInsertIndex].addSample(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.e(TAG, "Result received");
        }
    }
}
