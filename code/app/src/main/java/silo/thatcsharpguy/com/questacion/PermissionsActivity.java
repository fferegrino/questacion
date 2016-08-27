package silo.thatcsharpguy.com.questacion;

import android.*;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import jsqlite.Exception;
import silo.thatcsharpguy.com.questacion.dataaccess.MetrobusDatabase;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PermissionsActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;


    private static final String WriteStorage = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String ReadStorage = android.Manifest.permission.READ_EXTERNAL_STORAGE;

    private static final String AccessCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String AccessFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION;

    private static final int LOCATION_REQUEST = 2;
    private static final int STORAGE_REQUEST = 3;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnClickListener mDelayHideTouchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            tryToMoveOn();
        }
    };

    AlertDialog.Builder _dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_permissions);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        _dialogBuilder = new AlertDialog.Builder(this);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.continueButton).setOnClickListener(mDelayHideTouchListener);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    show();
                }
                else{

                    AlertDialog dialog = _dialogBuilder.setTitle("Necesitamos tu permiso")
                            .setMessage(R.string.database_explanation)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create();
                    dialog.show();
                }
            }
            case LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    show();
                }
                else{

                    AlertDialog dialog = _dialogBuilder.setTitle("Necesitamos tu permiso")
                            .setMessage(R.string.location_explanation)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create();
                    dialog.show();
                }
            }
        }
    }

    private boolean checkStoragePermissions(){
        // Check for permissions
        int canReadStorage = ContextCompat.checkSelfPermission(this,ReadStorage);
        int canWriteStorage = ContextCompat.checkSelfPermission(this,WriteStorage);

        return (canReadStorage == PackageManager.PERMISSION_GRANTED &&
                canWriteStorage == PackageManager.PERMISSION_GRANTED );
    }

    private boolean checkLocationPermissions(){
        // Check for permissions
        int canAccessCoarseLoc = ContextCompat.checkSelfPermission(this,AccessCoarseLocation);
        int canAccessFineLoc = ContextCompat.checkSelfPermission(this,AccessFineLocation);

        return (canAccessCoarseLoc == PackageManager.PERMISSION_GRANTED &&
                canAccessFineLoc == PackageManager.PERMISSION_GRANTED );
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
        Log.i("PermissionsActivity","requestLocationPermissions");
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
    protected void onStart() {
        tryToMoveOn();
        super.onStart();
    }



    private void tryToMoveOn() {
        boolean hasStorageAccess = checkStoragePermissions();
        boolean hasLocationAccess = checkLocationPermissions();

        if(!hasStorageAccess){
            requestStoragePermissions();
        }else if(!hasLocationAccess){
            requestLocationPermissions();
        }else{
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
