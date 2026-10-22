package com.hfut.schedule.ui.screen.home.search.function.jxglstu.person

import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.component.icon.filterDepartmentName
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.logic.util.LogUtil
import org.jsoup.Jsoup

/**
 * 学生信息。
 * 原先定义在 PersonItems.kt（Android 专属 UI 文件）里，桌面端控制器/埋点都要用，抽到共享源集。
 */
data class PersonInfo(
    val name: String?,
    val studentId: String?,
    val chineseID: String?,
    val className: String?,
    val major: String?,
    val department: String?,
    val campus: String?,
    val educationLevel: String?,
    val home: String?,
    val status: String?,
    val program: String?,
    val startDate: String?,
    val endDate: String?,
    val majorDirection: String?,
    val studyTime: String?,
    val gender: String?,
    val politicalStatus: String?, // 政治面貌
    val email: String?,
    val phone: String?,
    val mobile: String?,
    val address: String?,
    val postalCode: String? // 邮编
) {
    fun getStudentIdFinally(): String? = this.studentId ?: prefs.getString("Username", null)

    fun getNameFinally(): String = this.name ?: prefs.getString("Username", null) ?: "游客"

    /** 是否是游客，登陆成功之后回保存学号，没登陆成功过就是游客 */
    fun isAnonymous() = getStudentIdFinally() == null
}

fun getPersonInfo(): PersonInfo {
    try {
        val info = prefs.getString("info", "")

        val doc = info?.let { Jsoup.parse(it) }
        val studentnumber = doc?.select("li.list-group-item.text-right:contains(学号) span")?.last()?.text()
        val name = doc?.select("li.list-group-item.text-right:contains(中文姓名) span")?.last()?.text()
        val chineseid = doc?.select("li.list-group-item.text-right:contains(证件号) span")?.last()?.text()
        val elements = doc?.select("dl dt, dl dd")

        val infoMap = mutableMapOf<String, String>()
        if (elements != null) {
            for (i in 0 until elements.size step 2) {
                val key = elements[i].text()
                val value = elements[i + 1].text()
                infoMap[key] = value
            }
        }

        val benorsshuo = infoMap[elements?.get(8)?.text()]
        val department = infoMap[elements?.get(10)?.text()]?.filterDepartmentName()
        val zhuanye = infoMap[elements?.get(12)?.text()]
        val classes = infoMap[elements?.get(16)?.text()]
        val school = infoMap[elements?.get(18)?.text()]
        val home = infoMap[elements?.get(80)?.text()]

        val xueJiStatus = infoMap[elements?.get(20)?.text()] //学籍状态 正常 转专业
        val program = infoMap[elements?.get(22)?.text()]
        val startDate = infoMap[elements?.get(38)?.text()]
        val endDate = infoMap[elements?.get(86)?.text()]
        val majorDirection = infoMap[elements?.get(14)?.text()] //专业方向
        val studyTime = infoMap[elements?.get(36)?.text()] //学制 4.0

        val profileJson = prefs.getString("profile", EMPTY_STRING) ?: EMPTY_STRING
        val doc2 = Jsoup.parse(profileJson)
        val items = doc2.select(".list-group-item")

        val dataMap = mutableMapOf<String, String>()
        items.forEach { item ->
            val key = item.select("div.col-md-3 strong").text().trim()
            val value = item.select("div.col-md-6 span").first()?.ownText()?.trim() ?: EMPTY_STRING
            if (key.isNotEmpty() && value.isNotEmpty()) {
                dataMap[key] = value
            }
        }
        val gender = dataMap["性别"]
        val politicalStatus = dataMap["政治面貌"]
        val email = dataMap["邮箱"]
        val phone = dataMap["电话"]
        val mobile = dataMap["手机"]
        val address = dataMap["地址"]
        val postalCode = dataMap["邮编"]

        return PersonInfo(
            name, studentnumber, chineseid, classes, zhuanye, department, school, benorsshuo, home,
            xueJiStatus, program, startDate, endDate, majorDirection, studyTime,
            gender, politicalStatus, email, phone, mobile, address, postalCode
        )
    } catch (e: Exception) {
        LogUtil.error(e)
        return PersonInfo(
            null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null
        )
    }
}
/** 学号 + 学校邮箱后缀 */
fun getSchoolEmail(): String? = getPersonInfo().getStudentIdFinally()?.let { it + com.hfut.schedule.logic.model.AppConst.EMAIL }