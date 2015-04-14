package eu.ludiq.dopplerapp;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;

import eu.ludiq.dopplerapp.fft.FastFourierTransformer;
import eu.ludiq.dopplerapp.fft.Frequency;

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

        this.statusTextView = (TextView) findViewById(R.id.statusTextView);
        this.startStopButton = (Button) findViewById(R.id.startStopButton);

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
        if (started) {
            started = false;
            startStopButton.setText(getString(R.string.start));
            recordTask.cancel(true);
        } else {
            started = true;
            startStopButton.setText(getString(R.string.stop));
            recordTask = new RecordAudio();
            recordTask.execute();
        }
    }

    private class RecordAudio extends AsyncTask<Void, Frequency, Void> {

        @Override
        protected void onPreExecute() {
            statusTextView.setText("");
        }

        @Override
        protected Void doInBackground(Void... params) {
            int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioEncoding);
            AudioRecord audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioEncoding, bufferSize);

            short[] buffer = new short[blockSize];
            double[] x = new double[blockSize], y = new double[blockSize];

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

                Frequency[] frequencies = new Frequency[blockSize];
                for (int i = 0; i < blockSize; i++) {
                    double mag = Math.hypot(x[i], y[i]);
                    double f = sampleRate * mag / blockSize;
                    frequencies[i] = new Frequency(f, mag);
                }

                // calls onProgressUpdate
                // publishes the frequency
                publishProgress(frequencies);
            }
            try {
                audioRecord.stop();
            } catch (IllegalStateException e) {
                Log.e("Stop failed", e.toString());
            }
            return null;
        }

        protected void onProgressUpdate(Frequency... frequencies) {
            // print the frequency
            Arrays.sort(frequencies);
            StringBuilder info = new StringBuilder("Frequencies: ");
            for (int i = 0; i < 10 && i < frequencies.length; i++) {
                info.append("\nf = ").append(String.format("%.5f", frequencies[i].frequency));
                info.append(", m = ").append(String.format("%.5f", frequencies[i].magnitude));
            }
            statusTextView.setText(info);
        }
    }
}
