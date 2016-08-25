package silo.thatcsharpguy.com.questacion.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import silo.thatcsharpguy.com.questacion.entities.Estacion;
import silo.thatcsharpguy.com.questacion.entities.HardcodedData;

/**
 * Created by anton on 8/24/2016.
 */
public class LocationService
        extends Service
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Binder given to clients
    private IBinder _binder;

    private static final int NotificationId = 231;
    private Notification.Builder _notificationBuilder;
    NotificationManager _notificationManager;

    GoogleApiClient mGoogleApiClient;
    private LocationRequest _locationRequest = new LocationRequest();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return _binder;
    }


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public LocationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.i("LOCATION SERVICE", "onCreate");
        _notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        super.onCreate();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("LOCATION SERVICE", "onConnected");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, _locationRequest, new com.google.android.gms.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i("LOCATION SERVICE", "Locatrion changed");

                        Estacion nearestStation = getNearestStation(location);
                        notifica(nearestStation);
                    }
                });

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        Estacion nearestStation = getNearestStation(lastLocation);
        notifica(nearestStation);
    }

    private void createLocationRequest() {
        _locationRequest.setInterval(2000);
        _locationRequest.setFastestInterval(2000);
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LOCATION SERVICE", "onConnectionFailed");

    }

    public void notifica(Estacion nearestStation) {
        int color = 0x990000;
        Log.i("LOCATION SERVICE", "notifica " + nearestStation.getNombre());
        _notificationBuilder = new Notification.Builder(this)
        .setContentTitle(nearestStation.getNombre())
                .setOngoing(true)
                .setLocalOnly(true)
                .setColor(color)
                .setSmallIcon(com.google.android.gms.R.drawable.ic_plusone_tall_off_client);

        _notificationManager.notify(NotificationId, _notificationBuilder.build());
    }

    private Estacion getNearestStation(Location location) {
        return HardcodedData.Linea1.getEstaciones().get(0);
    }

}
