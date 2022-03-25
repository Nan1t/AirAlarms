package ua.nanit.airalarm

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri

object Resources {

    fun getSettings(ctx: Context): SharedPreferences {
        return ctx.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    fun getResUri(ctx: Context, resId: Int): Uri {
        return Uri.parse("android.resource://${ctx.packageName}/$resId")
    }

}