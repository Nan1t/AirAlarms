package ua.nanit.airalarm.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import ua.nanit.airalarm.PREFS_KEY_LANG
import java.util.*

object LocaleUtil {

    fun updateLocale(ctx: Context) {
        val prefs = Resources.getSettings(ctx)
        val savedLocale = prefs.getString(PREFS_KEY_LANG, null)

        if (savedLocale != null) {
            val conf = Configuration()
            val locale = Locale(savedLocale)

            Locale.setDefault(locale)
            conf.setLocale(locale)

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
                ctx.createConfigurationContext(conf)
            } else {
                ctx.resources.updateConfiguration(conf, ctx.resources.displayMetrics)
            }
        }
    }

}