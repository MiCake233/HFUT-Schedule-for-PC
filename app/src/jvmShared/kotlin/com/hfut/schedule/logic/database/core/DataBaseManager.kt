package com.hfut.schedule.logic.database.core

import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object DataBaseManager {

    private val enableSafeMode by lazy {
        runBlocking {
            !DataStoreManager.enableDataStore.first()
        }
    }

    private val dataBaseSafely by lazy {
        AppDataBaseSafely()
    }
    private val dataBase by lazy {
        createAppDataBase("app-database")
    }

    val customEventDao by lazy {
        if (!enableSafeMode) {
            dataBase.customEventDao()
        } else {
            dataBaseSafely.customEventDao()
        }
    }
    val showerLabelDao by lazy {
        if (!enableSafeMode) {
            dataBase.showerLabelDao()
        } else {
            dataBaseSafely.showerLabelDao()
        }
    }
    val specialWorkDayDao by lazy {
        if (!enableSafeMode) {
            dataBase.specialWorkDayDao()
        } else {
            dataBaseSafely.specialWorkDayDao()
        }
    }
    val webUrlDao by lazy {
        if (!enableSafeMode) {
            dataBase.webUrlDao()
        } else {
            dataBaseSafely.webUrlDao()
        }
    }
    val friendDao by lazy {
        if (!enableSafeMode) {
            dataBase.friendDao()
        } else {
            dataBaseSafely.friendDao()
        }
    }
    val examRecordDao by lazy {
        if (!enableSafeMode) {
            dataBase.examRecordDao()
        } else {
            dataBaseSafely.examRecordDao()
        }
    }
}
