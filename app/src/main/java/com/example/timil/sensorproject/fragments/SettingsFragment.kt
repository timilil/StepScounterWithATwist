package com.example.timil.sensorproject.fragments

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.example.timil.sensorproject.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}