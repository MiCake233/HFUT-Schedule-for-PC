package com.hfut.schedule.logic.database.core

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.xah.common.platform.Platform
import java.io.File

actual fun createAppDataBase(name: String): AppDataBase {
    val dir = File(Platform.dataDirPath(), "database").apply { mkdirs() }
    val file = File(dir, "$name.db")
    return Room.databaseBuilder<AppDataBase>(name = file.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .addMigrations(*APP_DB_MIGRATIONS)
        .build()
}
