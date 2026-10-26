package com.hfut.schedule.desktop

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * 备份 / 恢复的自检（`--verify-backup`）。
 *
 * 覆盖三条最要命的路径：
 * 1. 正常恢复：数据被替换，`cache` 不被清掉；
 * 2. 畸形 zip（含 `../` 越界条目）：必须拒绝，且**原数据一条都不能少**；
 * 3. 空 zip：必须拒绝，原数据保留。
 *
 * 全部使用临时目录，不触碰用户真实数据。
 */
fun runBackupProbe(): Boolean {
    val base = File(System.getProperty("java.io.tmpdir"), "hfut-backup-probe").apply { deleteRecursively(); mkdirs() }
    var allOk = true

    fun scenario(name: String, block: () -> Boolean) {
        val ok = runCatching(block).getOrElse {
            println("[$name] 异常：${it::class.simpleName}: ${it.message}")
            false
        }
        if (!ok) allOk = false
        println("[$name] " + if (ok) "PASS" else "FAIL")
    }

    /** 造一个「现有数据目录」 */
    fun freshRoot(tag: String): File {
        val root = File(base, "appdata-$tag").apply { deleteRecursively(); mkdirs() }
        File(root, "prefs").mkdirs()
        File(root, "prefs/com.hfut.schedule_preferences.properties").writeText("s|Username=old")
        File(root, "cache").mkdirs()
        File(root, "cache/blob.bin").writeText("keep-me")
        return root
    }

    fun zip(file: File, entries: Map<String, String>) {
        file.parentFile?.mkdirs()
        ZipOutputStream(file.outputStream()).use { zip ->
            entries.forEach { (name, content) ->
                zip.putNextEntry(ZipEntry(name))
                zip.write(content.toByteArray())
                zip.closeEntry()
            }
        }
    }

    scenario("restore-ok") {
        val root = freshRoot("ok")
        val zipFile = File(base, "ok.zip")
        zip(zipFile, mapOf("prefs/com.hfut.schedule_preferences.properties" to "s|Username=new"))
        DesktopBackup.replaceDataWith(zipFile, root)
        val prefs = File(root, "prefs/com.hfut.schedule_preferences.properties").readText()
        val cacheKept = File(root, "cache/blob.bin").readText() == "keep-me"
        val noLeftover = root.parentFile!!.listFiles()!!.none { it.name.startsWith("appdata-ok.before-restore") }
        println("  prefs=$prefs cacheKept=$cacheKept noBackupLeftover=$noLeftover")
        prefs.contains("new") && cacheKept && noLeftover
    }

    scenario("restore-zip-slip-rejected") {
        val root = freshRoot("slip")
        val zipFile = File(base, "slip.zip")
        zip(
            zipFile,
            mapOf(
                "prefs/com.hfut.schedule_preferences.properties" to "s|Username=new",
                "../evil.txt" to "pwned",
            )
        )
        val rejected = runCatching { DesktopBackup.replaceDataWith(zipFile, root) }.isFailure
        val originalIntact = File(root, "prefs/com.hfut.schedule_preferences.properties").readText().contains("old")
        val noEvil = !File(base.parentFile, "evil.txt").exists()
        println("  rejected=$rejected originalIntact=$originalIntact noEvil=$noEvil")
        rejected && originalIntact && noEvil
    }

    scenario("restore-empty-rejected") {
        val root = freshRoot("empty")
        val zipFile = File(base, "empty.zip")
        zip(zipFile, emptyMap())
        val rejected = runCatching { DesktopBackup.replaceDataWith(zipFile, root) }.isFailure
        val originalIntact = File(root, "prefs/com.hfut.schedule_preferences.properties").readText().contains("old")
        println("  rejected=$rejected originalIntact=$originalIntact")
        rejected && originalIntact
    }

    runCatching { base.deleteRecursively() }
    println("BACKUP_PROBE_OK=" + allOk)
    return allOk
}
