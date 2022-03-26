package ua.nanit.airalarm.ui.fragment

import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import ua.nanit.airalarm.*
import ua.nanit.airalarm.alarm.SoundAlarm

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var testableAlarm: SoundAlarm

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        testableAlarm = SoundAlarm(requireContext())

        val langPrefs = findPreference<DropDownPreference>(PREFS_KEY_LANG)!!
        val volumePrefs = findPreference<SeekBarPreference>(PREFS_KEY_VOLUME)!!
        val alarmSoundPrefs = findPreference<DropDownPreference>(PREFS_KEY_SOUND_ALARM)!!
        val allClearSoundPrefs = findPreference<DropDownPreference>(PREFS_KEY_SOUND_ALL_CLEAR)!!

        val ringtones = getRingtoneEntries()

        alarmSoundPrefs.entries = ringtones.first
        alarmSoundPrefs.entryValues = ringtones.second

        allClearSoundPrefs.entries = ringtones.first
        allClearSoundPrefs.entryValues = ringtones.second

        updateDropdownSummary(alarmSoundPrefs)
        updateDropdownSummary(allClearSoundPrefs)
        updateVolumeIcon(volumePrefs, volumePrefs.value)

        langPrefs.setOnPreferenceChangeListener(this::onChange)
        volumePrefs.setOnPreferenceChangeListener(this::onChange)
        alarmSoundPrefs.setOnPreferenceChangeListener(this::onChange)
        allClearSoundPrefs.setOnPreferenceChangeListener(this::onChange)

        preferenceScreen.sharedPreferences
            ?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onResume() {
        super.onResume()

        if (testableAlarm.released) {
            testableAlarm = SoundAlarm(requireContext())
        }
    }

    override fun onDetach() {
        super.onDetach()
        testableAlarm.release()
        Log.i("AirAlarm", "RELEASE PLAYER")
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
        val element = findPreference<Preference>(key)

        if (element is DropDownPreference) {
            updateDropdownSummary(element)
        }

        when(key) {
            PREFS_KEY_VOLUME,
            PREFS_KEY_SOUND_ALARM -> {
                testableAlarm.alarm()
            }
            PREFS_KEY_SOUND_ALL_CLEAR -> {
                testableAlarm.allClear()
            }
        }
    }

    private fun onChange(pref: Preference, newVal: Any?): Boolean {
        when (pref.key) {
            PREFS_KEY_VOLUME -> {
                if (newVal !is Int) return false
                updateVolumeIcon(pref as SeekBarPreference, newVal)
            }
            PREFS_KEY_LANG -> {
                Toast.makeText(requireContext(), R.string.settings_language_reload, Toast.LENGTH_LONG)
                    .show()
            }
        }

        return true
    }

    private fun updateDropdownSummary(pref: DropDownPreference) {
        pref.summary = pref.entry
    }

    private fun updateVolumeIcon(prefs: SeekBarPreference, newVal: Int) {
        var icon = R.drawable.ic_baseline_volume_up
        if (newVal < 1)
            icon = R.drawable.ic_baseline_volume_off
        prefs.setIcon(icon)
    }

    /**
     * <Array<Entry (Name)>, Array<Value (Uri)>>
     */
    private fun getRingtoneEntries(): Pair<Array<String?>, Array<String?>> {
        val ringtones = getAvailableRingtones()
        val entries = arrayOfNulls<String>(ringtones.size + 1)
        val values = arrayOfNulls<String>(ringtones.size + 1)

        entries[0] = getString(R.string.settings_ringtone_default)
        values[0] = SOUND_DEFAULT

        var index = 1
        for (ringtone in ringtones) {
            entries[index] = ringtone.key
            values[index] = ringtone.value
            index++
        }

        return Pair(entries, values)
    }

    /**
     * <Name, Uri>
     */
    private fun getAvailableRingtones(): Map<String, String> {
        val ringtones = HashMap<String, String>()
        val ringtoneManager = RingtoneManager(requireContext())

        ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE)

        val cursor = ringtoneManager.cursor

        while (cursor.moveToNext()) {
            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            val uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX)
            val id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX)

            ringtones[title] = "$uri/$id"
        }

        cursor.close()

        return ringtones
    }

}