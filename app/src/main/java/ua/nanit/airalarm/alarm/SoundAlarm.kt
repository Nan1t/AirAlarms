package ua.nanit.airalarm.alarm

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import ua.nanit.airalarm.R
import ua.nanit.airalarm.Resources

class SoundAlarm(private val ctx: Context) : Alarm {

    private val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val prefs: SharedPreferences = Resources.getSettings(ctx)
    private val alarmUri = Resources.getResUri(ctx, R.raw.officers_call)
    private val alarmCancelUri = Resources.getResUri(ctx, R.raw.officers_call)
    private val player = MediaPlayer()
    private var userVolume = 0

    init {
        if (Build.VERSION.SDK_INT >= 26) {
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())
        } else {
            player.setAudioStreamType(AudioManager.STREAM_ALARM)
        }

        player.setOnCompletionListener(this::onCompletion)
        player.setOnPreparedListener(this::onPrepared)
    }

    override fun alarm() {
        player.reset()
        player.setDataSource(ctx, alarmUri)
        player.prepareAsync()
    }

    override fun cancelAlarm() {
        player.reset()
        player.setDataSource(ctx, alarmCancelUri)
        player.prepareAsync()
    }

    private fun play(player: MediaPlayer) {
        player.start()
    }

    private fun stop(player: MediaPlayer) {
        player.stop()
        player.reset()
        //player.release()
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
        val volumePercent = prefs.getFloat("volume", 0.1F)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
        val volume = (maxVolume * volumePercent).toInt()

        userVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)

        Log.i("AirAlarm", "Set volume to $volumePercent% ($volume). User value: $userVolume")

        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            volume,
            AudioManager.FLAG_PLAY_SOUND
        )
    }

    private fun restoreVolume() {
        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            userVolume,
            AudioManager.FLAG_PLAY_SOUND
        )
    }
}