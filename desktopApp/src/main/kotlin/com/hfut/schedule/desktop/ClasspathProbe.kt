package com.hfut.schedule.desktop

import java.io.File
import java.util.zip.ZipFile

/**
 * 运行时 classpath 自检（`--verify-classpath`）。
 *
 * 用途：确认发行包里没有两个 jar 带同一个类。这类冲突会让「用哪个版本」取决于
 * classpath 顺序，表现为偶发 `NoSuchMethodError` / `AbstractMethodError`，很难查。
 *
 * 扫的是进程真实的 `java.class.path`，所以安装版可以直接跑
 * `HFUT-Schedule.exe --verify-classpath`（需要从控制台启动才看得到输出）。
 */
fun runClasspathProbe(): Boolean {
    val entries = (System.getProperty("java.class.path") ?: "").split(File.pathSeparatorChar)
        .filter { it.isNotBlank() }
    val jars = entries.map { File(it) }.filter { it.isFile && it.name.endsWith(".jar", ignoreCase = true) }
    val dirs = entries.map { File(it) }.filter { it.isDirectory }

    val owners = HashMap<String, MutableList<String>>()
    var classCount = 0
    for (jar in jars) {
        runCatching {
            ZipFile(jar).use { zip ->
                val it = zip.entries()
                while (it.hasMoreElements()) {
                    val name = it.nextElement().name
                    if (!name.endsWith(".class") || name.startsWith("META-INF/")) continue
                    classCount++
                    owners.getOrPut(name) { mutableListOf() }.add(jar.name)
                }
            }
        }.onFailure { println("WARN 无法读取 ${jar.name}: ${it.message}") }
    }
    for (dir in dirs) {
        dir.walkTopDown().filter { it.isFile && it.name.endsWith(".class") }.forEach { file ->
            val fqn = file.relativeTo(dir).invariantSeparatorsPath
            classCount++
            owners.getOrPut(fqn) { mutableListOf() }.add("<classes dir>")
        }
    }

    val duplicates = owners.filterValues { it.size > 1 }.toSortedMap()
    println("CLASSPATH_JARS=${jars.size}")
    println("CLASSPATH_DIRS=${dirs.size}")
    println("CLASSPATH_CLASSES=$classCount")
    println("DUPLICATE_CLASSES=${duplicates.size}")
    duplicates.entries.take(20).forEach { (fqn, from) ->
        println("  $fqn  <=  ${from.joinToString(" | ")}")
    }
    val ok = duplicates.isEmpty()
    println("CLASSPATH_OK=$ok")
    return ok
}
