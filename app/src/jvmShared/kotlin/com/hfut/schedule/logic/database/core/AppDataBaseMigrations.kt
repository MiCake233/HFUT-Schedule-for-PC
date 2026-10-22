package com.hfut.schedule.logic.database.core

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/** 数据库迁移（与平台无关，两端共用） */
private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE event ADD COLUMN supabase_id INTEGER DEFAULT NULL")
    }
}

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS special_work_day (
                origin_date TEXT NOT NULL PRIMARY KEY,
                target_date TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}

private val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS web_url (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                url TEXT NOT NULL,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                supabase_id INTEGER
            )
            """.trimIndent()
        )
        connection.execSQL("CREATE UNIQUE INDEX index_web_url_url ON web_url(url)")
    }
}

private val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(connection: SQLiteConnection) {
        // 新建表friend
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `friend` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `studentId` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `major` TEXT
            )
            """.trimIndent()
        )
        // 删除表course
        connection.execSQL("DROP TABLE IF EXISTS `course`")
    }
}

private val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS exam_record (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                dateTime TEXT NOT NULL,
                place TEXT,
                type TEXT,
                source TEXT NOT NULL,
                semester INTEGER NOT NULL,
                fetchedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
        connection.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_exam_record_semester_source_name_dateTime ON exam_record(semester, source, name, dateTime)"
        )
        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS index_exam_record_semester ON exam_record(semester)"
        )
    }
}

private val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE event ADD COLUMN group_id TEXT")
    }
}

internal val APP_DB_MIGRATIONS: Array<Migration> = arrayOf(
    MIGRATION_1_2,
    MIGRATION_2_3,
    MIGRATION_3_4,
    MIGRATION_4_5,
    MIGRATION_5_6,
    MIGRATION_6_7
)
