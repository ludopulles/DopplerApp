package eu.ludiq.dopplerapp;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;

import eu.ludiq.dopplerapp.fft.FastFourierTransformer;
import eu.ludiq.dopplerapp.fft.Frequency;
import eu.ludiq.dopplerapp.graphics.FrequencyGraph;

public class RealTimeFourier extends Activity {

    private static final String TAG = "RealTimeFourier";
    /**
     * Audio source is the device MIC
     */
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    /**
     * Recording in mono
     */
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    /**
     * Records in 16bit
     */
    private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * deal with this many samples at a time
     */
    private static final int BLOCK_SIZE = 256;
    /**
     * Sample rate in Hz
     */
    private static final int SAMPLE_RATE = 8000;

    /**
     * The time between two fourier analysises in ms
     */
    private static final int WAITING_TIME = 100;

    private FastFourierTransformer transformer = new FastFourierTransformer(BLOCK_SIZE);

    /**
     * Creates a Record Audio command
     */
    private RecordAudio recordTask;

    private TextView statusTextView;
    private Button startStopButton;
    private FrequencyGraph graph;

    private boolean isRunning = false;
    private long lastResult = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_fourier);

        this.statusTextView = (TextView) findViewById(R.id.statusTextView);
        this.startStopButton = (Button) findViewById(R.id.startStopButton);
        this.graph = (FrequencyGraph) findViewById(R.id.freq_graph);

        this.startStopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onStartStopButtonClicked();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onStartStopButtonClicked() {
        if (isRunning) {
            startStopButton.setText(getString(R.string.start));
            recordTask.cancel(true);
        } else {
            startStopButton.setText(getString(R.string.stop));
            recordTask = new RecordAudio();
            recordTask.execute();
        }
        isRunning = !isRunning;
    }

    private class RecordAudio extends AsyncTask<Void, Frequency, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_ENCODING);
            AudioRecord audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_ENCODING, bufferSize);

            short[] buffer = new short[BLOCK_SIZE];
            double[] x = new double[BLOCK_SIZE], y = new double[BLOCK_SIZE];
            Frequency[] frequencies = new Frequency[BLOCK_SIZE];

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

                for (int i = 0; i < BLOCK_SIZE; i++) {
                    double mag = Math.hypot(x[i], y[i]);
                    double f = SAMPLE_RATE * i / BLOCK_SIZE;
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
    }
}
