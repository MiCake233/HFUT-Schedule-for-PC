package com.hfut.schedule.desktop

import com.xah.common.platform.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * 配置备份与恢复。
 *
 * 备份：把数据目录（默认 `%APPDATA%\HFUT-Schedule`，不含 cache / update）打包为 zip，由文件对话框选择保存位置。
 * 恢复：先解压到**临时目录并逐条校验**，全部成功后才替换数据目录；任何一步失败都保留原数据。
 * （旧实现是先删数据再解压，选错文件或 zip 损坏会直接丢配置。）
 */
object DesktopBackup {

    /** 不参与备份、恢复时也不清空的子目录 */
    private val EXCLUDE = setOf("cache", "update")

    /** 解压上限：防止畸形的 zip（zip bomb）把磁盘写满 */
    private const val MAX_TOTAL_BYTES = 512L * 1024 * 1024
    private const val MAX_ENTRIES = 5000

    private val stamp: String
        get() = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

    /** 数据目录 */
    fun dataDir(): File = File(Platform.dataDirPath())

    /** 备份；返回写出的文件，取消或失败返回 null */
    fun backup(): File? {
        val target = chooseSaveFile("HFUT-Schedule_备份_$stamp.zip") ?: return null
        return runCatching {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(target))).use { zip ->
                zipDirectory(dataDir(), dataDir(), zip)
            }
            target
        }.getOrElse {
            runCatching { target.delete() }
            showError("备份失败：${it.message}")
            null
        }
    }

    /** 恢复；返回是否执行了恢复（成功后会退出进程） */
    fun restore(): Boolean {
        val source = chooseOpenFile() ?: return false
        if (!source.isFile || source.length() == 0L) {
            showError("所选文件为空或不存在")
            return false
        }
        if (!confirmRestore()) return false

        val ok = runCatching {
            replaceDataWith(source)
            true
        }.getOrElse {
            showError("恢复失败，原有数据已保留：${it.message}")
            false
        }
        if (ok) {
            showInfo("数据已恢复，程序即将退出，请重新启动。")
            // 直接终止，避免偏好设置缓存回写覆盖刚恢复的文件
            Runtime.getRuntime().halt(0)
        }
        return ok
    }

    /**
     * 解压到暂存目录 → 校验非空 → 把现有数据挪到旁路目录 → 落地 → 删除旁路。
     * 落地过程中出错则把旁路目录移回来。
     */
    internal fun replaceDataWith(source: File, root: File = dataDir()) {
        root.mkdirs()
        val parent = root.parentFile ?: throw IOException("数据目录不可写")
        val staging = File(parent, "${root.name}.restore-${stamp}").apply { deleteRecursively(); mkdirs() }
        val aside = File(parent, "${root.name}.before-restore-${stamp}").apply { deleteRecursively(); mkdirs() }
        try {
            extractValidated(source, staging)
            if (staging.walkTopDown().none { it.isFile }) throw IOException("备份文件里没有可恢复的内容")

            // 1) 现有数据先挪到旁路目录（保留 cache / update）
            val moved = mutableListOf<Pair<File, File>>()
            root.listFiles()?.forEach { child ->
                if (child.name in EXCLUDE) return@forEach
                val dest = File(aside, child.name)
                if (move(child, dest)) moved.add(child to dest)
            }

            // 2) 暂存内容落地
            try {
                staging.listFiles()?.forEach { child ->
                    val dest = File(root, child.name)
                    if (dest.exists()) dest.deleteRecursively()
                    if (!move(child, dest)) throw IOException("无法写入 ${dest.name}")
                }
            } catch (t: Throwable) {
                // 回滚：清掉半成品，把旁路目录移回原位
                root.listFiles()?.forEach { if (it.name !in EXCLUDE) it.deleteRecursively() }
                moved.forEach { (original, backup) ->
                    if (backup.exists()) move(backup, original)
                }
                throw t
            }
        } catch (t: Throwable) {
            // 失败时不删旁路目录：宁可多占一份空间，也不能把用户原数据删掉
            val kept = aside.walkTopDown().firstOrNull { it.isFile }
            val hint = if (kept != null) "；原数据保留在 ${aside.absolutePath}" else ""
            throw IOException("${t.message ?: "恢复中断"}$hint", t)
        } finally {
            staging.deleteRecursively()
        }
        aside.deleteRecursively()
    }

    /** 同一分区直接 rename，跨分区退化为复制 */
    private fun move(from: File, to: File): Boolean {
        to.parentFile?.mkdirs()
        if (from.renameTo(to)) return true
        return runCatching {
            if (from.isDirectory) from.copyRecursively(to, overwrite = true) else from.copyTo(to, overwrite = true)
            from.deleteRecursively()
            true
        }.getOrDefault(false)
    }

    private fun zipDirectory(root: File, current: File, zip: ZipOutputStream) {
        val children = current.listFiles() ?: return
        for (child in children) {
            if (current == root && child.name in EXCLUDE) continue
            val entryName = child.absolutePath.removePrefix(root.absolutePath).removePrefix(File.separator)
            if (child.isDirectory) {
                zip.putNextEntry(ZipEntry("$entryName/"))
                zip.closeEntry()
                zipDirectory(root, child, zip)
            } else {
                zip.putNextEntry(ZipEntry(entryName))
                BufferedInputStream(FileInputStream(child)).use { it.copyTo(zip) }
                zip.closeEntry()
            }
        }
    }

    /** 解压到指定目录；逐条校验条目名，拒绝绝对路径 / 上级目录 / 超量内容 */
    private fun extractValidated(source: File, target: File) {
        val rootPath = target.canonicalPath + File.separator
        var entries = 0
        var total = 0L
        ZipInputStream(BufferedInputStream(FileInputStream(source))).use { zip ->
            var entry: ZipEntry? = zip.nextEntry
            while (entry != null) {
                if (++entries > MAX_ENTRIES) throw IOException("备份条目过多，已中止")
                val name = entry.name.replace('\\', '/')
                if (name.isNotBlank()) {
                    if (name.contains("..") || name.startsWith("/") || name.contains(':')) {
                        throw IOException("备份内含非法路径：$name")
                    }
                    val dest = File(target, name)
                    if (!dest.canonicalPath.startsWith(rootPath) && dest.canonicalPath != target.canonicalPath) {
                        throw IOException("备份内含越界路径：$name")
                    }
                    if (entry.isDirectory) {
                        dest.mkdirs()
                    } else {
                        dest.parentFile?.mkdirs()
                        BufferedOutputStream(FileOutputStream(dest)).use { out ->
                            val buffer = ByteArray(64 * 1024)
                            while (true) {
                                val read = zip.read(buffer)
                                if (read <= 0) break
                                total += read
                                if (total > MAX_TOTAL_BYTES) throw IOException("备份解压后超过 512 MB，已中止")
                                out.write(buffer, 0, read)
                            }
                        }
                    }
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
    }

    /** Swing 对话框必须在事件线程上创建，否则可能死锁或画不出来 */
    private fun <T> onEdt(block: () -> T): T {
        if (SwingUtilities.isEventDispatchThread()) return block()
        var result: Result<T>? = null
        SwingUtilities.invokeAndWait { result = runCatching(block) }
        return result!!.getOrThrow()
    }

    private fun chooseSaveFile(defaultName: String): File? = onEdt {
        ensureLookAndFeel()
        val chooser = JFileChooser().apply {
            dialogTitle = "选择备份文件的保存位置"
            selectedFile = File(defaultName)
            fileFilter = FileNameExtensionFilter("ZIP 备份 (*.zip)", "zip")
        }
        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return@onEdt null
        val file = chooser.selectedFile
        if (file.name.lowercase().endsWith(".zip")) file else File(file.parentFile, "${file.name}.zip")
    }

    private fun chooseOpenFile(): File? = onEdt {
        ensureLookAndFeel()
        val chooser = JFileChooser().apply {
            dialogTitle = "选择要恢复的备份文件"
            fileFilter = FileNameExtensionFilter("ZIP 备份 (*.zip)", "zip")
        }
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return@onEdt null
        chooser.selectedFile
    }

    private fun confirmRestore(): Boolean = onEdt {
        JOptionPane.showConfirmDialog(
            null,
            "恢复将覆盖当前全部本地数据，且过程中程序会退出。\n是否继续？",
            "确认恢复",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE
        ) == JOptionPane.OK_OPTION
    }

    private fun ensureLookAndFeel() {
        runCatching { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()) }
    }

    private fun showError(message: String) = onEdt { showMessage(message, JOptionPane.ERROR_MESSAGE) }

    private fun showInfo(message: String) = onEdt { showMessage(message, JOptionPane.INFORMATION_MESSAGE) }

    private fun showMessage(message: String, type: Int) {
        JOptionPane.showMessageDialog(null, message, if (type == JOptionPane.ERROR_MESSAGE) "错误" else "完成", type)
    }

    /** 以 `Dispatchers.IO` 执行备份，避免阻塞界面 */
    suspend fun backupAsync(): File? = withContext(Dispatchers.IO) { backup() }
}
