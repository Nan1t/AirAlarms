/*
 * Copyright (C) 2022 Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ua.nanit.airalarm.ui.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.*
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import ua.nanit.airalarm.*
import ua.nanit.airalarm.R
import ua.nanit.airalarm.alarm.SoundAlarm
import ua.nanit.airalarm.alarm.VibrationAlarm

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var testSound: SoundAlarm
    private lateinit var testVibration: VibrationAlarm

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        testSound = SoundAlarm(requireContext())
        testVibration = VibrationAlarm(requireContext())

        val langPrefs = findPreference<DropDownPreference>(PREFS_KEY_LANG)!!
        val vibrationPrefs = findPreference<SwitchPreferenceCompat>(PREFS_KEY_VIBRATION)!!
        val volumePrefs = findPreference<SeekBarPreference>(PREFS_KEY_VOLUME)!!
        val alarmSoundPrefs = findPreference<DropDownPreference>(PREFS_KEY_SOUND_ALARM)!!
        val allClearSoundPrefs = findPreference<DropDownPreference>(PREFS_KEY_SOUND_ALL_CLEAR)!!
        val licensesPrefs = findPreference<Preference>(PREFS_KEY_LICENSES)!!

        val ringtones = getRingtoneEntries()

        alarmSoundPrefs.entries = ringtones.first
        alarmSoundPrefs.entryValues = ringtones.second

        allClearSoundPrefs.entries = ringtones.first
        allClearSoundPrefs.entryValues = ringtones.second

        updateDropdownSummary(alarmSoundPrefs)
        updateDropdownSummary(allClearSoundPrefs)
        updateVolumeIcon(volumePrefs, volumePrefs.value)

        langPrefs.setOnPreferenceChangeListener(this::onChange)
        vibrationPrefs.setOnPreferenceChangeListener(this::onChange)
        volumePrefs.setOnPreferenceChangeListener(this::onChange)
        alarmSoundPrefs.setOnPreferenceChangeListener(this::onChange)
        allClearSoundPrefs.setOnPreferenceChangeListener(this::onChange)

        preferenceScreen.sharedPreferences
            ?.registerOnSharedPreferenceChangeListener(this)

        licensesPrefs.setOnPreferenceClickListener {
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
            true
        }
    }

    override fun onResume() {
        super.onResume()

        if (testSound.released) {
            testSound = SoundAlarm(requireContext())
        }
    }


    override fun onPause() {
        super.onPause()
        testSound.stop()
        Log.i("AirAlarm", "STOP PLAYER")
    }

    override fun onDetach() {
        super.onDetach()
        testSound.release()
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
                testSound.alarm()
            }
            PREFS_KEY_SOUND_ALL_CLEAR -> {
                testSound.allClear()
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
            PREFS_KEY_VIBRATION -> {
                if (newVal as Boolean) {
                    testVibration.vibrateSingle()
                }
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