package ua.nanit.airalarm.alarm

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import ua.nanit.airalarm.PREFS_KEY_VOLUME
import ua.nanit.airalarm.R
import ua.nanit.airalarm.util.Resources
import ua.nanit.airalarm.VOLUME_DEFAULT
import kotlin.math.ceil

class SoundAlarm(private val ctx: Context) : Alarm {

    companion object {
        private const val AUDIO_STREAM = AudioManager.STREAM_ALARM
        private const val AUDIO_USAGE = AudioAttributes.USAGE_ALARM
    }
    
    private val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val prefs = Resources.getSettings(ctx)
    private val alarmUri = Resources.getResUri(ctx, R.raw.alarm_siren)
    private val allClearUri = Resources.getResUri(ctx, R.raw.watch_digital_alarm)
    private val player = MediaPlayer()
    private var userVolume = 0

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
        player.setOnPreparedListener(this::onPrepared)
    }

    override fun alarm() {
        if (getVolumeFromPrefs() <= 0) return

        player.reset()
        player.setDataSource(ctx, alarmUri)
        player.prepareAsync()
    }

    override fun allClear() {
        if (getVolumeFromPrefs() <= 0) return

        player.reset()
        player.setDataSource(ctx, allClearUri)
        player.prepareAsync()
    }

    override fun stop() {
        stop(player)
    }

    private fun play(player: MediaPlayer) {
        if (!player.isPlaying) {
            player.start()
        }
    }

    private fun stop(player: MediaPlayer) {
        if (player.isPlaying) {
            player.stop()
            player.reset()
        }
    }

    private fun onPrepared(player: MediaPlayer) {
        setupVolume()
        play(player)
    }

    private fun onCompletion(player: MediaPlayer) {
        restoreVolume()
        stop(player)
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
}