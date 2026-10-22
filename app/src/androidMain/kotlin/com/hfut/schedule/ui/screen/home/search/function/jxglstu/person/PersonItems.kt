package com.hfut.schedule.ui.screen.home.search.function.jxglstu.person

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hfut.schedule.logic.util.helper.getCampusRegion

import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.ClipBoardHelper
import com.hfut.schedule.logic.util.sys.JumpTransitionEffectWallpaper
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.ui.component.button.BUTTON_END_PADDING
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.icon.CircleProgressIcon
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.icon.filterDepartmentName
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.nav.destination.ClassmatesDestination
import com.hfut.schedule.ui.nav.destination.PersonInfoDestination
import com.hfut.schedule.ui.nav.destination.ProgramDestination
import com.hfut.schedule.ui.nav.destination.SettingsHuiXinPasswordDestination
import com.hfut.schedule.ui.nav.destination.SettingsJxglstuPasswordDestination
import com.hfut.schedule.ui.screen.home.cube.sub.getJxglstuPassword

import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getHuiXinPsk
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getSchoolNetPsk
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.coverBlur
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.button.rememberLayerBackdrop

import com.xah.navigation.util.LocalNavController
import com.xah.common.ui.component.status.LoadingScreen
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.container.component.base.sharedContainer
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.logic.model.CampusRegion
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.logic.util.LogUtil
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PersonScreen(
    vm: NetWorkViewModel,
) {
    val hazeState = rememberHazeBlur()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val refreshNetwork  =  {
        scope.launch(Dispatchers.IO) {
            val cookie = getJxglstuCookie() ?: return@launch
            loading = true
            async {
                launch {
                    vm.getInfo(cookie)
                }
                launch {
                    vm.getPhoto(cookie)
                }
                launch {
                    vm.getAccount(cookie)
                }
            }.await()
            loading = false
        }
    }

    val backdrop = rememberLayerBackdrop()
    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(PersonInfoDestination.title.asString()) },
                navigationIcon = {
                    TopBarNavigationIcon()
                },
                actions = {
                    LiquidButton(
                        onClick = {
                            refreshNetwork()
                        },
                        modifier = Modifier.padding(end = BUTTON_END_PADDING),
                        isCircle = true,
                        backdrop = backdrop
                    ) {
                        Icon(painterResource(Drawables.rotate_right),null)
                    }
                }
            )
        },
    ) { innerPadding ->
        if(loading) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .backDropSource(backdrop)
                    .hazeSource(hazeState)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                InnerPaddingHeight(innerPadding,true)
                PersonItems()
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
//    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
private fun PersonItems(
//    vm : NetWorkViewModel,
//    navController : NavHostController,
) {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val photo by produceState<String?>(initialValue = null) {
        value = LargeStringDataManager.read( LargeStringDataManager.PHOTO)
    }

    val info = getPersonInfo()

    val studentnumber = info.getStudentIdFinally()
    val name = info.name
    val chineseid = info.chineseID


    val studyType = info.educationLevel
    val yuanxi = info.department?.filterDepartmentName()
    val major = info.major
    val classes = info.className
    val school = info.campus
    val home = info.home
    val xueJiStatus = info.status
    val program = info.program
    val startDate = info.startDate
    val endDate = info.endDate
    val majorDirection = info.majorDirection
    val studyTime = info.studyTime

    var show by rememberSaveable { mutableStateOf(false) }
    var show2 by rememberSaveable() { mutableStateOf(chineseid == null) }
    val cardPsk by produceState(initialValue = EMPTY_STRING) {
        value = getHuiXinPsk() ?: EMPTY_STRING
    }
    val schoolNetPsk by produceState(initialValue = EMPTY_STRING) {
        value = getSchoolNetPsk() ?: EMPTY_STRING
    }
    val jxglstuPwd by produceState(initialValue = EMPTY_STRING) {
        value = getJxglstuPassword() ?: EMPTY_STRING
    }
    val context = LocalContext.current

    Column() {
        DividerTextExpandedWith(text = "账号信息") {
            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    headlineContent = { Text(text = name  ?: "---") },
                    overlineContent = { Text(text ="姓名" )},
                    leadingContent = {
                        Icon(
                            painterResource(Drawables.signature),
                            contentDescription = "Localized description",
                        )
                    }
                )

                TransplantListItem(
                    headlineContent = {   Text(text = studentnumber ?: "----------")  },
                    overlineContent = { Text(text ="学号" )},
                    leadingContent = {
                        Icon(
                            painterResource(Drawables.tag),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        studentnumber?.let { ClipBoardHelper.copy(it) }
                    }
                )

                Box() {
                    FilledTonalIconButton(onClick = { show2 = !show2 }, modifier = Modifier
                        .zIndex(1f)
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = APP_HORIZONTAL_DP)) {
                        Icon(painter = painterResource(id = if(show2)Drawables.visibility else Drawables.visibility_off), contentDescription = EMPTY_STRING)
                    }
                    Column(modifier = Modifier.coverBlur(!show2)) {
                        TransplantListItem(
                            headlineContent = { Text(text = chineseid ?: "------------------")  },
                            overlineContent = { Text(text = "身份证号")},
                            leadingContent = {
                                Icon(
                                    painterResource(Drawables.person),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier.clickable {
                                chineseid?.let { ClipBoardHelper.copy(it) }
                            }
                        )
                    }
                }
            }

        }

        DividerTextExpandedWith(text = "就读信息") {
            CustomCard(
                shape = NoneRoundShape,
                color = cardNormalColor(),
                modifier = Modifier.sharedContainer(
                    ClassmatesDestination.key,
                    MaterialTheme.shapes.medium,
                    cardNormalColor()
                )
            ) {
                TransplantListItem(
                    headlineContent = { Text(text = school ?: "----") },
                    overlineContent = { Text(text = "校区")},
                    leadingContent = {
                        Icon(
                            painterResource(Drawables.near_me),
                            contentDescription = "Localized description",
                        )
                    },
                )
                TransplantListItem(
                    headlineContent = { Text(text = yuanxi ?: "--------")  },
                    overlineContent = { Text(text = "学院")},
                    leadingContent = {
                        DepartmentIcons(name = yuanxi ?: EMPTY_STRING)
                    },
                    modifier = Modifier.clickable {
                        yuanxi?.let { ClipBoardHelper.copy(it) }
                    }
                )


                TransplantListItem(
                    headlineContent = {
                        Text(text = major ?: "--------")
                    },
                    overlineContent = { Text(text = "专业")},
                    supportingContent = { majorDirection?.let { if(it != EMPTY_STRING) Text(text = "方向 $it") else null } },
                    leadingContent = {
                        Icon(
                            painterResource(Drawables.square_foot),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        major?.let { ClipBoardHelper.copy(it) }
                    }
                )

                TransplantListItem(
                    modifier = Modifier.clickable {
                        classes?.let { ClipBoardHelper.copy(it) }
                    },
                    headlineContent = {  Text(text = classes ?: "-------") },
                    overlineContent = { Text(text = "班级")},
                    leadingContent = {
                        Icon(
                            painterResource(Drawables.sensor_door),
                            contentDescription = "Localized description",
                        )
                    },
                    trailingContent = {
                        FilledTonalButton(
                            onClick = {
                                navController.push(ClassmatesDestination)
                            }
                        ) {
                            Text(ClassmatesDestination.title.asString())
                        }
                    },
                )
            }
        }

        DividerTextExpandedWith(text = "密码信息") {
            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    headlineContent = { Text(text = "密码") },
                    leadingContent = {
                        Icon(
                            painterResource(Drawables.key),
                            contentDescription = "Localized description",
                        )
                    },
                    trailingContent = {
                        FilledTonalIconButton(onClick = { show = !show }) {
                            Icon(painter = painterResource(id = if(show)Drawables.visibility else Drawables.visibility_off), contentDescription = EMPTY_STRING)
                        }
                    }
                )
                PaddingHorizontalDivider()
                Column {
                    val pwd = remember { prefs.getString("Password", EMPTY_STRING) }
                    pwd?.let {
                        TransplantListItem(
                            headlineContent = { Text(text = it,modifier = Modifier.coverBlur(!show)) },
                            overlineContent = { Text(text = "CAS统一认证密码")},
                            modifier = Modifier.clickable {
                                ClipBoardHelper.copy(it)
                            },
                            trailingContent = {
                                FilledTonalIconButton(
                                    onClick = {
                                        scope.launch {
                                            Starter.startWebUrlInner(Constant.CAS_LOGIN_URL + "cas/forget","忘记密码",null,Drawables.lock_reset)
                                        }
                                    }
                                ) {
                                    Icon(painterResource(Drawables.edit),null)
                                }
                            },
                        )
                    }
                    jxglstuPwd.let {
                        TransplantListItem(
                            headlineContent = {
                                Text(text = it,modifier = Modifier.coverBlur(!show))
                            },
                            trailingContent = {
                                FilledTonalIconButton(
                                    onClick = {
                                        navController.push(SettingsJxglstuPasswordDestination, effect = JumpTransitionEffectWallpaper())
                                    }
                                ) {
                                    Icon(painterResource(Drawables.edit),null)
                                }
                            },
                            overlineContent = { Text(text = "教务系统与合工大教务密码")},
                            modifier = Modifier.clickable {
                                ClipBoardHelper.copy(it)
                            }
                        )
                    }
                    val campus = remember { getCampusRegion() }
                    cardPsk.let {
                        TransplantListItem(
                            headlineContent = {  Text(text = it,modifier = Modifier.coverBlur(!show)) },
                            overlineContent = {
                                Text(
                                    when(campus) {
                                        CampusRegion.XUANCHENG -> "一卡通与校园网密码"
                                        CampusRegion.HEFEI -> "一卡通密码"
                                    }
                                )
                            },
                            trailingContent = {
                                FilledTonalIconButton(
                                    onClick = {
                                        navController.push(SettingsHuiXinPasswordDestination, effect = JumpTransitionEffectWallpaper())
                                    }
                                ) {
                                    Icon(painterResource(Drawables.edit),null)
                                }
                            },
                            modifier = Modifier.clickable {
                                ClipBoardHelper.copy(it)
                            }
                        )
                    }
                    when(getCampusRegion()) {
                        CampusRegion.HEFEI -> {
                            schoolNetPsk.let {
                                TransplantListItem(
                                    headlineContent = {  Text(text = it,modifier = Modifier.coverBlur(!show)) },
                                    overlineContent = { Text(text = "校园网密码")},
                                    trailingContent = {
                                        FilledTonalIconButton(
                                            onClick = {
                                                navController.push(SettingsHuiXinPasswordDestination, effect = JumpTransitionEffectWallpaper())
                                            }
                                        ) {
                                            Icon(painterResource(Drawables.edit),null)
                                        }
                                    },
                                    modifier = Modifier.clickable {
                                        ClipBoardHelper.copy(it)
                                    }
                                )
                            }
                        }
                        CampusRegion.XUANCHENG -> Unit
                    }
                }
            }
        }

        DividerTextExpandedWith(text = "学籍信息") {
            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    headlineContent = { Text(text = studyType ?: "-----")  },
                    overlineContent = { Text(text = "类型")},
                    leadingContent = {
                        Icon(
                            painterResource(Drawables.school),
                            contentDescription = "Localized description",
                        )
                    },
                )
                xueJiStatus.let {
                    TransplantListItem(
                        headlineContent = {  Text(text = it ?: "--") },
                        overlineContent = { Text(text = "学籍状态")},
                        leadingContent = {
                            Icon(
                                painterResource(
                                    if(it == null) {
                                        Drawables.help
                                    } else if(it.contains("正常")) {
                                        Drawables.check_circle
                                    } else if(it.contains("转专业")) {
                                        Drawables.compare_arrows
                                    } else if(it.contains("毕业")) {
                                        Drawables.verified
                                    } else {
                                        Drawables.help
                                    }
                                ),
                                contentDescription = "Localized description",
                            )
                        },
                    )
                }

                val percent = remember(startDate,endDate) {
                    if (startDate != null && endDate != null && startDate != EMPTY_STRING && endDate != EMPTY_STRING) {
                        DateTimeManager.getPercent(startDate, endDate)
                    } else {
                        null
                    }
                }

                TransplantListItem(
                    headlineContent = { Text(text = "${startDate ?: "YYYY-MM-DD"}\n${endDate ?: "YYYY-MM-DD"}") },
                    overlineContent = { Text(text = "学制 ${studyTime ?: "-"} 年")},
                    leadingContent = {
                        if(percent != null && percent in 0.0 ..< 1.0) {
                            CircleProgressIcon(percent)
                        } else {
                            Icon(
                                painterResource(Drawables.schedule),
                                contentDescription = "Localized description",
                            )
                        }
                    },
                    trailingContent = {
                        if(percent != null) {
                            val str = if(percent in 0.0 ..< 1.0) {
                                "已过 ${(percent*100).roundOffString(1)}%"
                            } else if(percent < 0f) {
                                "待入学"
                            } else {
                                "已毕业"
                            }
                            Text(text = str)
                        } else { null }
                    },
                    modifier = Modifier.clickable {
                        xueJiStatus?.let { ClipBoardHelper.copy(it) }
                    }
                )
                TransplantListItem(
                    headlineContent = {  Text(text = program ?: "----------------")  },
                    overlineContent = { Text(text = "培养方案")},
                    leadingContent = {
                        Icon(
                            painterResource(Drawables.conversion_path),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {}
                )
                TransplantListItem(
                    headlineContent = {  Text(text = home ?: "---省")  },
                    overlineContent = { Text(text = "来源")},
                    leadingContent = {
                        Icon(
                            painterResource(Drawables.location_on),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {}
                )
                photo.let {
                    var showPhoto by remember { mutableStateOf(false) }
                    TransplantListItem(
                        headlineContent = { Text(text = "学籍照") },
                        trailingContent = m@ {
                            if(it == null) {
                                FilledTonalButton(
                                    onClick = { },
                                ) {
                                    Text("--")
                                }
                                return@m
                            }
                            if(showPhoto) {
                                val byteArray = Base64.decode(it, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(byteArray , 0, byteArray.size)
                                val imageBitmap = bitmap.asImageBitmap()
                                Image(bitmap = imageBitmap,
                                    contentDescription = "Displayed image",
                                    modifier = Modifier
                                        .size(130.dp)
                                        .padding(10.dp))
                            } else {
                                FilledTonalButton(
                                    onClick = { showPhoto = !showPhoto },
                                ) {
                                    Text("显示")
                                }
                            }
                        },
                        leadingContent = {
                            Icon(
                                painterResource(Drawables.background_replace),
                                contentDescription = "Localized description",
                            )
                        },
                        modifier = Modifier.clickable {
                            if(it != null) {
                                showPhoto = !showPhoto
                            }
                        }
                    )
                }
            }

        }

        DividerTextExpandedWith("私人信息") {
            CustomCard(color = cardNormalColor()) {
                info.gender.let {
                    TransplantListItem(
                        headlineContent = {  Text(it ?: "-")  },
                        overlineContent = { Text("性别") },
                        leadingContent = {
                            Icon(painterResource(
                                when(it) {
                                    "男" -> Drawables.male
                                    "女" -> Drawables.female
                                    else -> Drawables.help
                                }
                            ),null)
                        },
                    )
                }
                info.politicalStatus.let {
                    TransplantListItem(
                        headlineContent = { Text(it ?: "--")  },
                        overlineContent = { Text("政治面貌") },
                        leadingContent = {
                            Icon(painterResource(Drawables.groups),null)
                        },
                    )
                }
                val mobile = info.mobile
                val phone = info.phone
                if(mobile == phone) {
                    TransplantListItem(
                        headlineContent = {  Text(mobile ?: "-----------") },
                        overlineContent = { Text("手机号") },
                        leadingContent = {
                            Icon(painterResource(Drawables.call),null)
                        },
                        modifier = Modifier.clickable {
                            mobile?.let { ClipBoardHelper.copy(it) }
                        }
                    )
                } else {
                    mobile?.let {
                        TransplantListItem(
                            headlineContent = {  Text(it) },
                            overlineContent = { Text("手机") },
                            leadingContent = {
                                Icon(painterResource(Drawables.call),null)
                            },
                            modifier = Modifier.clickable {
                                ClipBoardHelper.copy(it)
                            }
                        )
                    }
                    phone?.let {
                        TransplantListItem(
                            headlineContent = { Text(it) },
                            overlineContent = { Text("电话") },
                            leadingContent = {
                                Icon(painterResource(Drawables.call),null)
                            },
                            modifier = Modifier.clickable {
                                ClipBoardHelper.copy(it)
                            }
                        )
                    }
                }

                info.email.let {
                    TransplantListItem(
                        headlineContent = {  Text(it ?: "-------------") },
                        overlineContent = { Text("电子邮件") },
                        leadingContent = {
                            Icon(painterResource(Drawables.alternate_email),null)
                        },
                        modifier = Modifier.clickable {
                            it?.let { str -> ClipBoardHelper.copy(str) }
                        }
                    )
                }
                info.address.let {
                    TransplantListItem(
                        headlineContent = {  Text(it ?: "----------------")  },
                        overlineContent = { Text("地址") },
                        leadingContent = {
                            Icon(painterResource(Drawables.home),null)
                        },
                        modifier = Modifier.clickable {
                            it?.let { str -> ClipBoardHelper.copy(str) }
                        }
                    )
                }

                info.postalCode.let {
                    TransplantListItem(
                        headlineContent = { Text(it ?: "------") } ,
                        overlineContent = { Text("邮编") },
                        leadingContent = {
                            Icon(painterResource(Drawables.mail),null)
                        },
                        modifier = Modifier.clickable {
                            it?.let { str -> ClipBoardHelper.copy(str) }
                        }
                    )
                }
            }


        }
    }
}


