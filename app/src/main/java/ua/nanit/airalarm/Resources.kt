package ua.nanit.airalarm

import android.content.Context
import android.net.Uri

object Resources {

    fun getResUri(ctx: Context, resId: Int): Uri {
        return Uri.parse("android.resource://${ctx.packageName}/$resId")
    }

}