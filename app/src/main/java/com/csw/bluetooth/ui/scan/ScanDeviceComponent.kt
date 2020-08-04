@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantOverride")
package com.csw.bluetooth.ui.scan

import com.csw.quickmvp.mvp.base.LifecycleCallback
import com.csw.quickmvp.mvp.base.PresenterSingleton
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Subcomponent

@PresenterSingleton
@Subcomponent(modules = [MyModule::class])
interface ScanDeviceComponent {
    fun inject(view: ScanDeviceActivity)

    @Subcomponent.Builder
    interface Builder {

        fun build(): ScanDeviceComponent

        @BindsInstance
        fun setView(view: ScanDeviceContract.View): Builder
    }
}

@Module
class MyModule {

    @PresenterSingleton
    @Provides
    fun getPresenter(presenter: ScanDevicePresenter): ScanDeviceContract.Presenter {
        return presenter
    }

    @Provides
    fun getLifecycleCallback(presenter: ScanDeviceContract.Presenter): LifecycleCallback {
        return presenter.getLifecycleCallback()
    }
}