package silo.thatcsharpguy.com.questacion;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;

import silo.thatcsharpguy.com.questacion.services.DownloadTask;
import silo.thatcsharpguy.com.questacion.services.Commons;
import silo.thatcsharpguy.com.questacion.services.LocationService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String WriteStorage = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String ReadStorage = android.Manifest.permission.READ_EXTERNAL_STORAGE;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 2;
    private static final int STORAGE_REQUEST = 3;
    private static final int DATABASE_DOWNLOADED = 45;

    LocationService mService;
    boolean mBound = false;

    ProgressBar _databaseProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    private void requestStoragePermissions(){
        // Should we show an explanation?
        boolean requiresRational = ActivityCompat.shouldShowRequestPermissionRationale(this,ReadStorage) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,WriteStorage);

        if (!requiresRational) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);

            dialogBuilder.setTitle("Necesitamos tu permiso");
            dialogBuilder.setMessage("Questacion necesita tu permiso para descargar y guardar una pequeña base de datos con las líneas del metrobus");

            dialogBuilder.setPositiveButton(R.string.ok_open_settings, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startInstalledAppDetailsActivity();
                }
            });
            dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Log.e(TAG,"DUDE WHAT");
                }
            });

            AlertDialog dialog = dialogBuilder.create();

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
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkDatabase();
                }
                else{

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);

                    dialogBuilder.setTitle("Necesitamos tu permiso");
                    dialogBuilder.setMessage("Questacion necesita tu permiso para descargar y guardar una pequeña base de datos con las líneas del metrobus");

                    dialogBuilder.setPositiveButton(R.string.ok_retry, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            requestStoragePermissions();
                        }
                    });
                    dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.e(TAG,"DUDE WHAT");
                        }
                    });

                    AlertDialog dialog = dialogBuilder.create();

                    dialog.show();
                }
            }
        }
    }
}
