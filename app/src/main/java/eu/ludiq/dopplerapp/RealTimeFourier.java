package eu.ludiq.dopplerapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Ludo on 14-4-2015.
 */
public class RealTimeFourier extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do shit
    }

    @Override
    public void onClick(View v) {
        // do more shit
    }

    /*
    int audioSource = MediaRecorder.AudioSource.MIC;    // Audio source is the device MIC
    int channelConfig = AudioFormat.CHANNEL_IN_MONO;    // Recording in mono
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT; // Records in 16bit

    private DoubleFFT_1D fft;                           // The fft double array
    private RealDoubleFFT transformer;
    int blockSize = 256;                               // deal with this many samples at a time
    int sampleRate = 8000;                             // Sample rate in Hz
    public double frequency = 0.0;                      // the frequency given

    RecordAudio recordTask;                             // Creates a Record Audio command
    TextView tv;                                        // Creates a text view for the frequency
    boolean started = false;
    Button startStopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView1);
        startStopButton = (Button) findViewById(R.id.button1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    private class RecordAudio extends AsyncTask<Void, Double, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioEncoding);
            AudioRecord audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioEncoding, bufferSize);
            short[] buffer = new short[blockSize];
            double[] re = new double[blockSize];
            double[] im = new double[blockSize];
            double[] magnitude = new double[blockSize];
            //   ----------------------------------------------------
            double[] toTransform = new double[blockSize];

            tv.setText("Hello");
            try {
                audioRecord.startRecording();  //Start
            } catch (Throwable t) {
                Log.e("AudioRecord", "Recording Failed");
            }

            while (started) {
                int bufferReadResult = audioRecord.read(buffer, 0, blockSize);
                for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
                    toTransform[i] = (double) buffer[i] / 32768.0;
                }

                // audiodataDoubles now holds data to work with
                // fft.complexForward(audioDataDoubles);
                transformer.ft(toTransform);

                // Calculate the Real and imaginary and Magnitude.
                for (int i = 0; i < blockSize; i++) {
                    re[i] = toTransform[i * 2];
                    im[i] = toTransform[(i * 2) + 1];
                    magnitude[i] = Math.sqrt((re[i] * re[i]) + (im[i] * im[i]));
                }

                double peak = -1.0;
                // Get the largest magnitude peak
                for (int i = 0; i < blockSize; i++) {
                    if (peak < magnitude[i])
                        peak = magnitude[i];
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
            tv.setText(info);
        }

    }

    @Override
    public void onClick(View v) {
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
*/
}
