package com.example.tiagotoscano.freetec;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private EditText cpflogin;
    private static final String TAG = "MainActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (Utility.getCpfSettings(this).equals("")) {

            setContentView(R.layout.activity_main);
            cpflogin = (EditText) findViewById(R.id.cpfLogin);;

        }else{
            //setContentView(R.layout.activity_main);
            //cpflogin = (EditText) findViewById(R.id.cpfLogin);;

            setContentView(R.layout.activity_table_time);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");

        }

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Log.e(TAG, "GCM Registration Token Main");

            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);


        }

        //MainActivityFragment alunoFramgent = ((MainActivityFragment) getSupportFragmentManager()
          //      .findFragmentById(R.id.fragment_main));


       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
       // getSupportActionBar().setTitle("");




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

            getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    public void clickLogin(View view){

        if(cpflogin.getText().toString().equals(""))
            Toast.makeText(this, "Digite seu CPF!", Toast.LENGTH_LONG).show();
        else{
            if (Utility.validateCPF(cpflogin.getText().toString())) {

                if (checkPlayServices()) {
                    // Start IntentService to register this application with GCM.
                    Log.e(TAG, "GCM Registration Token Main");

                    Intent intent = new Intent(this, RegistrationIntentService.class);
                    startService(intent);


                }

                Utility.setCpfSettings(this, cpflogin.getText().toString());
                Intent list = new Intent(this,TableTimeActivity.class);

                startActivity(list);

            }else{
                Toast.makeText(this, "CPF Invalido!", Toast.LENGTH_LONG).show();
            }

        }

    }
    public void clickLog (View view){

        Utility.setCpfSettings(this, "");
        Intent inte = getIntent();

        finish();
        startActivity(inte);

    }

    private boolean checkPlayServices() {
        Log.i(TAG, "checkPlay.");
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        Log.i(TAG, "This device  supported.");
        return true;
    }


}
