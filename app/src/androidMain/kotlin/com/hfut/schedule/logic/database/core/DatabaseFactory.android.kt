package com.hfut.schedule.logic.database.core

import androidx.room.Room
import com.hfut.schedule.application.MyApplication

actual fun createAppDataBase(name: String): AppDataBase =
    Room.databaseBuilder(
        MyApplication.context,
        AppDataBase::class.java,
        name
    ).addMigrations(*APP_DB_MIGRATIONS).build()
