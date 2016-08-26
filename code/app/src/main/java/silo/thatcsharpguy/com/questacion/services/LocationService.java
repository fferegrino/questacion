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

import jsqlite.Exception;
import silo.thatcsharpguy.com.questacion.MainActivity;
import silo.thatcsharpguy.com.questacion.entities.Estacion;
import silo.thatcsharpguy.com.questacion.entities.HardcodedData;

/**
 * Created by anton on 8/24/2016.
 */
public class LocationService
        extends Service {

    // Binder given to clients
    private IBinder _binder;

    private static final int NotificationId = 231;
    private Notification.Builder _notificationBuilder;
    NotificationManager _notificationManager;

    private LocationRequest _locationRequest = new LocationRequest();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.i("LOCATION SERVICE", "onBind");
        createLocationRequest();

        LocationServices.FusedLocationApi.requestLocationUpdates(
                MainActivity.GoogleApiClient, _locationRequest, new com.google.android.gms.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i("LOCATION SERVICE", "Locatrion changed");
                        Estacion nearestStation = getNearestStation(location);
                        notifica(nearestStation);
                    }
                });

        return _binder;
    }


    @Override
    public boolean onUnbind(Intent intent) {

        Log.i("LOCATION SERVICE", "onUnbind");
        return super.onUnbind(intent);
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
        super.onCreate();
    }

    private void createLocationRequest() {
        _locationRequest.setInterval(2000);
        _locationRequest.setFastestInterval(2000);
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }



    public void notifica(Estacion nearestStation) {
        _notificationBuilder = new Notification.Builder(this)
        .setContentTitle(nearestStation.getNombre())
                .setOngoing(true)
                .setLocalOnly(true)
                .setColor(nearestStation.getColor())
                .setSmallIcon(com.google.android.gms.R.drawable.ic_plusone_tall_off_client);

        _notificationManager.notify(NotificationId, _notificationBuilder.build());
    }

    private Estacion getNearestStation(Location location) {
        try {
            return MainActivity.MetrobusDatabase.getEstacionCercana(location.getLatitude(),location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
