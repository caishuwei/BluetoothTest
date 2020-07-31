@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantOverride")
package com.csw.quickmvp.mvp.example.module

import com.csw.quickmvp.mvp.base.LifecycleCallback
import com.csw.quickmvp.mvp.base.PresenterSingleton
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Subcomponent

@PresenterSingleton
@Subcomponent(modules = [MyModule::class])
interface ModuleComponent {
    fun inject(view: ModuleView)

    @Subcomponent.Builder
    interface Builder {

        fun build(): ModuleComponent

        @BindsInstance
        fun setView(view: ModuleContract.View): Builder
    }
}

@Module
class MyModule {

    @PresenterSingleton
    @Provides
    fun getPresenter(presenter: ModulePresenter): ModuleContract.Presenter {
        return presenter
    }

    @Provides
    fun getLifecycleCallback(presenter: ModuleContract.Presenter): LifecycleCallback {
        return presenter.getLifecycleCallback()
    }
}