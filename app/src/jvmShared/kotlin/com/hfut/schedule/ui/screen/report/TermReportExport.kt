package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

enum class TermReportExportModule(val title: String) {
    ACADEMIC_REPORT("学业报表"),
    ACADEMIC_ANALYSIS("学业分析"),
    EXPENSE_ANALYSIS("消费分析"),
    LIBRARY("图书馆报告"),
    LIFE("生活报告")
}

enum class TermReportExportAction {
    SAVE_TO_GALLERY,
    SHARE
}

data class GraduationInfo(
    val startYear: Int,
    val graduationYear: Int,
    val totalSemesters: Int
)

/** 报告的导出视图：与屏幕内展示一致，供截图 / 生成图片使用。 */
@Composable
fun TermReportExportContent(
    vm: NetWorkViewModel,
    semester: Int,
    modules: Set<TermReportExportModule>,
    isGraduating: Boolean = false,
    allSemesters: List<Int> = emptyList()
) {
    val periodLabel = if (isGraduating) "四年" else "本学期"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = CARD_NORMAL_DP)
        ) {
            if (TermReportExportModule.ACADEMIC_REPORT in modules) {
                AcademicReportSection(
                    vm = vm,
                    semester = semester,
                    allSemesters = if (isGraduating) allSemesters else emptyList(),
                    onLatestSemester = {}
                )
            }
            if (TermReportExportModule.ACADEMIC_ANALYSIS in modules) {
                AcademicAnalysisSection(vm, semester, periodLabel)
            }
            if (TermReportExportModule.EXPENSE_ANALYSIS in modules) {
                ExpenseAnalysisSection(vm, semester, periodLabel)
            }
            if (TermReportExportModule.LIBRARY in modules) {
                LibraryReportSection(vm, periodLabel)
            }
            if (TermReportExportModule.LIFE in modules) {
                LifeReportSection(vm, semester, allSemesters = if (isGraduating) allSemesters else emptyList())
            }
        }
    }
}

/**
 * 导出学期报告为图片文件。
 * Android 端保存到相册（或调用分享）；桌面端渲染为 PNG 并保存到下载目录。
 * @return 成功时返回保存位置的描述，失败返回 null
 */
expect suspend fun exportTermReportImage(
    vm: NetWorkViewModel,
    semester: Int,
    modules: Set<TermReportExportModule>,
    isGraduating: Boolean = false,
    allSemesters: List<Int> = emptyList()
): String?

fun detectGraduation(semesters: List<Int>): GraduationInfo? {
    if (semesters.size < 7) return null

    val years = semesters.mapNotNull { sem ->
        val text = SemesterParser.parseSemester(sem) ?: return@mapNotNull null
        val match = Regex("""(\d+)~(\d+)""").find(text) ?: return@mapNotNull null
        match.groupValues[1].toIntOrNull()
    }.distinct().sorted()

    if (years.size < 4) return null

    val startYear = years.first()
    val graduationYear = startYear + 4

    return GraduationInfo(
        startYear = startYear,
        graduationYear = graduationYear,
        totalSemesters = semesters.size
    )
}

