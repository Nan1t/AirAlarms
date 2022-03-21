package me.nanit.ukrainealarms

import android.app.Application

class AlarmApp : Application() {

    companion object {
        const val BASE_API_URL = "https://air-save.ops.ajax.systems/"
    }

    override fun onCreate() {
        super.onCreate()
    }

}