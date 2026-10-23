package com.hfut.schedule.logic.util.network.state
import com.hfut.schedule.logic.util.sys.runOnMain

import com.hfut.schedule.logic.util.state.MutableLiveData

@Deprecated("LiveData已不再作为本项目主力")
fun <T> reEmptyLiveDta(liveData: MutableLiveData<T?>) {
    runOnMain { liveData.value = null }
}