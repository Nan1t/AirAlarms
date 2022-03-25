package ua.nanit.airalarm.alarm

import android.app.Service
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class VibrationAlarm(ctx: Context) : Alarm {

    companion object {
        private val effectAlarm = longArrayOf(
            100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 1000,
            100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 1000
        )

        private val effectCancel = longArrayOf(
            300, 80, 100, 80, 300, 80, 100, 80, 300, 80, 100, 80, 300, 80, 100, 80,
            300, 80, 100, 80, 300, 80, 100, 80, 300, 80, 100, 80, 300, 80, 100, 80
        )
    }

    private val vibrator = ctx.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator

    override fun alarm() {
        vibrate(effectAlarm)
    }

    override fun cancelAlarm() {
        vibrate(effectCancel)
    }

    private fun vibrate(effect: LongArray) {
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createWaveform(effect, -1))
        } else {
            vibrator.vibrate(effect, -1)
        }
    }

}