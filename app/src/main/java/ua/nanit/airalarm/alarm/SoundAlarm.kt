package ua.nanit.airalarm.alarm

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import ua.nanit.airalarm.*
import ua.nanit.airalarm.util.Resources
import kotlin.math.ceil

class SoundAlarm(private val ctx: Context) : Alarm {

    companion object {
        private const val AUDIO_STREAM = AudioManager.STREAM_ALARM
        private const val AUDIO_USAGE = AudioAttributes.USAGE_ALARM
    }
    
    private val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val prefs = Resources.getSettings(ctx)
    private val player = MediaPlayer()
    private var userVolume = 0
    var released = false

    init {
        if (Build.VERSION.SDK_INT >= 26) {
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AUDIO_USAGE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())
        } else {
            player.setAudioStreamType(AUDIO_STREAM)
        }

        player.setOnCompletionListener(this::onCompletion)
    }

    override fun alarm() {
        if (released) return
        if (getVolumeFromPrefs() < 1) return

        val uri = getSavedSound(PREFS_KEY_SOUND_ALARM, R.raw.alarm_siren)

        stop()
        setupVolume()
        player.reset()
        player.setDataSource(ctx, uri)
        player.prepare()
        player.start()
    }

    override fun allClear() {
        if (released) return
        if (getVolumeFromPrefs() < 1) return

        val uri = getSavedSound(PREFS_KEY_SOUND_ALL_CLEAR, R.raw.watch_digital_alarm)

        stop()
        setupVolume()
        player.reset()
        player.setDataSource(ctx, uri)
        player.prepare()
        player.start()
    }

    override fun stop() {
        player.stop()
    }

    fun release() {
        stop()
        player.release()
        released = true
    }

    private fun onCompletion(player: MediaPlayer) {
        restoreVolume()
    }

    private fun setupVolume() {
        val volumePercent = getVolumeFromPrefs().toFloat() / 100
        val maxVolume = audioManager.getStreamMaxVolume(AUDIO_STREAM)
        val volume = ceil(maxVolume.toFloat() * volumePercent).toInt()

        userVolume = audioManager.getStreamVolume(AUDIO_STREAM)

        player.setVolume(volumePercent, volumePercent)
        audioManager.setStreamVolume(AUDIO_STREAM, volume, AudioManager.FLAG_PLAY_SOUND)
    }

    private fun restoreVolume() {
        audioManager.setStreamVolume(AUDIO_STREAM, userVolume, AudioManager.FLAG_PLAY_SOUND)
        Log.d("AirAlarm", "Restored system volume")
    }

    private fun getVolumeFromPrefs(): Int {
        return prefs.getInt(PREFS_KEY_VOLUME, VOLUME_DEFAULT)
    }

    private fun getSavedSound(prefsKey: String, defSoundId: Int): Uri {
        val rawUri = prefs.getString(prefsKey, SOUND_DEFAULT)

        return if (rawUri == SOUND_DEFAULT) {
            Resources.getResUri(ctx, defSoundId)
        } else {
            Uri.parse(rawUri)
        }
    }
}