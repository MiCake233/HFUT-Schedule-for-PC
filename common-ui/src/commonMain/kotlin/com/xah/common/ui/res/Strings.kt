// 由 tools/migrate-resources.mjs 自动生成，请勿手改。
package com.xah.common.ui.res

import androidx.compose.runtime.Composable
import com.xah.common.ui.resources.Res
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource as composeStringResource

object Strings {
    private val names: Array<String> = arrayOf(
        "about_setting_fix_description",
        "about_setting_fix_title",
        "about_settings_about_description",
        "about_settings_about_half_title",
        "about_settings_about_title",
        "about_settings_description",
        "about_settings_developer_description",
        "about_settings_developer_title",
        "about_settings_different_supported_description",
        "about_settings_different_supported_title",
        "about_settings_feedback_description",
        "about_settings_feedback_title",
        "about_settings_fix_half_title",
        "about_settings_history_update_log_title",
        "about_settings_promote_description",
        "about_settings_promote_title",
        "about_settings_tips_description",
        "about_settings_tips_title",
        "about_settings_title",
        "about_settings_toast_promote",
        "about_settings_version_info_description",
        "app_name",
        "app_settings_auto_calculate_term_description",
        "app_settings_auto_calculate_term_title",
        "app_settings_auto_fill_captcha_description",
        "app_settings_auto_fill_captcha_title",
        "app_settings_backup_and_restore_description",
        "app_settings_backup_and_restore_title",
        "app_settings_calendar_half_title",
        "app_settings_clear_cache_description",
        "app_settings_clear_cache_title",
        "app_settings_current_term_title",
        "app_settings_default_calendar_account_description",
        "app_settings_default_calendar_account_title",
        "app_settings_default_calendar_choice_community",
        "app_settings_default_calendar_choice_jxglstu",
        "app_settings_default_calendar_choice_jxglstu_2",
        "app_settings_default_calendar_choice_uni_app",
        "app_settings_default_calendar_choice_zhi_jian",
        "app_settings_default_calendar_community_description",
        "app_settings_default_calendar_jxglstu_description",
        "app_settings_default_calendar_title",
        "app_settings_default_calendar_uni_app_description",
        "app_settings_description",
        "app_settings_display_overdue_courses_on_focus_description",
        "app_settings_display_overdue_courses_on_focus_title",
        "app_settings_display_overdue_events_on_focus_description",
        "app_settings_display_overdue_events_on_focus_title",
        "app_settings_display_teachers_title",
        "app_settings_focus_card_description",
        "app_settings_focus_card_title",
        "app_settings_free_flow_of_school_net_of_xc_campus_description",
        "app_settings_free_flow_of_school_net_of_xc_campus_title",
        "app_settings_interaction_half_title",
        "app_settings_language_choice_auto",
        "app_settings_language_choice_english",
        "app_settings_language_choice_simplified_chinese",
        "app_settings_language_title",
        "app_settings_live_update_description_supported",
        "app_settings_live_update_description_unsupported",
        "app_settings_live_update_title",
        "app_settings_merge_conflict_calendar_square_description",
        "app_settings_merge_conflict_calendar_squares_title",
        "app_settings_predictive_back_gesture_description_supported",
        "app_settings_predictive_back_gesture_description_unsupported",
        "app_settings_predictive_back_gesture_title",
        "app_settings_preferences_half_title",
        "app_settings_school_net_description",
        "app_settings_school_net_title",
        "app_settings_shortcut_description",
        "app_settings_shortcut_title",
        "app_settings_start_date_of_term_description",
        "app_settings_start_date_of_term_title",
        "app_settings_storage_half_title",
        "app_settings_study_interaction_description",
        "app_settings_study_interaction_title",
        "app_settings_tips_start_date_of_term",
        "app_settings_title",
        "app_settings_toast_change_current_term_unsupported",
        "app_settings_toast_clear_cache_done",
        "app_settings_widget_data_description",
        "app_settings_widget_data_title",
        "app_settings_widget_focus_description",
        "app_settings_widget_focus_title",
        "app_settings_widget_half_title",
        "appearance_settings_always_display_bottom_bar_labels_description",
        "appearance_settings_always_display_bottom_bar_labels_title",
        "appearance_settings_bottom_bar_half_title",
        "appearance_settings_button_input_color_cancel",
        "appearance_settings_button_input_color_set",
        "appearance_settings_calendar_background_description",
        "appearance_settings_calendar_background_title",
        "appearance_settings_calendar_half_title",
        "appearance_settings_calendar_square_alpha_description",
        "appearance_settings_calendar_square_alpha_title",
        "appearance_settings_calendar_square_height_description",
        "appearance_settings_calendar_square_height_title",
        "appearance_settings_calendar_square_text_line_padding_description",
        "appearance_settings_calendar_square_text_line_padding_title",
        "appearance_settings_calendar_square_text_size_description",
        "appearance_settings_calendar_square_text_size_title",
        "appearance_settings_camera_render_real_time_description",
        "appearance_settings_camera_render_real_time_title",
        "appearance_settings_choice_bright_heavy",
        "appearance_settings_choice_bright_monochrome",
        "appearance_settings_choice_bright_normal",
        "appearance_settings_choice_bright_tinge",
        "appearance_settings_choice_display_teachers_all",
        "appearance_settings_choice_display_teachers_none",
        "appearance_settings_choice_display_teachers_only_multi",
        "appearance_settings_choice_theme",
        "appearance_settings_choice_theme_auto",
        "appearance_settings_choice_theme_dark",
        "appearance_settings_choice_theme_light",
        "appearance_settings_color_bright_title",
        "appearance_settings_color_half_title",
        "appearance_settings_default_color_half_title",
        "appearance_settings_description",
        "appearance_settings_dynamic_color_title",
        "appearance_settings_dynamic_effect_half_title",
        "appearance_settings_effect_half_title",
        "appearance_settings_enforce_web_page_dark_theme_description",
        "appearance_settings_enforce_web_page_dark_theme_title",
        "appearance_settings_layer_blur_description",
        "appearance_settings_layer_blur_title",
        "appearance_settings_motion_blur_description_supported",
        "appearance_settings_motion_blur_description_unsupported",
        "appearance_settings_motion_blur_title",
        "appearance_settings_pick_color_by_hue_band_description",
        "appearance_settings_pick_color_by_hue_band_title",
        "appearance_settings_pick_color_by_photo_title",
        "appearance_settings_pick_color_half_title",
        "appearance_settings_pure_black_background_description",
        "appearance_settings_pure_black_background_title",
        "appearance_settings_shader_description_supported",
        "appearance_settings_shader_description_unsupported",
        "appearance_settings_shader_title",
        "appearance_settings_theme_description",
        "appearance_settings_theme_half_title",
        "appearance_settings_theme_title",
        "appearance_settings_tips_input_color",
        "appearance_settings_title",
        "appearance_settings_toast_set_calendar_background",
        "appearance_settings_transition_bottom_bar_description",
        "appearance_settings_transition_bottom_bar_title",
        "appearance_settings_transition_extension_description",
        "appearance_settings_transition_extension_title",
        "appearance_settings_transition_level_description",
        "appearance_settings_transition_level_title",
        "appearance_settings_transition_screen_corner_description",
        "appearance_settings_transition_screen_corner_title",
        "appearance_settings_transition_splash_description",
        "appearance_settings_transition_splash_title",
        "card",
        "fix",
        "focus_and_calendar_title",
        "functions_center_title",
        "main",
        "navigation_label_add_event",
        "navigation_label_admission",
        "navigation_label_admission_detail",
        "navigation_label_agreement",
        "navigation_label_ai",
        "navigation_label_all_programs",
        "navigation_label_alumni",
        "navigation_label_average_grade",
        "navigation_label_bus",
        "navigation_label_chsi",
        "navigation_label_classmates",
        "navigation_label_classroom",
        "navigation_label_classroom_course_table",
        "navigation_label_classroom_detail",
        "navigation_label_community_appointment",
        "navigation_label_course_detail",
        "navigation_label_course_search",
        "navigation_label_course_search_table",
        "navigation_label_departments",
        "navigation_label_dormitory",
        "navigation_label_dormitory_electricity_bill",
        "navigation_label_drop_courses",
        "navigation_label_empty",
        "navigation_label_exam",
        "navigation_label_exam_news",
        "navigation_label_exception",
        "navigation_label_express",
        "navigation_label_fail_rate",
        "navigation_label_fee",
        "navigation_label_feedback",
        "navigation_label_functions_sort",
        "navigation_label_grade",
        "navigation_label_grade_detail",
        "navigation_label_guagua",
        "navigation_label_holiday",
        "navigation_label_home",
        "navigation_label_hot_water",
        "navigation_label_hui_xin",
        "navigation_label_ietp",
        "navigation_label_laundry",
        "navigation_label_library",
        "navigation_label_library_borrowed",
        "navigation_label_life",
        "navigation_label_live_update",
        "navigation_label_login",
        "navigation_label_news",
        "navigation_label_notification_box",
        "navigation_label_notifications",
        "navigation_label_office_hall",
        "navigation_label_person_info",
        "navigation_label_physical_fitness_test",
        "navigation_label_program",
        "navigation_label_program_competition",
        "navigation_label_program_competition_detail",
        "navigation_label_program_confirmation",
        "navigation_label_repair",
        "navigation_label_scan_qr_code",
        "navigation_label_scan_qr_code_description",
        "navigation_label_school_card",
        "navigation_label_school_card_not_login",
        "navigation_label_school_card_warn",
        "navigation_label_school_email",
        "navigation_label_school_net",
        "navigation_label_second_class",
        "navigation_label_select_courses",
        "navigation_label_select_courses_detail",
        "navigation_label_settings_about",
        "navigation_label_settings_appearance",
        "navigation_label_settings_configurations",
        "navigation_label_settings_corner",
        "navigation_label_settings_network",
        "navigation_label_shortcut",
        "navigation_label_shower",
        "navigation_label_stu",
        "navigation_label_supabase",
        "navigation_label_survey",
        "navigation_label_teacher_search",
        "navigation_label_term_courses",
        "navigation_label_term_report",
        "navigation_label_to_outer",
        "navigation_label_track",
        "navigation_label_transfer_major",
        "navigation_label_transfer_major_applied",
        "navigation_label_transfer_major_applied_detail",
        "navigation_label_transfer_major_detail",
        "navigation_label_update_successfully",
        "navigation_label_version_info",
        "navigation_label_washing",
        "navigation_label_washing_detail",
        "navigation_label_web",
        "navigation_label_web_folder",
        "navigation_label_webvpn",
        "navigation_label_work",
        "navigation_label_work_and_rest",
        "network_settings_ai_description",
        "network_settings_ai_title",
        "network_settings_auto_refresh_login_description",
        "network_settings_auto_refresh_login_title",
        "network_settings_config_half_title",
        "network_settings_description",
        "network_settings_jxglstu_password_description",
        "network_settings_jxglstu_password_title",
        "network_settings_others_half_title",
        "network_settings_refresh_login_description",
        "network_settings_refresh_login_title",
        "network_settings_request_page_size_description",
        "network_settings_request_page_size_title",
        "network_settings_school_card_password_description",
        "network_settings_school_card_password_title",
        "network_settings_title",
        "network_settings_update_data_description",
        "network_settings_update_data_title",
        "qs_label_hot_water",
        "qs_label_login_web_hefei",
        "qs_label_login_web_xuancheng",
        "qs_label_pdd",
        "qs_label_qr_code_pay",
        "qs_label_recharge",
        "qs_label_scan",
        "qs_label_taobao",
        "settings_about_update_by_github_description",
        "settings_about_update_by_github_title",
        "settings_always_display_half_title",
        "settings_half_title",
        "settings_important_notice_half_title",
        "settings_person_info_default_name",
        "settings_person_info_half_title",
        "settings_person_info_student_id_description",
        "settings_person_info_tag_avd",
        "settings_person_info_tag_debug",
        "settings_person_info_tag_harmony_next",
        "settings_person_info_tag_normal",
        "settings_person_info_tag_preview",
        "settings_person_info_tag_sign",
        "settings_person_info_tag_wsa",
        "settings_title",
        "settings_update_button_delete",
        "settings_update_button_download",
        "settings_update_button_download_file_size",
        "settings_update_button_download_manage",
        "settings_update_button_failed",
        "settings_update_button_install",
        "settings_update_description",
        "settings_update_detail_title",
        "settings_update_failed_title",
        "settings_update_half_title",
        "settings_update_merge_patch_title",
        "settings_update_patch_button__download_to_latest",
        "settings_update_patch_button_download_to_version",
        "settings_update_patch_description",
        "settings_update_patch_loading",
        "settings_update_patch_title_latest",
        "settings_update_tips_different_architectures",
        "settings_update_tips_none_patch",
        "settings_update_title_failed",
        "settings_update_title_find_new_version",
        "settings_version_info_description",
        "settings_version_info_description_preview",
        "title_activity_anonymity",
        "title_activity_base",
        "title_activity_card",
        "title_activity_fix",
        "title_activity_grade",
        "title_activity_news",
        "title_activity_schedule",
        "title_activity_shower",
        "title_activity_shower_login",
        "title_activity_supabase",
        "title_activity_uiacitivity",
        "title_activity_web_view",
        "title_activity_widget_configure",
        "title_activity_xwx",
        "toast_developing",
        "uninstall_content",
        "uninstall_title",
        "widget_course"
    )

