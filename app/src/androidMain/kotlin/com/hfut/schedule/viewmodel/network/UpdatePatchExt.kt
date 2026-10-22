package com.hfut.schedule.viewmodel.network

import com.hfut.schedule.logic.network.repo.GithubRepository
import com.xah.bsdiffs.model.Patch
import com.xah.bsdiffs.util.parsePatch

/**
 * 增量更新包大小查询。
 *
 * [Patch] 来自 Android 侧的 bsdiffs 库，桌面端不做增量更新，
 * 因此这个扩展只留在 androidMain，不进共享层。
 */
suspend fun NetWorkViewModel.getGiteePatchSize(patch: Patch) =
    GithubRepository.getUpdateFileSize(parsePatch(patch), giteePatchSizeResp)
