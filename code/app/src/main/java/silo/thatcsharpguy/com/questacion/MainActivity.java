package silo.thatcsharpguy.com.questacion;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import jsqlite.Exception;
import silo.thatcsharpguy.com.questacion.dataaccess.MetrobusDatabase;
import silo.thatcsharpguy.com.questacion.entities.Estacion;
import silo.thatcsharpguy.com.questacion.services.LocationService;
import silo.thatcsharpguy.com.questacion.services.NuevaEstacionListener;


public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, NuevaEstacionListener{

    private static final String TAG = "MainActivity";

    LocationService mService;
    boolean mBound = false;

    AlertDialog.Builder _dialogBuilder;
    TextView _estacionCercanaText;
    Button _bindUnbindButton;
    LinearLayout _mainPanel;

    public static MetrobusDatabase MetrobusDatabase;

    public static GoogleApiClient GoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _dialogBuilder = new AlertDialog.Builder(this);

        _estacionCercanaText = (TextView)findViewById(R.id.estacionCercanaText);
        _mainPanel = (LinearLayout)findViewById(R.id.mainPanel);
        _bindUnbindButton = (Button)findViewById(R.id.bindUnbindButton);

        _bindUnbindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound == true)
                {
                    _bindUnbindButton.setText(R.string.start_trip);
                    StopAndUnbindLocationService();
                }
                else{
                    _bindUnbindButton.setText(R.string.stop_trip);
                    StartAndBindLocationService();
                }
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        int color = savedInstanceState.getInt("color");
        _mainPanel.getRootView().setBackgroundColor(color);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("color", 0xFF990000);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {


        if(Commons.getMainDatabaseFile().exists())
        {
            try {
                MetrobusDatabase = new MetrobusDatabase(Commons.getMainDatabaseFile());

                if (GoogleApiClient == null) {
                    GoogleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                }

                if(!GoogleApiClient.isConnected()) {
                    GoogleApiClient.connect();
                }else{
                    StartAndBindLocationService();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else
        {
            Intent intent = new Intent(this, DownloadDatabaseActivity.class);
            startActivity(intent);
        }

        super.onStart();
    }

    @Override
    protected void onDestroy() {
        StopAndUnbindLocationService();
        super.onDestroy();
    }

    Intent locationServiceIntent;

    void StartAndBindLocationService(){
        locationServiceIntent = new Intent(this, LocationService.class);
        startService(locationServiceIntent);
        bindService(locationServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    void StopAndUnbindLocationService(){
        if(mBound) {
            stopService(locationServiceIntent);
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onConnected(@Nullable Bundle bundle) {

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(GoogleApiClient);

        try {
            if(lastLocation != null)
            {
                Estacion cercana = MetrobusDatabase.getEstacionCercana(lastLocation.getLatitude(), lastLocation.getLongitude());
                stationUpdate(cercana);
            }
            StartAndBindLocationService();
            _bindUnbindButton.setText(R.string.stop_trip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            mService = binder.getService();
            mService.addListener(MainActivity.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void stationUpdate(Estacion cercana) {

        int color = 0xFF000000 | cercana.getColor();

        _estacionCercanaText.setText(cercana.getNombre());
        _estacionCercanaText.setTextColor(Color.WHITE);
        _mainPanel.getRootView().setBackgroundColor(color);

        color = Utils.darkenColor(color,0.8f);

        android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();

        supportActionBar.setBackgroundDrawable(new ColorDrawable(color));
        supportActionBar.setWindowTitle("Linea " + cercana.getLinea());

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Utils.darkenColor(color, 0.8f));

    }
}
