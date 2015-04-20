package eu.ludiq.dopplerapp.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

import eu.ludiq.dopplerapp.fft.FastFourierTransformer;

public class AudioRecorder extends AsyncTask<Void, Frequency, Void> {

    private static final String TAG = "AudioRecorder";

    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private static final int BLOCK_SIZE = 256;
    private static final int SAMPLE_RATE = 8000;

    private static final int WAITING_TIME = 100;

    private long lastResult = 0;
    private FastFourierTransformer transformer = new FastFourierTransformer(BLOCK_SIZE);

    @Override
    protected Void doInBackground(Void... params) {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_ENCODING);
        AudioRecord audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_ENCODING, bufferSize);

        short[] buffer = new short[BLOCK_SIZE];
        double[] x = new double[BLOCK_SIZE], y = new double[BLOCK_SIZE];

        try {
            audioRecord.startRecording();  //Start
        } catch (IllegalStateException e) {
            Log.e(TAG, "Recording Failed", e);
            throw e;
        }

        Log.i(TAG, "Starting recording");

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

            Frequency[] frequencies = new Frequency[BLOCK_SIZE / 2];
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
            throw e;
        }
        Log.i(TAG, "Recording has stopped");
        return null;
    }
}