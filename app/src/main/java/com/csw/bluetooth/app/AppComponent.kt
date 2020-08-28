package com.csw.bluetooth.app

import android.app.Application
import android.content.Context
import com.csw.bluetooth.database.MyRoomDatabase
import com.csw.bluetooth.model.DataModel
import com.csw.bluetooth.service.bluetooth.classic.ClassicBluetoothService
import com.csw.bluetooth.service.bluetooth.le.LowEnergyBluetoothService
import com.csw.bluetooth.ui.chat.ChatComponent
import com.csw.bluetooth.ui.scan.ScanDeviceComponent
import com.csw.quickmvp.mvp.base.AppSingleton
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Named

@AppSingleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(myApplication: MyApplication)
    fun inject(myApplication: ClassicBluetoothService)
    fun inject(lowEnergyBluetoothService: LowEnergyBluetoothService)

    fun getScanDeviceComponentBuilder(): ScanDeviceComponent.Builder
    fun getChatComponentBuilder(): ChatComponent.Builder

    /**
     * 实例工厂构建，可以先注入一些已存的实例
     */
    @Component.Builder
    interface Builder {

        /**
         * 构建MyApplicationComponent
         */
        fun build(): AppComponent

        /**
         * 注入已经存在的实例到AppComponent中，以便程序中使用
         */
        @BindsInstance
        fun setMyApplication(myApplication: MyApplication): Builder
    }
}

/**
 * 实例生成器，module的作用是，当要从Component中获取不存在的实例时，Component可以通过module来生成实例
 */
@Module
class AppModule {

    /**
     * Provides注解这个方法，表明这个方法可以提供返回值类型的实例，调用方法需要的参数是从Component中
     * 获取的。
     *
     * 可以使用一些注解，达到单例。当需要区分两个返回类型一样的Provider时，可以使用注解进行标记，这样
     * 当需要往某个对象注入实例时，该对象使用相同的注解即可获取到想要的实例。
     */
    @Provides
    fun getApplication(myApplication: MyApplication): Application {
        return myApplication
    }

    @Provides
    fun getContext(application: Application): Context {
        return application
    }

    @AppSingleton
    @Provides
    fun getMyRoomDatabase(application: Application): MyRoomDatabase {
        return MyRoomDatabase.getInstance(application)
    }

    /**
     * @Named 注解用于区分相同返回类型的Provides，在注入的地方也使用这个注解，即可注入这个Provides提供的实例
     *
     * 比如返回值是一个字符串的话，同个module有存在多个provides都提供了返回字符串，则可以通过这个注解来区分到
     * 底需要哪个字符串
     */
    @Named("App")
    @AppSingleton
    @Provides
    fun getDataModel(dataModel: DataModel): DataModel {
        return dataModel
    }
}