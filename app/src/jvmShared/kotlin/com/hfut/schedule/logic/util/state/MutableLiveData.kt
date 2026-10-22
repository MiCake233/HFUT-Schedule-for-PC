package com.hfut.schedule.logic.util.state

/**
 * 极简 LiveData 替代品。
 *
 * 原项目大量使用 `androidx.lifecycle.MutableLiveData`（Android 专有，且没有 KMP 版本），
 * 作者本人的方向也是迁移到 StateFlow/`UiStateHolder`。为不阻塞桌面端，
 * 这里提供一个 API 形状兼容的最小实现（value/observe/observeForever/postValue），
 * 后续可平滑替换成 StateFlow。
 */
class MutableLiveData<T> {

    constructor()
    constructor(initial: T?) { this._value = initial }

    private val lock = Any()
    private val observers = mutableListOf<(T?) -> Unit>()

    private var _value: T? = null

    var value: T?
        get() = synchronized(lock) { _value }
        set(v) {
            val snapshot = synchronized(lock) {
                _value = v
                observers.toList()
            }
            snapshot.forEach { runCatching { it(v) } }
        }

    fun postValue(v: T?) {
        value = v
    }

    /** 兼容 `observe(lifecycleOwner) { }`：owner 仅用于占位，桌面端没有生命周期概念 */
    fun <O : Any> observe(owner: O, observer: (T?) -> Unit) {
        synchronized(lock) { observers.add(observer) }
        observer(value)
    }

    fun observeForever(observer: (T?) -> Unit) {
        synchronized(lock) { observers.add(observer) }
        observer(value)
    }

    fun removeObserver(observer: (T?) -> Unit) {
        synchronized(lock) { observers.remove(observer) }
    }

    fun removeObservers() {
        synchronized(lock) { observers.clear() }
    }

    fun hasObservers(): Boolean = synchronized(lock) { observers.isNotEmpty() }

    fun getOrNull(): T? = value

    fun setIfChanged(v: T?) {
        if (value != v) value = v
    }
}

/** 保持与原代码 `MutableLiveData<T>()` 相同的构造形态 */
fun <T> mutableLiveDataOf(initial: T? = null): MutableLiveData<T> = MutableLiveData(initial)