    const val about_setting_fix_description: Int = 0
    const val about_setting_fix_title: Int = 1
    const val about_settings_about_description: Int = 2
    const val about_settings_about_half_title: Int = 3
    const val about_settings_about_title: Int = 4
    const val about_settings_description: Int = 5
    const val about_settings_developer_description: Int = 6
    const val about_settings_developer_title: Int = 7
    const val about_settings_different_supported_description: Int = 8
    const val about_settings_different_supported_title: Int = 9
    const val about_settings_feedback_description: Int = 10
    const val about_settings_feedback_title: Int = 11
    const val about_settings_fix_half_title: Int = 12
    const val about_settings_history_update_log_title: Int = 13
    const val about_settings_promote_description: Int = 14
    const val about_settings_promote_title: Int = 15
    const val about_settings_tips_description: Int = 16
    const val about_settings_tips_title: Int = 17
    const val about_settings_title: Int = 18
    const val about_settings_toast_promote: Int = 19
    const val about_settings_version_info_description: Int = 20
    const val app_name: Int = 21
    const val app_settings_auto_calculate_term_description: Int = 22
    const val app_settings_auto_calculate_term_title: Int = 23
    const val app_settings_auto_fill_captcha_description: Int = 24
    const val app_settings_auto_fill_captcha_title: Int = 25
    const val app_settings_backup_and_restore_description: Int = 26
    const val app_settings_backup_and_restore_title: Int = 27
    const val app_settings_calendar_half_title: Int = 28
    const val app_settings_clear_cache_description: Int = 29
    const val app_settings_clear_cache_title: Int = 30
    const val app_settings_current_term_title: Int = 31
    const val app_settings_default_calendar_account_description: Int = 32
    const val app_settings_default_calendar_account_title: Int = 33
    const val app_settings_default_calendar_choice_community: Int = 34
    const val app_settings_default_calendar_choice_jxglstu: Int = 35
    const val app_settings_default_calendar_choice_jxglstu_2: Int = 36
    const val app_settings_default_calendar_choice_uni_app: Int = 37
    const val app_settings_default_calendar_choice_zhi_jian: Int = 38
    const val app_settings_default_calendar_community_description: Int = 39
    const val app_settings_default_calendar_jxglstu_description: Int = 40
    const val app_settings_default_calendar_title: Int = 41
    const val app_settings_default_calendar_uni_app_description: Int = 42
    const val app_settings_description: Int = 43
    const val app_settings_display_overdue_courses_on_focus_description: Int = 44
    const val app_settings_display_overdue_courses_on_focus_title: Int = 45
    const val app_settings_display_overdue_events_on_focus_description: Int = 46
    const val app_settings_display_overdue_events_on_focus_title: Int = 47
    const val app_settings_display_teachers_title: Int = 48
    const val app_settings_focus_card_description: Int = 49
    const val app_settings_focus_card_title: Int = 50
    const val app_settings_free_flow_of_school_net_of_xc_campus_description: Int = 51
    const val app_settings_free_flow_of_school_net_of_xc_campus_title: Int = 52
    const val app_settings_interaction_half_title: Int = 53
    const val app_settings_language_choice_auto: Int = 54
    const val app_settings_language_choice_english: Int = 55
    const val app_settings_language_choice_simplified_chinese: Int = 56
    const val app_settings_language_title: Int = 57
    const val app_settings_live_update_description_supported: Int = 58
    const val app_settings_live_update_description_unsupported: Int = 59
    const val app_settings_live_update_title: Int = 60
    const val app_settings_merge_conflict_calendar_square_description: Int = 61
    const val app_settings_merge_conflict_calendar_squares_title: Int = 62
    const val app_settings_predictive_back_gesture_description_supported: Int = 63
    const val app_settings_predictive_back_gesture_description_unsupported: Int = 64
    const val app_settings_predictive_back_gesture_title: Int = 65
    const val app_settings_preferences_half_title: Int = 66
    const val app_settings_school_net_description: Int = 67
    const val app_settings_school_net_title: Int = 68
    const val app_settings_shortcut_description: Int = 69
    const val app_settings_shortcut_title: Int = 70
    const val app_settings_start_date_of_term_description: Int = 71
    const val app_settings_start_date_of_term_title: Int = 72
    const val app_settings_storage_half_title: Int = 73
    const val app_settings_study_interaction_description: Int = 74
    const val app_settings_study_interaction_title: Int = 75
    const val app_settings_tips_start_date_of_term: Int = 76
    const val app_settings_title: Int = 77
    const val app_settings_toast_change_current_term_unsupported: Int = 78
    const val app_settings_toast_clear_cache_done: Int = 79
    const val app_settings_widget_data_description: Int = 80
    const val app_settings_widget_data_title: Int = 81
    const val app_settings_widget_focus_description: Int = 82
    const val app_settings_widget_focus_title: Int = 83
    const val app_settings_widget_half_title: Int = 84
    const val appearance_settings_always_display_bottom_bar_labels_description: Int = 85
    const val appearance_settings_always_display_bottom_bar_labels_title: Int = 86
    const val appearance_settings_bottom_bar_half_title: Int = 87
    const val appearance_settings_button_input_color_cancel: Int = 88
    const val appearance_settings_button_input_color_set: Int = 89
    const val appearance_settings_calendar_background_description: Int = 90
    const val appearance_settings_calendar_background_title: Int = 91
    const val appearance_settings_calendar_half_title: Int = 92
    const val appearance_settings_calendar_square_alpha_description: Int = 93
    const val appearance_settings_calendar_square_alpha_title: Int = 94
    const val appearance_settings_calendar_square_height_description: Int = 95
    const val appearance_settings_calendar_square_height_title: Int = 96
    const val appearance_settings_calendar_square_text_line_padding_description: Int = 97
    const val appearance_settings_calendar_square_text_line_padding_title: Int = 98
    const val appearance_settings_calendar_square_text_size_description: Int = 99
    const val appearance_settings_calendar_square_text_size_title: Int = 100
    const val appearance_settings_camera_render_real_time_description: Int = 101
    const val appearance_settings_camera_render_real_time_title: Int = 102
    const val appearance_settings_choice_bright_heavy: Int = 103
    const val appearance_settings_choice_bright_monochrome: Int = 104
    const val appearance_settings_choice_bright_normal: Int = 105
    const val appearance_settings_choice_bright_tinge: Int = 106
    const val appearance_settings_choice_display_teachers_all: Int = 107
    const val appearance_settings_choice_display_teachers_none: Int = 108
    const val appearance_settings_choice_display_teachers_only_multi: Int = 109
    const val appearance_settings_choice_theme: Int = 110
    const val appearance_settings_choice_theme_auto: Int = 111
    const val appearance_settings_choice_theme_dark: Int = 112
    const val appearance_settings_choice_theme_light: Int = 113
    const val appearance_settings_color_bright_title: Int = 114
    const val appearance_settings_color_half_title: Int = 115
    const val appearance_settings_default_color_half_title: Int = 116
    const val appearance_settings_description: Int = 117
    const val appearance_settings_dynamic_color_title: Int = 118
    const val appearance_settings_dynamic_effect_half_title: Int = 119
    const val appearance_settings_effect_half_title: Int = 120
    const val appearance_settings_enforce_web_page_dark_theme_description: Int = 121
    const val appearance_settings_enforce_web_page_dark_theme_title: Int = 122
    const val appearance_settings_layer_blur_description: Int = 123
    const val appearance_settings_layer_blur_title: Int = 124
    const val appearance_settings_motion_blur_description_supported: Int = 125
    const val appearance_settings_motion_blur_description_unsupported: Int = 126
    const val appearance_settings_motion_blur_title: Int = 127
    const val appearance_settings_pick_color_by_hue_band_description: Int = 128
    const val appearance_settings_pick_color_by_hue_band_title: Int = 129
    const val appearance_settings_pick_color_by_photo_title: Int = 130
    const val appearance_settings_pick_color_half_title: Int = 131
    const val appearance_settings_pure_black_background_description: Int = 132
    const val appearance_settings_pure_black_background_title: Int = 133
    const val appearance_settings_shader_description_supported: Int = 134
    const val appearance_settings_shader_description_unsupported: Int = 135
    const val appearance_settings_shader_title: Int = 136
    const val appearance_settings_theme_description: Int = 137
    const val appearance_settings_theme_half_title: Int = 138
    const val appearance_settings_theme_title: Int = 139
    const val appearance_settings_tips_input_color: Int = 140
    const val appearance_settings_title: Int = 141
    const val appearance_settings_toast_set_calendar_background: Int = 142
    const val appearance_settings_transition_bottom_bar_description: Int = 143
    const val appearance_settings_transition_bottom_bar_title: Int = 144
    const val appearance_settings_transition_extension_description: Int = 145
    const val appearance_settings_transition_extension_title: Int = 146
    const val appearance_settings_transition_level_description: Int = 147
    const val appearance_settings_transition_level_title: Int = 148
    const val appearance_settings_transition_screen_corner_description: Int = 149
    const val appearance_settings_transition_screen_corner_title: Int = 150
    const val appearance_settings_transition_splash_description: Int = 151
    const val appearance_settings_transition_splash_title: Int = 152
    const val card: Int = 153
    const val fix: Int = 154
    const val focus_and_calendar_title: Int = 155
    const val functions_center_title: Int = 156
    const val main: Int = 157
    const val navigation_label_add_event: Int = 158
    const val navigation_label_admission: Int = 159
    const val navigation_label_admission_detail: Int = 160
    const val navigation_label_agreement: Int = 161
    const val navigation_label_ai: Int = 162
    const val navigation_label_all_programs: Int = 163
    const val navigation_label_alumni: Int = 164
    const val navigation_label_average_grade: Int = 165
    const val navigation_label_bus: Int = 166
    const val navigation_label_chsi: Int = 167
    const val navigation_label_classmates: Int = 168
    const val navigation_label_classroom: Int = 169
    const val navigation_label_classroom_course_table: Int = 170
    const val navigation_label_classroom_detail: Int = 171
    const val navigation_label_community_appointment: Int = 172
    const val navigation_label_course_detail: Int = 173
    const val navigation_label_course_search: Int = 174
    const val navigation_label_course_search_table: Int = 175
    const val navigation_label_departments: Int = 176
    const val navigation_label_dormitory: Int = 177
    const val navigation_label_dormitory_electricity_bill: Int = 178
    const val navigation_label_drop_courses: Int = 179
    const val navigation_label_empty: Int = 180
    const val navigation_label_exam: Int = 181
    const val navigation_label_exam_news: Int = 182
    const val navigation_label_exception: Int = 183
    const val navigation_label_express: Int = 184
    const val navigation_label_fail_rate: Int = 185
    const val navigation_label_fee: Int = 186
    const val navigation_label_feedback: Int = 187
    const val navigation_label_functions_sort: Int = 188
    const val navigation_label_grade: Int = 189
    const val navigation_label_grade_detail: Int = 190
    const val navigation_label_guagua: Int = 191
    const val navigation_label_holiday: Int = 192
    const val navigation_label_home: Int = 193
    const val navigation_label_hot_water: Int = 194
    const val navigation_label_hui_xin: Int = 195
    const val navigation_label_ietp: Int = 196
    const val navigation_label_laundry: Int = 197
    const val navigation_label_library: Int = 198
    const val navigation_label_library_borrowed: Int = 199
    const val navigation_label_life: Int = 200
    const val navigation_label_live_update: Int = 201
    const val navigation_label_login: Int = 202
    const val navigation_label_news: Int = 203
    const val navigation_label_notification_box: Int = 204
    const val navigation_label_notifications: Int = 205
    const val navigation_label_office_hall: Int = 206
    const val navigation_label_person_info: Int = 207
    const val navigation_label_physical_fitness_test: Int = 208
    const val navigation_label_program: Int = 209
    const val navigation_label_program_competition: Int = 210
    const val navigation_label_program_competition_detail: Int = 211
    const val navigation_label_program_confirmation: Int = 212
    const val navigation_label_repair: Int = 213
    const val navigation_label_scan_qr_code: Int = 214
    const val navigation_label_scan_qr_code_description: Int = 215
    const val navigation_label_school_card: Int = 216
    const val navigation_label_school_card_not_login: Int = 217
    const val navigation_label_school_card_warn: Int = 218
    const val navigation_label_school_email: Int = 219
    const val navigation_label_school_net: Int = 220
    const val navigation_label_second_class: Int = 221
    const val navigation_label_select_courses: Int = 222
    const val navigation_label_select_courses_detail: Int = 223
    const val navigation_label_settings_about: Int = 224
    const val navigation_label_settings_appearance: Int = 225
    const val navigation_label_settings_configurations: Int = 226
    const val navigation_label_settings_corner: Int = 227
    const val navigation_label_settings_network: Int = 228
    const val navigation_label_shortcut: Int = 229
    const val navigation_label_shower: Int = 230
    const val navigation_label_stu: Int = 231
    const val navigation_label_supabase: Int = 232
    const val navigation_label_survey: Int = 233
    const val navigation_label_teacher_search: Int = 234
    const val navigation_label_term_courses: Int = 235
    const val navigation_label_term_report: Int = 236
    const val navigation_label_to_outer: Int = 237
    const val navigation_label_track: Int = 238
    const val navigation_label_transfer_major: Int = 239
    const val navigation_label_transfer_major_applied: Int = 240
    const val navigation_label_transfer_major_applied_detail: Int = 241
    const val navigation_label_transfer_major_detail: Int = 242
    const val navigation_label_update_successfully: Int = 243
    const val navigation_label_version_info: Int = 244
    const val navigation_label_washing: Int = 245
    const val navigation_label_washing_detail: Int = 246
    const val navigation_label_web: Int = 247
    const val navigation_label_web_folder: Int = 248
    const val navigation_label_webvpn: Int = 249
    const val navigation_label_work: Int = 250
    const val navigation_label_work_and_rest: Int = 251
    const val network_settings_ai_description: Int = 252
    const val network_settings_ai_title: Int = 253
    const val network_settings_auto_refresh_login_description: Int = 254
    const val network_settings_auto_refresh_login_title: Int = 255
    const val network_settings_config_half_title: Int = 256
    const val network_settings_description: Int = 257
    const val network_settings_jxglstu_password_description: Int = 258
    const val network_settings_jxglstu_password_title: Int = 259
    const val network_settings_others_half_title: Int = 260
    const val network_settings_refresh_login_description: Int = 261
    const val network_settings_refresh_login_title: Int = 262
    const val network_settings_request_page_size_description: Int = 263
    const val network_settings_request_page_size_title: Int = 264
    const val network_settings_school_card_password_description: Int = 265
    const val network_settings_school_card_password_title: Int = 266
    const val network_settings_title: Int = 267
    const val network_settings_update_data_description: Int = 268
    const val network_settings_update_data_title: Int = 269
    const val qs_label_hot_water: Int = 270
    const val qs_label_login_web_hefei: Int = 271
    const val qs_label_login_web_xuancheng: Int = 272
    const val qs_label_pdd: Int = 273
    const val qs_label_qr_code_pay: Int = 274
    const val qs_label_recharge: Int = 275
    const val qs_label_scan: Int = 276
    const val qs_label_taobao: Int = 277
    const val settings_about_update_by_github_description: Int = 278
    const val settings_about_update_by_github_title: Int = 279
    const val settings_always_display_half_title: Int = 280
    const val settings_half_title: Int = 281
    const val settings_important_notice_half_title: Int = 282
    const val settings_person_info_default_name: Int = 283
    const val settings_person_info_half_title: Int = 284
    const val settings_person_info_student_id_description: Int = 285
    const val settings_person_info_tag_avd: Int = 286
    const val settings_person_info_tag_debug: Int = 287
    const val settings_person_info_tag_harmony_next: Int = 288
    const val settings_person_info_tag_normal: Int = 289
    const val settings_person_info_tag_preview: Int = 290
    const val settings_person_info_tag_sign: Int = 291
    const val settings_person_info_tag_wsa: Int = 292
    const val settings_title: Int = 293
    const val settings_update_button_delete: Int = 294
    const val settings_update_button_download: Int = 295
    const val settings_update_button_download_file_size: Int = 296
    const val settings_update_button_download_manage: Int = 297
    const val settings_update_button_failed: Int = 298
    const val settings_update_button_install: Int = 299
    const val settings_update_description: Int = 300
    const val settings_update_detail_title: Int = 301
    const val settings_update_failed_title: Int = 302
    const val settings_update_half_title: Int = 303
    const val settings_update_merge_patch_title: Int = 304
    const val settings_update_patch_button__download_to_latest: Int = 305
    const val settings_update_patch_button_download_to_version: Int = 306
    const val settings_update_patch_description: Int = 307
    const val settings_update_patch_loading: Int = 308
    const val settings_update_patch_title_latest: Int = 309
    const val settings_update_tips_different_architectures: Int = 310
    const val settings_update_tips_none_patch: Int = 311
    const val settings_update_title_failed: Int = 312
    const val settings_update_title_find_new_version: Int = 313
    const val settings_version_info_description: Int = 314
    const val settings_version_info_description_preview: Int = 315
    const val title_activity_anonymity: Int = 316
    const val title_activity_base: Int = 317
    const val title_activity_card: Int = 318
    const val title_activity_fix: Int = 319
    const val title_activity_grade: Int = 320
    const val title_activity_news: Int = 321
    const val title_activity_schedule: Int = 322
    const val title_activity_shower: Int = 323
    const val title_activity_shower_login: Int = 324
    const val title_activity_supabase: Int = 325
    const val title_activity_uiacitivity: Int = 326
    const val title_activity_web_view: Int = 327
    const val title_activity_widget_configure: Int = 328
    const val title_activity_xwx: Int = 329
    const val toast_developing: Int = 330
    const val uninstall_content: Int = 331
    const val uninstall_title: Int = 332
    const val widget_course: Int = 333

    val size: Int get() = names.size

    fun resourceOf(id: Int): StringResource =
        Res.allStringResources.getValue(names[id])
}

@Composable
fun stringResource(id: Int, vararg args: Any): String =
    composeStringResource(Strings.resourceOf(id), *args)

fun stringResourceResource(id: Int): StringResource = Strings.resourceOf(id)
