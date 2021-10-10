package com.seven

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * @author Richi on 10/10/21.
 */
@HiltAndroidApp class App : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}