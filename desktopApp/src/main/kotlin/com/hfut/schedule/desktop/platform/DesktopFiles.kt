package com.hfut.schedule.desktop.platform

import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * 备份文件对话框。
 *
 * Windows 继续用 Swing [JFileChooser]（和原先行为一致）。
 * macOS 用 AWT [FileDialog]，这是系统原生的打开/存储面板。
 */
internal object DesktopFiles {

    fun chooseSaveZip(defaultName: String): File? = when (currentDesktopOs()) {
        DesktopOs.MACOS -> macChoose(defaultName, FileDialog.SAVE)
        else -> swingSave(defaultName)
    }

    fun chooseOpenZip(): File? = when (currentDesktopOs()) {
        DesktopOs.MACOS -> macChoose("backup.zip", FileDialog.LOAD)
        else -> swingOpen()
    }

    private fun swingSave(defaultName: String): File? {
        val chooser = JFileChooser().apply {
            dialogTitle = "选择备份文件的保存位置"
            selectedFile = File(defaultName)
            fileFilter = FileNameExtensionFilter("ZIP 备份 (*.zip)", "zip")
        }
        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return null
        val file = chooser.selectedFile ?: return null
        return if (file.name.lowercase().endsWith(".zip")) file else File(file.parentFile, "${file.name}.zip")
    }

    private fun swingOpen(): File? {
        val chooser = JFileChooser().apply {
            dialogTitle = "选择要恢复的备份文件"
            fileFilter = FileNameExtensionFilter("ZIP 备份 (*.zip)", "zip")
        }
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return null
        return chooser.selectedFile
    }

    private fun macChoose(defaultName: String, mode: Int): File? {
        val title = if (mode == FileDialog.SAVE) "选择备份文件的保存位置" else "选择要恢复的备份文件"
        val dialog = FileDialog(null as Frame?, title, mode)
        dialog.file = defaultName
        dialog.isVisible = true
        val name = dialog.file ?: return null
        val dir = dialog.directory ?: return null
        val file = File(dir, name)
        return if (mode == FileDialog.SAVE && !file.name.lowercase().endsWith(".zip")) {
            File(file.parentFile, "${file.name}.zip")
        } else {
            file
        }
    }
}
