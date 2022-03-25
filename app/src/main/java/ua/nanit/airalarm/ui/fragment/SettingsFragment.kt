package ua.nanit.airalarm.ui.fragment

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import ua.nanit.airalarm.PREFS_KEY_VOLUME
import ua.nanit.airalarm.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val volumePrefs = findPreference<SeekBarPreference>(PREFS_KEY_VOLUME)!!

        updateVolumeIcon(volumePrefs, volumePrefs.value)
        volumePrefs.setOnPreferenceChangeListener(this::onVolumeChanged)
    }

    private fun onVolumeChanged(pref: Preference, newVal: Any?): Boolean {
        if (pref !is SeekBarPreference || newVal !is Int) return false
        updateVolumeIcon(pref, newVal)
        return true
    }

    private fun updateVolumeIcon(prefs: SeekBarPreference, newVal: Int) {
        var icon = R.drawable.ic_baseline_volume_up
        if (newVal < 1)
            icon = R.drawable.ic_baseline_volume_off
        prefs.setIcon(icon)
    }

}