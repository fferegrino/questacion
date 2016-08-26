package silo.thatcsharpguy.com.questacion;

import android.*;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import silo.thatcsharpguy.com.questacion.services.DownloadTask;

/**
 * Created by anton on 8/25/2016.
 */
public class DownloadDatabaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_database);

        Button startDatabaseDownloadButton = (Button) findViewById(R.id.startDatabaseDownloadButton);
        final ProgressBar databaseDownloadProgress = (ProgressBar)findViewById(R.id.databaseDownloadProgress);

        startDatabaseDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DownloadTask downloadTask = new DownloadTask(DownloadDatabaseActivity.this);

                downloadTask.setProgressBar(databaseDownloadProgress);

                downloadTask.execute();
            }
        });

    }
}
