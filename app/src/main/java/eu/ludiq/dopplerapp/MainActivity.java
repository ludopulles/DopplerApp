package eu.ludiq.dopplerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button metingButton = (Button) findViewById(R.id.button_meting);
        Button overButton = (Button) findViewById(R.id.button_over);
        metingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MetingActivity
                startActivity(new Intent(MainActivity.this, AudioRecordTest.class));
            }
        });
        overButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //OverActivity
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
                Toast toast = Toast.makeText(this, "Sprite!", Toast.LENGTH_SHORT);
                toast.show();
        }

        return true;
        //return super.onOptionsItemSelected(item);
    }
}
