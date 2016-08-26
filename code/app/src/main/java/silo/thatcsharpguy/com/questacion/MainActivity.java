package silo.thatcsharpguy.com.questacion;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import jsqlite.Exception;
import silo.thatcsharpguy.com.questacion.dataaccess.MetrobusDatabase;
import silo.thatcsharpguy.com.questacion.entities.Estacion;
import silo.thatcsharpguy.com.questacion.services.LocationService;


public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "MainActivity";

    private static final String WriteStorage = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String ReadStorage = android.Manifest.permission.READ_EXTERNAL_STORAGE;

    private static final String AccessCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String AccessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION;

    private static final int LOCATION_REQUEST = 2;
    private static final int STORAGE_REQUEST = 3;

    LocationService mService;
    boolean mBound = false;

    AlertDialog.Builder _dialogBuilder;
    TextView _estacionCercanaText;

    public static MetrobusDatabase MetrobusDatabase;

    public static GoogleApiClient GoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _dialogBuilder = new AlertDialog.Builder(this);
        _estacionCercanaText = (TextView)findViewById(R.id.estacionCercanaText);
    }

    @Override
    protected void onStart() {
        checkDatabase();
        super.onStart();
    }

    private void checkDatabase(){
        // Check for permissions
        int canReadStorage = ContextCompat.checkSelfPermission(this,ReadStorage);
        int canWriteStorage = ContextCompat.checkSelfPermission(this,WriteStorage);

        if(canReadStorage == PackageManager.PERMISSION_GRANTED &&
                canWriteStorage == PackageManager.PERMISSION_GRANTED )
        {
            if(Commons.getMainDatabaseFile().exists())
            {
                Log.i(TAG,"Database at " + Commons.getMainDatabaseFile().getAbsolutePath() );

                try {

                    MetrobusDatabase = new MetrobusDatabase(Commons.getMainDatabaseFile());

                    setupLocationService();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else
            {
                Log.w(TAG,"No database yet");
                Intent intent = new Intent(this, DownloadDatabaseActivity.class);
                startActivity(intent);
            }
        }
        else
        {
            requestStoragePermissions();
        }
    }


    private void setupLocationService(){
        // Check for permissions
        int canReadStorage = ContextCompat.checkSelfPermission(this,AccessFineLocation);
        int canWriteStorage = ContextCompat.checkSelfPermission(this,AccessCoarseLocation);

        if(canReadStorage == PackageManager.PERMISSION_GRANTED &&
                canWriteStorage == PackageManager.PERMISSION_GRANTED )
        {
            // Start service or whatever
            Log.i(TAG,"Ok location permissions");

            if (GoogleApiClient == null) {
                GoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            GoogleApiClient.connect();
        }
        else
        {
            requestLocationPermissions();
        }
    }

    private void requestStoragePermissions(){
        // Should we show an explanation?
        boolean requiresRational = ActivityCompat.shouldShowRequestPermissionRationale(this,ReadStorage) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,WriteStorage);

        if (!requiresRational) {
            AlertDialog dialog = _dialogBuilder.setTitle("Necesitamos tu permiso")
                    .setMessage(R.string.database_explanation)
                    .setPositiveButton(R.string.ok_open_settings, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startInstalledAppDetailsActivity();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.e(TAG,"DUDE WHAT");
                        }
                    }).create();

            dialog.show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            ReadStorage,
                            WriteStorage
                    },
                    STORAGE_REQUEST);
        }
    }

    private void requestLocationPermissions(){
        // Should we show an explanation?
        boolean requiresRational = ActivityCompat.shouldShowRequestPermissionRationale(this,AccessCoarseLocation) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,AccessFineLocation);

        if (!requiresRational) {


            AlertDialog dialog = _dialogBuilder.setTitle("Necesitamos tu permiso")
                    .setMessage(R.string.location_explanation)
                    .setPositiveButton(R.string.ok_open_settings, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startInstalledAppDetailsActivity();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.e(TAG,"DUDE WHAT");
                        }
                    }).create();

            dialog.show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            AccessCoarseLocation,
                            AccessFineLocation
                    },
                    LOCATION_REQUEST);
        }
    }

    public void startInstalledAppDetailsActivity() {

        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkDatabase();
                }
                else{

                    AlertDialog dialog = _dialogBuilder.setTitle("Necesitamos tu permiso")
                            .setMessage(R.string.database_explanation)
                            .setPositiveButton(R.string.ok_retry, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    requestStoragePermissions();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Log.e(TAG,"DUDE WHAT");
                                }
                            }).create();

                    dialog.show();
                }
            }
            case LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupLocationService();
                }
                else{

                    AlertDialog dialog = _dialogBuilder.setTitle("Necesitamos tu permiso")
                            .setMessage(R.string.location_explanation)
                            .setPositiveButton(R.string.ok_retry, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    requestLocationPermissions();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Log.e(TAG,"DUDE WHAT");
                                }
                            }).create();

                    dialog.show();
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(GoogleApiClient);

        try {
            Estacion cercana = MetrobusDatabase.getEstacionCercana(lastLocation.getLatitude(), lastLocation.getLongitude());
            _estacionCercanaText.setText(cercana.getNombre() + " " + cercana.getMetros());

            Intent intent = new Intent(this, LocationService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(TAG, "Service, I got the service!");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            mService = binder.getService();
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
}
