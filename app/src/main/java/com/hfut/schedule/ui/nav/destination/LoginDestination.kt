package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.login.LoginScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.res

object LoginDestination : NavDestination() {
    override val key: String = "login"
    override val title: UiText = res(Strings.navigation_label_login)
    override val icon = Drawables.login

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val loginVm = LocalNavDependencies.current.get<LoginViewModel>()
        LoginScreen(loginVm,vm)
    }
}