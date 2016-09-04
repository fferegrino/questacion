package silo.thatcsharpguy.com.questacion;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;

import silo.thatcsharpguy.com.questacion.fragments.SettingsFragment;

/**
 * Created by anton on 9/3/2016.
 */
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();



    }
}