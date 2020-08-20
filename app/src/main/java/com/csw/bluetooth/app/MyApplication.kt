package com.csw.bluetooth.app

import android.app.Application
import com.csw.bluetooth.database.MyRoomDatabase
import com.csw.bluetooth.model.DataModel
import com.csw.bluetooth.model.cache.ExternalFileDataCache
import com.csw.quickmvp.SDK
import com.csw.quickmvp.utils.LogUtils
import javax.inject.Inject
import javax.inject.Named

class MyApplication : Application() {

    companion object {
        lateinit var instance: MyApplication
    }

    @Inject
    lateinit var appComponent: AppComponent

    @Inject
    lateinit var myRoomDatabase: MyRoomDatabase

    @Named("App")
    @Inject
    lateinit var dataModel: DataModel
    override fun onCreate() {
        super.onCreate()
        instance = this
        SDK.init(this)
        DaggerAppComponent.builder().setMyApplication(this).build().inject(this)
        LogUtils.d(this, ExternalFileDataCache().getDeviceId())
    }

}