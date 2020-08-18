package com.csw.bluetooth.app

import android.app.Application
import androidx.room.Room
import com.csw.bluetooth.database.MyRoomDatabase
import com.csw.quickmvp.SDK
import javax.inject.Inject

class MyApplication : Application() {

    companion object {
        lateinit var instance: MyApplication
    }

    @Inject
    lateinit var appComponent: AppComponent
    @Inject
    lateinit var myRoomDatabase: MyRoomDatabase
    override fun onCreate() {
        super.onCreate()
        instance = this
        SDK.init(this)
        DaggerAppComponent.builder().setMyApplication(this).build().inject(this)
    }

}