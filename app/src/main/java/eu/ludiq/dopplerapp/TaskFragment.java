package eu.ludiq.dopplerapp;

import android.app.Activity;
import android.app.Fragment;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Arrays;

import eu.ludiq.dopplerapp.fft.FastFourierTransformer;
import eu.ludiq.dopplerapp.fft.Frequency;
import eu.ludiq.dopplerapp.graphics.FrequencyGraph;

/**
 * Created by Loek on 18-4-2015.
 */
public class TaskFragment extends Fragment {

    public static final int RECORD_AUDIO = 1;

    private int taskID;
    private Activity activity;
    private AsyncTask currentTask;
    private boolean updateTask;

    public interface ActivityUpdateListener {
        void onActivityUpdate();
    }

    public static TaskFragment getFragment(int id) {
        TaskFragment fragment = new TaskFragment();
        fragment.taskID = id;

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("LOEK: ", "onAttach");

        this.activity = activity;
        if(currentTask != null) {
            updateTask = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(updateTask) {
            ((ActivityUpdateListener) currentTask).onActivityUpdate();
            updateTask = false;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("LOEK: ", "onDetach");

        this.activity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LOEK: ", "onCreate");

        setRetainInstance(true);
        if(taskID == RECORD_AUDIO) {
            currentTask = new RecordAudio().execute();
        }
        // Nog andere id evt.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("LOEK: ", "onDestroy");

        currentTask.cancel(true);
    }

    private class RecordAudio extends AsyncTask<Void, Frequency, Void> implements ActivityUpdateListener {
        private static final String TAG = "RecordAudio";
        private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
        private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
        private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
        private static final int BLOCK_SIZE = 256;
        private static final int SAMPLE_RATE = 8000;
        private static final int WAITING_TIME = 100;

        private long lastResult = 0;
        private TextView statusTextView;
        private FrequencyGraph graph;
        private FastFourierTransformer transformer = new FastFourierTransformer(BLOCK_SIZE);

        public void onActivityUpdate() {
            Log.d("LOEK: ", "onActivityUpdate");

            statusTextView = (TextView) activity.findViewById(R.id.statusTextView);
            graph = (FrequencyGraph) activity.findViewById(R.id.freq_graph);

            Log.d("LOEK: ", graph.toString() + statusTextView.toString());
        }

        @Override
        protected void onPreExecute() {
            onActivityUpdate();
        }

        @Override
        protected Void doInBackground(Void... params) {

            int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_ENCODING);
            AudioRecord audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_ENCODING, bufferSize);

            short[] buffer = new short[BLOCK_SIZE];
            double[] x = new double[BLOCK_SIZE], y = new double[BLOCK_SIZE];
            Frequency[] frequencies = new Frequency[BLOCK_SIZE / 2];

            try {
                audioRecord.startRecording();  //Start
            } catch (Exception e) {
                Log.e(TAG, "Recording Failed", e);
            }

            while (!isCancelled()) {
                int bufferReadResult = audioRecord.read(buffer, 0, BLOCK_SIZE);

                long curTime = System.currentTimeMillis();
                if (curTime < lastResult + WAITING_TIME) {
                    continue;
                }
                lastResult = curTime;

                for (int i = 0; i < BLOCK_SIZE && i < bufferReadResult; i++) {
                    x[i] = (double) buffer[i] / 32768.0;
                    y[i] = 0.0;
                }

                transformer.fft(x, y);

                for (int i = 0; i < frequencies.length; i++) {
                    double mag = Math.hypot(x[i], y[i]);
                    double f = (double) SAMPLE_RATE * i / BLOCK_SIZE;
                    frequencies[i] = new Frequency(f, mag);
                }

                publishProgress(frequencies);
            }
            try {
                audioRecord.stop();
            } catch (IllegalStateException e) {
                Log.e(TAG, "Stop failed", e);
            }

            return null;
        }

        protected void onProgressUpdate(Frequency... frequencies) {
            graph.setFrequencies(frequencies);
            graph.invalidate();

            // print the frequency
            Arrays.sort(frequencies);
            StringBuilder info = new StringBuilder("Frequencies: ");
            for (int i = 0; i < 4 && i < frequencies.length; i++) {
                info.append("\nf = ").append(String.format("%.5f", frequencies[i].frequency));
                info.append(", m = ").append(String.format("%.5f", frequencies[i].magnitude));
            }
            statusTextView.setText(info);

        }

        @Override
        public void onCancelled(Void result) {
            PickerDialog.newInstance(new String[] {"henk"}, 1).show(getFragmentManager(), "FreqDialog");
        }
    }
}
