package ua.nanit.airalarm.ui

import android.os.Bundle
import ua.nanit.airalarm.R
import ua.nanit.airalarm.ui.fragment.SettingsFragment

class SettingsActivity : LocalizedActivity(R.layout.activity_settings) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.settings_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()
    }

}