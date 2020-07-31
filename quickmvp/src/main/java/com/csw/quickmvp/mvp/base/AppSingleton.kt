@file:Suppress("unused")

package com.csw.quickmvp.mvp.base

import javax.inject.Scope

/**
 * 单例限制于App内
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppSingleton