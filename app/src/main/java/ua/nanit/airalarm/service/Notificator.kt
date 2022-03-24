package ua.nanit.airalarm.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import ua.nanit.airalarm.R
import ua.nanit.airalarm.Resources
import ua.nanit.airalarm.ui.MainActivity

class Notificator(private val service: Service) {

    companion object {
        const val NOTIFICATION_ID_MAIN = 1
        const val NOTIFICATION_ID_PUSH = 2
        const val NOTIFICATION_CHANNEL = "importance"

        private val vibrationAlarm = longArrayOf(
            100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 1000,
            100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 1000
        )

        private val vibrationCancel = longArrayOf(
            300, 80, 100, 80, 300, 80, 100, 80, 300, 80, 100, 80, 300, 80, 100, 80,
            300, 80, 100, 80, 300, 80, 100, 80, 300, 80, 100, 80, 300, 80, 100, 80
        )
    }

    private val notifyManager = service.getSystemService(Service.NOTIFICATION_SERVICE)
            as NotificationManager

    private val vibrator = service.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator

    private val imgAlarmOn = BitmapFactory.decodeResource(service.resources, R.drawable.alarm_on)
    private val imgAlarmOff = BitmapFactory.decodeResource(service.resources, R.drawable.alarm_off)

    fun startForegroundNotification() {
        service.startForeground(NOTIFICATION_ID_MAIN, getNotifyMain())
    }

    fun displayActualNotification(alarmed: Boolean) {
        val notification = if (alarmed) getNotifyAlarm() else getNotifyOk()
        notifyManager.notify(NOTIFICATION_ID_PUSH, notification)

        if (alarmed) {
            // TODO rewrite this shit

            val audioManager = service.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val userVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
            val player = MediaPlayer()

            if (Build.VERSION.SDK_INT >= 26) {
                player.setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())
            } else {
                player.setAudioStreamType(AudioManager.STREAM_ALARM)
            }

            player.setOnCompletionListener {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, userVolume, AudioManager.FLAG_PLAY_SOUND)
                it.stop()
                it.reset()
                it.release()
            }
            player.setOnPreparedListener(MediaPlayer::start)
            player.reset()
            player.setDataSource(service, Resources.getResUri(service, R.raw.officers_call))
            player.prepareAsync()

            audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
                AudioManager.FLAG_PLAY_SOUND)
        }

        //vibrate(alarmed)
    }

    private fun vibrate(alarmed: Boolean) {
        val effect = if (alarmed) vibrationAlarm else vibrationCancel

        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createWaveform(effect, -1))
        } else {
            vibrator.vibrate(effect, -1)
        }
    }

    private fun getNotifyMain(): Notification {
        val builder = baseNotifyBuilder(getPendingIntent())

        builder.setSmallIcon(R.drawable.ic_megaphone)
        builder.setContentTitle(service.getString(R.string.service_notification_title))
        builder.setContentText(service.getString(R.string.service_notification_subtitle))

        return builder.build()
    }

    private fun getNotifyAlarm(): Notification {
        val builder = baseNotifyBuilder()

        builder.color = service.resources.getColor(R.color.danger)
        builder.setCategory(Notification.CATEGORY_ALARM)
        builder.setChannelId("Alarm")
        builder.setSmallIcon(R.drawable.ic_baseline_warning)
        builder.setLargeIcon(imgAlarmOn)
        builder.setContentTitle(service.getString(R.string.status_alarmed_title))
        builder.setContentText(service.getString(R.string.status_alarmed_subtitle))
        builder.setWhen(System.currentTimeMillis())
        builder.setLights(Color.RED, 400, 200)
        builder.setAutoCancel(true)

        return builder.build()
    }

    private fun getNotifyOk(): Notification {
        val builder = baseNotifyBuilder()

        builder.color = service.resources.getColor(R.color.success)
        builder.setCategory(Notification.CATEGORY_ALARM)
        builder.setChannelId(NotificationChannel.EDIT_IMPORTANCE)
        builder.setSmallIcon(R.drawable.ic_baseline_check)
        builder.setLargeIcon(imgAlarmOff)
        builder.setContentTitle(service.getString(R.string.alarm_cancelled))
        builder.setContentText(service.getString(R.string.status_ok_subtitle))
        builder.setWhen(System.currentTimeMillis())
        builder.setLights(Color.GREEN, 400, 200)
        builder.setAutoCancel(true)

        return builder.build()
    }

    private fun getPendingIntent(): PendingIntent {
        return PendingIntent.getActivity(service,
            0, Intent(service, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE)
    }

    private fun baseNotifyBuilder(intent: PendingIntent? = null): NotificationCompat.Builder {
        return NotificationCompat.Builder(service, NOTIFICATION_CHANNEL)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(intent)
    }

}