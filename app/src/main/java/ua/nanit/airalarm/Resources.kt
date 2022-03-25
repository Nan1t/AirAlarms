package ua.nanit.airalarm

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.preference.PreferenceManager

object Resources {

    fun getSettings(ctx: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun getResUri(ctx: Context, resId: Int): Uri {
        return Uri.parse("android.resource://${ctx.packageName}/$resId")
    }

}