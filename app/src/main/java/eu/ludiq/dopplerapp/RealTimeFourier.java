package eu.ludiq.dopplerapp;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class RealTimeFourier extends ActionBarActivity {

    private static final String RECORD_FRAGMENT_NAME = "record_fragment";

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

        if (savedInstanceState != null && savedInstanceState.containsKey("wasRunning") && savedInstanceState.getBoolean("wasRunning")) {
            isRunning = true;
            startStopButton.setText(getString(R.string.stop));
        }

        fm = getFragmentManager();
    }

    /*@Override
    public void onResume() {
        super.onResume();

        // When activity is recreated (orientation change)
        if (!isRunning && fm.findFragmentByTag(RECORD_FRAGMENT_NAME) != null) {
            isRunning = true;
            startStopButton.setText(getString(R.string.stop));
        }
    }*/

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("wasRunning", isRunning);
        super.onSaveInstanceState(savedInstanceState);
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
            fm.beginTransaction().remove(fm.findFragmentByTag(RECORD_FRAGMENT_NAME)).commit();
        } else {
            startStopButton.setText(getString(R.string.stop));
            fm.beginTransaction().add(RecordFragment.getFragment(RecordFragment.RECORD_AUDIO), RECORD_FRAGMENT_NAME).commit();
        }
        isRunning = !isRunning;
    }
}
