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

import eu.ludiq.dopplerapp.fft.FastFourierTransformer;

public class RealTimeFourier extends Activity {

    /**
     * Audio source is the device MIC
     */
    private int audioSource = MediaRecorder.AudioSource.MIC;
    /**
     * Recording in mono
     */
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    /**
     * Records in 16bit
     */
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * deal with this many samples at a time
     */
    private int blockSize = 256;
    /**
     * Sample rate in Hz
     */
    private int sampleRate = 8000;
    /**
     * the frequency given
     */
    private double frequency = 0.0;

    private FastFourierTransformer transformer = new FastFourierTransformer(blockSize);

    /**
     * Creates a Record Audio command
     */
    private RecordAudio recordTask;

    private TextView statusTextView;
    private Button startStopButton;

    private volatile boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_fourier);

        statusTextView = (TextView) findViewById(R.id.statusTextView);
        startStopButton = (Button) findViewById(R.id.startStopButton);

        startStopButton.setOnClickListener(new View.OnClickListener() {

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


    private class RecordAudio extends AsyncTask<Void, Double, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioEncoding);
            AudioRecord audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioEncoding, bufferSize);

            short[] buffer = new short[blockSize];

            double[] x = new double[blockSize], y = new double[blockSize];
            double[] magnitude = new double[blockSize];

            statusTextView.setText("Hello");
            try {
                audioRecord.startRecording();  //Start
            } catch (Throwable t) {
                Log.e("AudioRecord", "Recording Failed");
            }

            while (started) {
                int bufferReadResult = audioRecord.read(buffer, 0, blockSize);
                for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
                    x[i] = (double) buffer[i] / 32768.0;
                    y[i] = 0.0;
                }

                transformer.fft(x, y);

                for (int i = 0; i < blockSize; i++) {
                    magnitude[i] = Math.hypot(x[i], y[i]);
                }

                // Get the largest magnitude peak
                double peak = -1.0;
                for (int i = 0; i < blockSize; i++) {
                    if (peak < magnitude[i]) {
                        peak = magnitude[i];
                    }
                }

                // calculated the frequency
                frequency = (sampleRate * peak) / blockSize;

                // calls onProgressUpdate publishes the frequency
                publishProgress(frequency);
                try {
                    audioRecord.stop();
                } catch (IllegalStateException e) {
                    Log.e("Stop failed", e.toString());
                }
            }
            return null;
        }

        protected void onProgressUpdate(Double... frequencies) {
            //print the frequency
            String info = Double.toString(frequencies[0]);
            statusTextView.setText(info);
        }
    }

    public void onStartStopButtonClicked() {
        if (started) {
            started = false;
            startStopButton.setText("Start");
            recordTask.cancel(true);
        } else {
            started = true;
            startStopButton.setText("Stop");
            recordTask = new RecordAudio();
            recordTask.execute();
        }
    }

}
