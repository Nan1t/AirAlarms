package ua.nanit.airalarm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ua.nanit.airalarm.util.LocaleUtil

open class LocalizedActivity(layout: Int) : AppCompatActivity(layout) {

    override fun onCreate(savedInstanceState: Bundle?) {
        LocaleUtil.updateLocale(this)
        super.onCreate(savedInstanceState)
    }

}