package com.csw.quickmvp.mvp.example.app

import android.app.Application
import com.csw.quickmvp.SDK
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
        SDK.init(this)
        //编译后生成DaggerAppComponent，通过它进行AppComponent的初始化
        appComponent = DaggerAppComponent.builder().setMyApplication(this).build()
        appComponent.inject(this)
        appModel.log(this)
    }

}