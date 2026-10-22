package com.hfut.schedule.ui.component.icon

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource
import com.hfut.schedule.R
import com.xah.common.logic.util.EMPTY_STRING

@Composable
fun DepartmentIcons(name : String, modifier: Modifier = Modifier) = Icon(
    painterResource(departmentIcon(name)),
    null,
    modifier
)

/**
 * 学院图标重绘
 * @param name 学院名称
 *
 * CNM，总改学院名，总要重新适配，加个“智能”字眼就起飞了是吧，直接叫合肥工业智能大学得了呗
 */
fun departmentIcon(name : String) : Int =
    // 学院
    if(name.contains("计算机与信息学院")) Drawables.data_object
    else if(name.contains("资源与环境工程学院")) Drawables.eco
    else if(name.contains("数学学院")) Drawables.function
    else if(name.contains("外国语学院")) Drawables.translate
    else if(name.contains("马克思主义学院")) Drawables.cognition
    else if(name.contains("机械工程学院")) Drawables.settings
    else if(name.contains("材料科学与工程学院")) Drawables.texture
    else if(name.contains("电气与自动化工程学院")) Drawables.flash_on
    else if(name.contains("土木与水利工程学院")) Drawables.precision_manufacturing
    else if(name.contains("化学与化工学院")) Drawables.science
    else if(name.contains("经济学院")) Drawables.currency_pound
    else if(name.contains("文法学院")) Drawables.newsstand
    // 新名字：管理与智能科学学院，旧名字：管理学院
    else if(name.contains("管理")) Drawables.account_tree
    else if(name.contains("仪器科学与光电工程学院")) Drawables.body_fat
    else if(name.contains("建筑与艺术学院")) Drawables.domain
    else if(name.contains("食品与生物工程学院")) Drawables.genetics
    else if(name.contains("微电子学院")) Drawables.empty_dashboard
    else if(name.contains("物理学院")) Drawables.category
    else if(name.contains("汽车与交通工程学院")) Drawables.directions_car
    // 新名字：智能软件工程学院，旧名字：软件学院
    else if(name.contains("软件")) Drawables.code
    else if(name.contains("聚变科学与工程学院")) Drawables.orbit
    else if(name.contains("卓越工程师学院")) Drawables.manufacturing
    else if(name.contains("创新学院")) Drawables.stylus_laser_pointer // 集成电路创新学院 人工智能创新学院 未来技术创新学院
    else if(name.contains("体育部")) Drawables.directions_run
    // 其余机构
    else if(name.contains("校医院")) Drawables.emergency
    else if(name.contains("工程素质教育中心")) Drawables.massage
    else if(name.contains("创新创业教育处")) Drawables.stylus_laser_pointer
    // 为二课适配的图标
    else if(name.contains("教务处") || name.contains("研究生")) Drawables.school
    else if(name.contains("党委学生工作部") || name.contains("团委")) Drawables.person_play
    else if(name.contains("招生与就业处")) Drawables.azm
    else if(name.contains("校领导")) Drawables.person
    // 未知图标
    else Drawables.crossword

@Composable
fun BillsIcons(
    name : String,
    modifier: Modifier = Modifier
) = Icon(
    painterResource(billsIcons(name)),
    contentDescription = EMPTY_STRING,
    modifier
)

fun billsIcons(name : String) = when {
    name.contains("餐饮") || name.contains("食堂") -> Drawables.restaurant
    name.contains("超市") || name.contains("贸易") || name.contains("商店") -> Drawables.storefront
    name.contains("淋浴") || name.contains("呱呱物联") -> Drawables.bathtub
    name.contains("网") -> Drawables.net
    name.contains("电") -> Drawables.flash_on
    name.contains("打印") -> Drawables.print
    name.contains("医院") || name.contains("药")-> Drawables.emergency
    name.contains("充值") -> Drawables.add_card
    name.contains("补助") -> Drawables.volunteer_activism
    // 默认
    else ->  Drawables.paid
}

fun String.filterDepartmentName()  = this.substringBefore("（").substringBefore("(")