package com.wwwjf.jetpackdemo.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.wwwjf.jetpackdemo.log
import java.lang.NullPointerException

object BusLiveData {

    //存放订阅者
    private val bus: MutableMap<String, BusMutableLiveData<Any>> by lazy { HashMap() }

    @Synchronized
    fun <T> with(key: String, type: Class<T>, isStick: Boolean = true): BusMutableLiveData<T> {
        if (!bus.containsKey(key)) {
            log("!containKey")
            bus[key] = BusMutableLiveData(isStick)
        }
        log("with...isStick=${isStick},key=${key},bus:${bus.size}")
        return bus[key] as BusMutableLiveData<T>
    }

}

class BusMutableLiveData<T> private  constructor(): MutableLiveData<T>() {

    private var isStick: Boolean = false

    constructor(isStick: Boolean):this(){
        this.isStick = isStick
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, observer)
        log("1-observe......${isStick}")
        if (!isStick){
            log("2-hookliveData......${isStick}")
            hookLiveData(observer)
        }
        log("3-observe......${isStick}")
    }

    /**
     * 去除livedata粘性数据
     */
    private fun hookLiveData(observer: Observer<in T>){

        //1.mLastVersion
        val liveDataClass  = Class.forName(LiveData::class.java.name)
        log("${liveDataClass}")

        val mObserversField = liveDataClass.getDeclaredField("mObservers")
        mObserversField.isAccessible = true

        val mObserversObject = mObserversField.get(this)

        val mObserversClass = mObserversObject.javaClass

        val getMethod = mObserversClass.getDeclaredMethod("get",Any::class.java)
        getMethod.isAccessible = true
        val invokeEntry = getMethod.invoke(mObserversObject,observer)

        var observerWrapper:Any? = null
        if (invokeEntry != null && invokeEntry is Map.Entry<*, *>){
            observerWrapper = invokeEntry.value
        }
        if (observerWrapper == null){
            throw NullPointerException("observerWrapper is null")
        }
        log("observerWrapper=${observerWrapper::class.java.name}")
        val supperClass = observerWrapper.javaClass.superclass
        val mLastVersionField = supperClass.getDeclaredField("mLastVersion")
        mLastVersionField.isAccessible = true


        //2.mVersion
        val mVersionField = liveDataClass.getDeclaredField("mVersion")
        log("$mVersionField")
        mVersionField.isAccessible = true

        //3.mLastVersion = mVersion
        val mVersionValue = mVersionField.get(this)
        mLastVersionField.set(observerWrapper,mVersionValue)

    }
}