package com.hfut.schedule.logic.model.supabase

import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getSchoolEmail

data class SupabaseLoginWithPwdRequest(
    val email : String? = getSchoolEmail(),
    val password : String
)