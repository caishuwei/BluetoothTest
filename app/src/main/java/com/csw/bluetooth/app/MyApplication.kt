package com.csw.bluetooth.app

import android.app.Application
import androidx.room.Room
import com.csw.quickmvp.SDK
import javax.inject.Inject

class MyApplication : Application() {

    companion object {
        lateinit var instance: MyApplication
    }

    @Inject
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this
        SDK.init(this)
        DaggerAppComponent.builder().setMyApplication(this).build().inject(this)
    }

}