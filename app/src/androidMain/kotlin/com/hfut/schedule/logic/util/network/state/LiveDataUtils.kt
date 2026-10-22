package com.hfut.schedule.logic.util.network.state

import android.os.Handler
import android.os.Looper
import com.hfut.schedule.logic.util.state.MutableLiveData

@Deprecated("LiveData已不再作为本项目主力")
fun <T> reEmptyLiveDta(liveData: MutableLiveData<T?>) {
    Handler(Looper.getMainLooper()).post { liveData.value = null }
}