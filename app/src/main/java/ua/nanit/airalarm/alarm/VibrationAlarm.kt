package ua.nanit.airalarm.alarm

import android.app.Service
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import ua.nanit.airalarm.PREFS_KEY_VIBRATION
import ua.nanit.airalarm.util.Resources

class VibrationAlarm(private val ctx: Context) : Alarm {

    companion object {
        private val effectAlarm = longArrayOf(
            100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 1000,
            100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 1000
        )

        private val effectAllClear = longArrayOf(
            300, 80, 100, 80, 300, 80, 100, 80, 300, 80, 100, 80, 300, 80, 100, 80,
            300, 80, 100, 80, 300, 80, 100, 80, 300, 80, 100, 80, 300, 80, 100, 80
        )

        private val effectSingle = longArrayOf(0, 200)
    }

    private val vibrator = ctx.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
    private val prefs = Resources.getSettings(ctx)

    override fun alarm() {
        val enabled = prefs.getBoolean(PREFS_KEY_VIBRATION, true)

        if (enabled) {
            vibrate(effectAlarm)
        }
    }

    override fun allClear() {
        val enabled = prefs.getBoolean(PREFS_KEY_VIBRATION, true)

        if (enabled) {
            vibrate(effectAllClear)
        }
    }

    override fun stop() {
        vibrator.cancel()
    }

    fun vibrateSingle() {
        vibrate(effectSingle)
    }

    private fun vibrate(effect: LongArray) {
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createWaveform(effect, -1))
        } else {
            vibrator.vibrate(effect, -1)
        }
    }

}