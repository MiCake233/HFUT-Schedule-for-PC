package com.hfut.schedule.logic.platform

import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.other.AppVersion.getSignatureInfo

actual fun platformSignature(): SignatureDisplay? = runCatching {
    val info = MyApplication.context.getSignatureInfo() ?: return@runCatching null
    SignatureDisplay(
        issuer = info.issuer,
        subject = info.subject,
        validFrom = info.validFrom,
        validUntil = info.validUntil
    )
}.getOrNull()
