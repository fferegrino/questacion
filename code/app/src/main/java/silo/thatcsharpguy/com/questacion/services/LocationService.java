package silo.thatcsharpguy.com.questacion.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import jsqlite.Exception;
import silo.thatcsharpguy.com.questacion.Commons;
import silo.thatcsharpguy.com.questacion.MainActivity;
import silo.thatcsharpguy.com.questacion.R;
import silo.thatcsharpguy.com.questacion.entities.Estacion;

/**
 * Created by anton on 8/24/2016.
 */
public class LocationService
        extends Service implements  com.google.android.gms.location.LocationListener{

    private static final int NotificationId = 231;
    private static final int LocationUpdateInterval = 2500;
    private static final long[] VibrationPattern = {500,1000};
    private static final Uri Sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    private List<NuevaEstacionListener> listeners = new ArrayList<NuevaEstacionListener>();
    public void addListener(NuevaEstacionListener toAdd) {
        listeners.add(toAdd);
    }

    // Binder given to clients
    private IBinder _binder = new LocalBinder();
    private LocationRequest _locationRequest = new LocationRequest();
    SharedPreferences  _preferences;
    private NotificationCompat.Builder _notificationBuilder;
    NotificationManager _notificationManager;
    Resources res;

    private boolean _isPaused;

    @Nullable
    @Override
    @SuppressWarnings({"MissingPermission"})
    public IBinder onBind(Intent intent) {
        createLocationRequest();

        LocationServices.FusedLocationApi.requestLocationUpdates(
                MainActivity.GoogleApiClient, _locationRequest, this);

        return _binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LocationServices.FusedLocationApi.removeLocationUpdates(MainActivity.GoogleApiClient, this);
        _notificationManager.cancel(NotificationId);
        return super.onUnbind(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(_isPaused) return;

        Estacion nearestStation = getNearestStation(location);
        notifica(nearestStation);

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
        _notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        _preferences= PreferenceManager.getDefaultSharedPreferences(this);

        res = getResources();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void createLocationRequest() {
        _locationRequest.setInterval(LocationUpdateInterval);
        _locationRequest.setFastestInterval(LocationUpdateInterval);
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private Estacion _lastVisitedStation;
    private double _lastDistance = Double.MAX_VALUE;

    private void notifica(Estacion nearestStation) {
        if(nearestStation != null){
            String texto = null;
            boolean newStation = !nearestStation.equals(_lastVisitedStation);
            if(newStation) {
                _lastDistance = nearestStation.getMetros();
                texto = res.getString(R.string.ariving_to);
            }
            else {
                if(_lastDistance < nearestStation.getMetros())
                {
                    texto = res.getString(R.string.leaving_from);
                }else
                {
                    texto = res.getString(R.string.ariving_to);
                }
                _lastDistance = nearestStation.getMetros();
            }

            if(_lastDistance != Double.MAX_VALUE)
                _lastDistance = nearestStation.getMetros();

            _notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(String.format(texto, nearestStation.getNombre()))
                    .setContentText(String.format(res.getString(R.string.route), nearestStation.getLinea()))
                    .setOngoing(true)
                    .setLocalOnly(true)
                    .setColor(nearestStation.getColor())
                    .setSmallIcon(Commons.LineaIconMapper[nearestStation.getLinea()]);

            if(newStation && _preferences.getBoolean(res.getString(R.string.vibrate_key),false))
            {
                _notificationBuilder.setVibrate(VibrationPattern);
            }

            if(newStation && _preferences.getBoolean(res.getString(R.string.sound_key),false))
            {
                _notificationBuilder.setSound(Sound);
            }



            Intent myIntent = new Intent(this, MainActivity.class);
            PendingIntent intent2 = PendingIntent.getActivity(this, 1,
                    myIntent, PendingIntent.FLAG_UPDATE_CURRENT
                            | PendingIntent.FLAG_ONE_SHOT);
            _notificationBuilder.setContentIntent(intent2);


            _lastVisitedStation = nearestStation;

            _notificationManager.notify(NotificationId, _notificationBuilder.build());

            for (NuevaEstacionListener hl : listeners)
                hl.stationUpdate(nearestStation);
        }
        else if(nearestStation == null)
        {
            _notificationManager.cancel(NotificationId);

            for (NuevaEstacionListener hl : listeners)
                hl.stationUpdate(null);
        }
    }


    private Estacion getNearestStation(Location location) {
        double threshold = Double.parseDouble(_preferences.getString(res.getString(R.string.threshold_key),"500"));
        try {
            return MainActivity.MetrobusDatabase.getEstacionCercana(location.getLatitude(),location.getLongitude(), threshold);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPaused(boolean isPaused)
    {
        _isPaused = isPaused;
    }

}
