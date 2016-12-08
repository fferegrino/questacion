package com.thatcsharpguy.questacion.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.thatcsharpguy.questacion.DownloadDatabaseActivity;
import com.thatcsharpguy.questacion.R;

/**
 * Created by anton on 9/3/2016.
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.app_preferences);


        Preference myPref = (Preference) findPreference("database");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {


                Intent intent = new Intent(getActivity(), DownloadDatabaseActivity.class);
                startActivity(intent);

                return true;
            }
        });
    }
}
