package eu.ludiq.dopplerapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import eu.ludiq.dopplerapp.audio.AudioSample;

public class RecordActivity extends ActionBarActivity {

    private static final String TAG = "RecordActivity";
    private static final String RECORD_FRAGMENT_NAME = "record_fragment";
    private static final int PICKER_REQUEST_CODE = 0;

    private static final String KEY_WAS_RUNNING = "wasRunning";

    private static final int STATUS_INIT = 0;
    private static final int STATUS_APPROACHING = 1;
    private static final int STATUS_LEAVING = 2;
    private static final int STATUS_STOPPED = 3;

    private FragmentManager manager;
    private int status;
    private Button recordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        this.manager = getFragmentManager();

        this.recordButton = (Button) findViewById(R.id.button_record);
        this.recordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RecordActivity.this.onClick();
            }
        });

        // TODO: get status, set appropriate button text
        initState();
        updateText();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICKER_REQUEST_CODE) {
            Log.d(TAG, "returned from picker activity");
            this.status = STATUS_INIT;
            initState();
            updateText();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle data) {
        // TODO: save status
        super.onSaveInstanceState(data);
    }

    private void onClick() {
        if (this.status == STATUS_INIT) {
            startRecording();
        } else if (this.status == STATUS_APPROACHING) {
            continueRecording();
        } else if (this.status == STATUS_LEAVING) {
            finishRecording();
        } else {
            return;
        }
        this.status++;
        updateText();
    }

    private void initState() {
        this.recordButton.setVisibility(View.VISIBLE);
    }

    private void startRecording() {
        FragmentTransaction transaction = this.manager.beginTransaction();
        transaction.add(new RecordFragment(), RECORD_FRAGMENT_NAME);
        transaction.commit();
    }

    private void continueRecording() {
        Fragment fragment = getFragmentManager().findFragmentByTag(RECORD_FRAGMENT_NAME);
        if (fragment != null && fragment instanceof RecordFragment) {
            ((RecordFragment) fragment).startNextStage();
        }
    }

    private void finishRecording() {
        Fragment fragment = getFragmentManager().findFragmentByTag(RECORD_FRAGMENT_NAME);

        // get the samples
        AudioSample[] samples = null;
        if (fragment != null && fragment instanceof RecordFragment) {
            RecordFragment recordFragment = ((RecordFragment) fragment);
            samples = recordFragment.getRecordData();
        }

        // remove the fragment
        FragmentTransaction transaction = this.manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commit();

        // load the new activity
        if (samples != null) {
            Intent intent = new Intent(this, PickerActivity.class);
            intent.putExtra(PickerActivity.EXTRA_SAMPLE_DATA, samples);
            startActivityForResult(intent, PICKER_REQUEST_CODE);
        }
    }

    private void updateText() {
        int resId = 0;
        if (this.status == STATUS_INIT) {
            resId = R.string.button_approaching;
        } else if (this.status == STATUS_APPROACHING) {
            resId = R.string.button_leaving;
        } else if (this.status == STATUS_LEAVING) {
            resId = R.string.button_stop_measure;
        } else if (this.status == STATUS_STOPPED) {
            // hide the button
            this.recordButton.setVisibility(View.INVISIBLE);
        }

        if (resId != 0) {
            this.recordButton.setText(resId);
        } else {
            this.recordButton.setText("");
        }
    }
}
