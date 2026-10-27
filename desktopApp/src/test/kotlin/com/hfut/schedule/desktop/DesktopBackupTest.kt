package com.hfut.schedule.desktop

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * 备份恢复的安全测试。
 *
 * 重点是「恢复失败时绝不能弄丢原有数据」——这条以前是反的（先删再解压）。
 * 全部在临时目录里跑，不碰真实数据，也不需要 Swing。
 */
class DesktopBackupTest {

    private lateinit var base: File
    private lateinit var root: File

    @Before
    fun setUp() {
        base = File(System.getProperty("java.io.tmpdir"), "hfut-backup-test-" + System.nanoTime())
        root = File(base, "appdata").apply { mkdirs() }
        File(root, "prefs").mkdirs()
        File(root, "prefs/old.properties").writeText("s|Username=old")
        File(root, "cache").mkdirs()
        File(root, "cache/blob.bin").writeText("keep")
    }

    @After
    fun tearDown() {
        base.deleteRecursively()
    }

    private fun zip(name: String, entries: Map<String, String>): File {
        val file = File(base, name)
        ZipOutputStream(file.outputStream()).use { out ->
            entries.forEach { (entryName, content) ->
                out.putNextEntry(ZipEntry(entryName))
                out.write(content.toByteArray(Charsets.UTF_8))
                out.closeEntry()
            }
        }
        return file
    }

    @Test
    fun restoreReplacesDataAndKeepsCache() {
        val archive = zip("ok.zip", mapOf("prefs/old.properties" to "s|Username=new"))
        DesktopBackup.replaceDataWith(archive, root)

        assertEquals("s|Username=new", File(root, "prefs/old.properties").readText())
        assertEquals("keep", File(root, "cache/blob.bin").readText())
    }

    @Test
    fun restoreHandlesNestedDirectories() {
        val archive = zip(
            "nested.zip",
            mapOf(
                "database/db.sqlite" to "fake-db",
                "logs/a/b/c.log" to "nested-log",
            )
        )
        DesktopBackup.replaceDataWith(archive, root)
        assertEquals("fake-db", File(root, "database/db.sqlite").readText())
        assertEquals("nested-log", File(root, "logs/a/b/c.log").readText())
    }

    @Test
    fun zipSlipIsRejectedAndDataSurvives() {
        val archive = zip(
            "slip.zip",
            mapOf(
                "prefs/old.properties" to "s|Username=hijacked",
                "../escaped.txt" to "pwned",
            )
        )
        try {
            DesktopBackup.replaceDataWith(archive, root)
            fail("越界条目必须被拒绝")
        } catch (expected: Exception) {
            assertTrue(expected.message.orEmpty().isNotEmpty())
        }
        assertEquals("s|Username=old", File(root, "prefs/old.properties").readText())
        assertFalse(File(base, "escaped.txt").exists())
    }

    @Test
    fun absolutePathEntryIsRejected() {
        val archive = zip("abs.zip", mapOf("/etc/evil.txt" to "x"))
        try {
            DesktopBackup.replaceDataWith(archive, root)
            fail("绝对路径条目必须被拒绝")
        } catch (expected: Exception) {
            // ok
        }
        assertEquals("s|Username=old", File(root, "prefs/old.properties").readText())
    }

    @Test
    fun emptyArchiveIsRejectedAndDataSurvives() {
        val archive = zip("empty.zip", emptyMap())
        try {
            DesktopBackup.replaceDataWith(archive, root)
            fail("空备份必须被拒绝")
        } catch (expected: Exception) {
            // ok
        }
        assertEquals("s|Username=old", File(root, "prefs/old.properties").readText())
    }

    @Test
    fun corruptArchiveIsRejectedAndDataSurvives() {
        val corrupt = File(base, "corrupt.zip")
        corrupt.writeBytes(ByteArray(64) { 0x41 })
        try {
            DesktopBackup.replaceDataWith(corrupt, root)
            fail("损坏的备份必须被拒绝")
        } catch (expected: Exception) {
            // ok
        }
        assertEquals("s|Username=old", File(root, "prefs/old.properties").readText())
    }

    @Test
    fun noStagingDirectoriesAreLeftBehind() {
        val archive = zip("ok.zip", mapOf("prefs/old.properties" to "s|Username=new"))
        DesktopBackup.replaceDataWith(archive, root)
        val leftovers = base.listFiles().orEmpty().filter {
            it.name.contains(".restore-") || it.name.contains(".before-restore-")
        }
        assertTrue("恢复后不应残留暂存目录：$leftovers", leftovers.isEmpty())
    }
}
