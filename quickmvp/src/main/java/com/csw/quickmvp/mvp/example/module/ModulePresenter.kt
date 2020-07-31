package com.csw.quickmvp.mvp.example.module

import com.csw.quickmvp.mvp.base.BasePresenterImpl
import com.csw.quickmvp.mvp.example.app.AppModel
import javax.inject.Inject

/**
 * 模块切面实现,
 * 构造方法添加@Inject注解
 * 在Component中就可以自动通过构造方法实例化对象
 * 所需参数只需要在Component有对应实例或者对应实例的Provide存在就行
 * 由于调用此构造方法是Component编译时自动生成的，所以这样随便修改构造方法
 * 所需要的参数，都不需要修改其它地方，这就是依赖注入好处，不关心实例怎么生成的，
 * 反正往Component配置好实例的生成方式，使用时通过Component注入即可
 */
class ModulePresenter @Inject constructor(
    view: ModuleContract.View,
    private val appModel: AppModel
) : BasePresenterImpl<ModuleContract.View>(view)
    , ModuleContract.Presenter {

    override fun setInitParams() {
    }

    override fun initUIData() {
        super.initUIData()
        view.updateUI()
    }

}