package com.xianghe.ivy.http.scheduler

import io.reactivex.Scheduler

/**
 * @Author:Ycl
 * @Date:2017-03-11 14:31
 * @Desc:
 */
interface BaseSchedulerProvider {

    fun computation(): Scheduler

    fun io(): Scheduler

    fun ui(): Scheduler
}