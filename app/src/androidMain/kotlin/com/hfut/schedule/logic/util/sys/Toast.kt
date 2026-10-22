package com.hfut.schedule.logic.util.sys

import com.xah.common.ui.model.text.asStringNow

import com.xah.common.ui.res.Strings

import android.content.Context
import androidx.annotation.StringRes
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.xah.common.logic.util.ToastUtil

fun showDevelopingToast(context : Context = MyApplication.context) = showToast(Strings.toast_developing,context)

//fun showToast(text: UiText,context: Context = MyApplication.context) = ToastUtil.showToast(context,text.asStringNow())

fun showToast(text: String,context: Context = MyApplication.context) = ToastUtil.showToast(text)

private fun showToast(@StringRes text: Int,context: Context = MyApplication.context) = ToastUtil.showToast(context.getString(text))

