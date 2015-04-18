package eu.ludiq.dopplerapp;

import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class RealTimeFourier extends ActionBarActivity {

    private FragmentManager fm;
    private Button startStopButton;
    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_fourier);

        fm = getFragmentManager();

        this.startStopButton = (Button) findViewById(R.id.startStopButton);
        this.startStopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onStartStopButtonClicked();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // When activity is recreated (orientation change)
        if(!isRunning && fm.findFragmentByTag("Joop") != null) {
            isRunning = true;
            startStopButton.setText(getString(R.string.stop));
        }
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
            fm.beginTransaction().remove(fm.findFragmentByTag("Joop")).commit();
        } else {
            startStopButton.setText(getString(R.string.stop));
            fm.beginTransaction().add(TaskFragment.getFragment(TaskFragment.RECORD_AUDIO), "Joop").commit();
        }
        isRunning = !isRunning;
    }

    /*private class RecordAudio extends AsyncTask<Void, Frequency, Void> {

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

                Log.d("Debug: ", "Frequency should be: " + numPeriods * SAMPLE_RATE / BLOCK_SIZE);
                int numPeriods = 14
                for (int i = 0; i < BLOCK_SIZE; i++) {
                    x[i] = Math.sin((2 * Math.PI * numPeriods) * ((double) i / BLOCK_SIZE));
                    Log.d("Amplitude " + i + ": ", String.valueOf(x[i]));
                    y[i] = 0.0;
                }

                transformer.fft(x, y);

                for (int i = 0; i < BLOCK_SIZE / 2; i++) {
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
    }*/
}
