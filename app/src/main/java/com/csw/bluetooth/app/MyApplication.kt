package com.csw.bluetooth.app

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.csw.bluetooth.database.MyRoomDatabase
import com.csw.bluetooth.model.ExternalFileDataCache
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
    lateinit var externalFileDataCache: ExternalFileDataCache
    override fun onCreate() {
        super.onCreate()
        instance = this
        SDK.init(this)
        DaggerAppComponent.builder().setMyApplication(this).build().inject(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        LogUtils.d(this, ExternalFileDataCache().getDeviceId())
    }

}