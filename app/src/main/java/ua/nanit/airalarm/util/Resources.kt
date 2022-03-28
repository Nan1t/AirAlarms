package ua.nanit.airalarm.util

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.preference.PreferenceManager

object Resources {

    fun getSettings(ctx: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun getResUri(ctx: Context, resId: Int): Uri {
        return Uri.parse(getResUriStr(ctx, resId))
    }

    fun getVectorBitmap(ctx: Context, resId: Int): Bitmap {
        return AppCompatResources.getDrawable(ctx, resId)?.toBitmap()!!
    }

    private fun getResUriStr(ctx: Context, resId: Int): String {
        return "android.resource://${ctx.packageName}/$resId"
    }

}