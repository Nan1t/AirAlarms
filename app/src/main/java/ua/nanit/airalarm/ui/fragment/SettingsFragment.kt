package ua.nanit.airalarm.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import ua.nanit.airalarm.*

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val langPrefs = findPreference<DropDownPreference>(PREFS_KEY_LANG)!!
        val volumePrefs = findPreference<SeekBarPreference>(PREFS_KEY_VOLUME)!!
        val alarmSoundPrefs = findPreference<DropDownPreference>(PREFS_KEY_SOUND_ALARM)!!
        val allClearSoundPrefs = findPreference<DropDownPreference>(PREFS_KEY_SOUND_ALL_CLEAR)!!

        updateVolumeIcon(volumePrefs, volumePrefs.value)

        langPrefs.setOnPreferenceChangeListener(this::onChange)
        volumePrefs.setOnPreferenceChangeListener(this::onChange)
    }

    fun onPreferenceChanged(pref: SharedPreferences, key: String) {
        val saved = findPreference<Preference>(key)

        if (saved is DropDownPreference) {
            saved.summary = saved.entry
        }
    }

    private fun onChange(pref: Preference, newVal: Any?): Boolean {
        when (pref.key) {
            PREFS_KEY_VOLUME -> {
                if (newVal !is Int) return false
                updateVolumeIcon(pref as SeekBarPreference, newVal)
            }
        }

        return true
    }

    private fun updateVolumeIcon(prefs: SeekBarPreference, newVal: Int) {
        var icon = R.drawable.ic_baseline_volume_up
        if (newVal < 1)
            icon = R.drawable.ic_baseline_volume_off
        prefs.setIcon(icon)
    }

}