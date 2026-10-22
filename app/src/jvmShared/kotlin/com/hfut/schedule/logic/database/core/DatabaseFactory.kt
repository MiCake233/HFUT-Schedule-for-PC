package com.hfut.schedule.logic.database.core

import androidx.room.RoomDatabase

/**
 * 创建 AppDataBase。
 * Android: Room.databaseBuilder(context, ...)；桌面: Room KMP + BundledSQLiteDriver。
 */
expect fun createAppDataBase(name: String): AppDataBase
