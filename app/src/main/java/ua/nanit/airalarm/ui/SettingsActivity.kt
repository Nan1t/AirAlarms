package ua.nanit.airalarm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ua.nanit.airalarm.R
import ua.nanit.airalarm.ui.fragment.SettingsFragment

class SettingsActivity : AppCompatActivity(R.layout.activity_settings) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()
    }

}