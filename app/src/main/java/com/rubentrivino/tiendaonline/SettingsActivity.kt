package com.rubentrivino.tiendaonline

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.preference.Preference

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings_container, SettingsFragment())
                .commit()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val darkModeSwitch = findPreference<SwitchPreferenceCompat>("pref_dark_mode")
            darkModeSwitch?.setOnPreferenceChangeListener { _, newValue ->
                val enabled = newValue as Boolean
                AppCompatDelegate.setDefaultNightMode(
                    if (enabled)
                        AppCompatDelegate.MODE_NIGHT_YES
                    else
                        AppCompatDelegate.MODE_NIGHT_NO
                )
                true
            }

            val privacyPref = findPreference<Preference>("pref_privacy")
            privacyPref?.setOnPreferenceClickListener {
                android.widget.Toast.makeText(
                    requireContext(),
                    "Próximamente: política de privacidad.",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                true
            }
        }
    }
}
