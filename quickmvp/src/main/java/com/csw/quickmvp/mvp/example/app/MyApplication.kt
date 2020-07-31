package com.csw.quickmvp.mvp.example.app

import android.app.Application
import javax.inject.Inject

class MyApplication : Application() {

    companion object {
        lateinit var instance: MyApplication
    }

    @Inject
    lateinit var appModel: AppModel
    lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        instance = this
        appComponent = DaggerAppComponent.builder().setMyApplication(this).build()
        appComponent.inject(this)
    }

}