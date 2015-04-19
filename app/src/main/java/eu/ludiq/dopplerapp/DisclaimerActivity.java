package eu.ludiq.dopplerapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

/**
 * Created by Loek on 19-4-2015.
 */
public class DisclaimerActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        TextView aboutView = (TextView) findViewById(R.id.disclaimerView);
        aboutView.setMovementMethod(new ScrollingMovementMethod());
    }
}
