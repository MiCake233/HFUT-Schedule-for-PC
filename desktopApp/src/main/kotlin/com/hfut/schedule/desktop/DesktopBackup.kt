package com.hfut.schedule.desktop

import com.xah.common.platform.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.UIManager
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * 配置备份与恢复。
 *
 * 备份：把数据目录（默认 `%APPDATA%\HFUT-Schedule`，不含 cache）打包为 zip，由文件对话框选择保存位置。
 * 恢复：选择一个 zip，解压覆盖数据目录后立即退出进程，避免内存中的旧配置回写。
 */
object DesktopBackup {

    private val EXCLUDE = setOf("cache")

    private val stamp: String
        get() = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

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
        }.getOrNull()
    }

    /** 恢复；返回是否执行了恢复（成功后会退出进程） */
    fun restore(): Boolean {
        val source = chooseOpenFile() ?: return false
        if (!source.isFile || source.length() == 0L) {
            showError("所选文件为空或不存在")
            return false
        }
        val result = JOptionPane.showConfirmDialog(
            null,
            "恢复将覆盖当前全部本地数据，且过程中程序会退出。\n是否继续？",
            "确认恢复",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE
        )
        if (result != JOptionPane.OK_OPTION) return false

        val ok = runCatching {
            unzipToDataDir(source)
            true
        }.getOrElse {
            showError("恢复失败：${it.message}")
            false
        }
        if (ok) {
            JOptionPane.showMessageDialog(null, "数据已恢复，程序即将退出，请重新启动。", "完成", JOptionPane.INFORMATION_MESSAGE)
            // 直接终止，避免偏好设置缓存回写覆盖刚恢复的文件
            Runtime.getRuntime().halt(0)
        }
        return ok
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

    private fun unzipToDataDir(source: File) {
        val root = dataDir()
        // 先清空除 cache 外的内容，保证恢复到备份时的状态
        root.listFiles()?.forEach { child ->
            if (child.name !in EXCLUDE) child.deleteRecursively()
        }
        root.mkdirs()
        ZipInputStream(BufferedInputStream(FileInputStream(source))).use { zip ->
            var entry: ZipEntry? = zip.nextEntry
            while (entry != null) {
                val name = entry.name
                if (name.isNotEmpty() && !name.contains("..")) {
                    val target = File(root, name)
                    if (entry.isDirectory) {
                        target.mkdirs()
                    } else {
                        target.parentFile?.mkdirs()
                        BufferedOutputStream(FileOutputStream(target)).use { out -> zip.copyTo(out) }
                    }
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
    }

    private fun chooseSaveFile(defaultName: String): File? {
        ensureLookAndFeel()
        val chooser = JFileChooser().apply {
            dialogTitle = "选择备份文件的保存位置"
            selectedFile = File(defaultName)
            fileFilter = FileNameExtensionFilter("ZIP 备份 (*.zip)", "zip")
        }
        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return null
        val file = chooser.selectedFile
        return if (file.name.lowercase().endsWith(".zip")) file else File(file.parentFile, "${file.name}.zip")
    }

    private fun chooseOpenFile(): File? {
        ensureLookAndFeel()
        val chooser = JFileChooser().apply {
            dialogTitle = "选择要恢复的备份文件"
            fileFilter = FileNameExtensionFilter("ZIP 备份 (*.zip)", "zip")
        }
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return null
        return chooser.selectedFile
    }

    private fun ensureLookAndFeel() {
        runCatching { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()) }
    }

    private fun showError(message: String) {
        withContextUnsafe { JOptionPane.showMessageDialog(null, message, "错误", JOptionPane.ERROR_MESSAGE) }
    }

    private fun withContextUnsafe(block: () -> Unit) {
        if (javax.swing.SwingUtilities.isEventDispatchThread()) block() else javax.swing.SwingUtilities.invokeLater(block)
    }

    /** 以 `Dispatchers.IO` 执行备份，避免阻塞界面 */
    suspend fun backupAsync(): File? = withContext(Dispatchers.IO) { backup() }
}
