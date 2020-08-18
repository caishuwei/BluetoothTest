@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantOverride")
package com.csw.bluetooth.ui.chat

import com.csw.quickmvp.mvp.base.LifecycleCallback
import com.csw.quickmvp.mvp.base.PresenterSingleton
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Subcomponent

@PresenterSingleton
@Subcomponent(modules = [MyModule::class])
interface ChatComponent {
    fun inject(view: ChatActivity)

    @Subcomponent.Builder
    interface Builder {

        fun build(): ChatComponent

        @BindsInstance
        fun setView(view: ChatContract.View): Builder
    }
}

@Module
class MyModule {

    @PresenterSingleton
    @Provides
    fun getPresenter(presenter: ChatPresenter): ChatContract.Presenter {
        return presenter
    }

    @Provides
    fun getLifecycleCallback(presenter: ChatContract.Presenter): LifecycleCallback {
        return presenter.getLifecycleCallback()
    }
}