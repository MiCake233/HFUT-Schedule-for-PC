package com.hfut.schedule.logic.platform

/** 安装包签名信息的展示模型（桌面端没有 APK 签名，返回 null） */
data class SignatureDisplay(
    val issuer: String,
    val subject: String,
    val validFrom: String,
    val validUntil: String
)

expect fun platformSignature(): SignatureDisplay?
