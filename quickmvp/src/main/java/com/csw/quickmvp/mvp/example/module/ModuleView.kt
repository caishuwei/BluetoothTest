@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantOverride")

package com.csw.quickmvp.mvp.example.module

import com.csw.quickmvp.R
import com.csw.quickmvp.mvp.example.app.AppModel
import com.csw.quickmvp.mvp.example.app.MyApplication
import com.csw.quickmvp.mvp.ui.BaseMVPActivity
import javax.inject.Inject

class ModuleView : BaseMVPActivity<ModuleContract.Presenter>(), ModuleContract.View {

    //这里也可以通过注入，获取在AppComponent ModuleComponent中存在的实例
    @Inject
    lateinit var appModel: AppModel

    override fun initInject() {
        super.initInject()
        MyApplication.instance.appComponent.getModuleComponentBuilder()
            .setView(this)
            .build()
            .inject(this)
    }

    override fun getContentViewID(): Int {
        return R.layout.quick_mvp_activity_common
    }

    override fun initData() {
        super.initData()

        presenter.setInitParams()
        presenter.initUIData()
    }

    //View------------------------------------------------------------------------------------------

    override fun updateUI() {

    }

}