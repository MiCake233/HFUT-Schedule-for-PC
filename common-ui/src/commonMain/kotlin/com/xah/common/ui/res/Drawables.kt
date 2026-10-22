// 由 tools/migrate-resources.mjs 自动生成，请勿手改。
// 目的：让原本持有 Android drawable 资源 id(Int) 的代码（如 icon: Int）无痛迁移到
// Compose Resources —— Int 只作为稳定索引，真实资源由 Drawables.resourceOf(id) 查表。
package com.xah.common.ui.res

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.xah.common.ui.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource as composePainterResource

object Drawables {
    private val names: Array<String> = arrayOf(
        "account_balance_wallet",
        "account_tree",
        "adb",
        "add",
        "add_2",
        "add_card",
        "add_circle",
        "add_link",
        "ads_click",
        "air",
        "airline_stops",
        "alt_route",
        "alternate_email",
        "animation",
        "apk_document",
        "apk_install",
        "approval",
        "arrow_back",
        "arrow_downward",
        "arrow_forward",
        "arrow_range",
        "arrow_split",
        "arrow_upward",
        "article",
        "attach_file",
        "azm",
        "background_replace",
        "backspace",
        "barcode",
        "bathtub",
        "bathtub_filled",
        "beach_access",
        "bed",
        "block",
        "body_fat",
        "book_5",
        "book_5_filled",
        "bookmark",
        "build",
        "build_filled",
        "cached",
        "cake",
        "calendar",
        "calendar_add_on",
        "calendar_month_filled",
        "call",
        "category",
        "category_search",
        "category_search_filled",
        "celebration",
        "center_focus_strong",
        "check",
        "check_circle",
        "check_widget",
        "circle",
        "circle_filled",
        "close",
        "cloud",
        "cloud_download",
        "cloud_filled",
        "cloud_upload",
        "code",
        "cognition",
        "collapse_content",
        "colorize",
        "commit",
        "compare_arrows",
        "construction",
        "content_paste",
        "contrast",
        "conversion_path",
        "cookie",
        "cool_to_dry",
        "copy_all",
        "corporate_fare",
        "credit_card",
        "credit_card_filled",
        "crossword",
        "currency_pound",
        "dark_mode",
        "data_object",
        "database",
        "database_filled",
        "deblur",
        "delete",
        "deployed_code",
        "deployed_code_filled",
        "deployed_code_update",
        "deployed_code_update_filled",
        "description",
        "desktop_windows",
        "directions_alt",
        "directions_alt_filled",
        "directions_bus",
        "directions_car",
        "directions_run",
        "do_not_disturb_on",
        "domain",
        "download",
        "draw",
        "eco",
        "edit",
        "emergency",
        "empty_dashboard",
        "error",
        "event_busy",
        "event_repeat",
        "event_upcoming",
        "expand_circle_right",
        "expand_content",
        "explore",
        "exposure_plus_1",
        "exposure_plus_1_widget",
        "family_star",
        "female",
        "fiber_new",
        "filter_alt",
        "filter_alt_filled",
        "filter_vintage",
        "flag",
        "flash_on",
        "folder_managed",
        "format_paint",
        "forum",
        "forward",
        "function",
        "genetics",
        "github",
        "globe_book",
        "group",
        "group_search",
        "groups",
        "handshake",
        "height",
        "help",
        "hfut_badge",
        "hfut_badge_white",
        "hfut_building",
        "history",
        "home",
        "horizontal_split",
        "humidity_high",
        "humidity_low",
        "humidity_mid",
        "hvac_max_defrost",
        "ic_360",
        "ic_launcher",
        "ic_launcher_background",
        "image",
        "image_search",
        "info",
        "ink_pen",
        "invert_colors",
        "ios_share",
        "joystick",
        "key",
        "keyboard_arrow_down",
        "keyboard_arrow_left",
        "keyboard_arrow_right",
        "keyboard_command_key",
        "kid_star",
        "kid_star_filled",
        "label",
        "lasso_select",
        "laundry",
        "leaderboard",
        "leaderboard_filled",
        "light_mode",
        "lightbulb",
        "lightbulb_filled",
        "line_end_circle",
        "link",
        "link_2",
        "link_off",
        "local_laundry_service",
        "local_library",
        "location_on",
        "lock",
        "lock_reset",
        "login",
        "logo_dev",
        "logout",
        "mail",
        "male",
        "manufacturing",
        "massage",
        "meeting_room",
        "meeting_room_filled",
        "mode_heat",
        "monitor_heart",
        "monochrome_photos",
        "mop",
        "more_vert",
        "multiple_stop",
        "near_me",
        "near_me_filled",
        "net",
        "newsstand",
        "notifications",
        "notifications_active",
        "notifications_unread",
        "orbit",
        "package_2",
        "paid",
        "palette",
        "partner_exchange",
        "password",
        "pause",
        "percent",
        "person",
        "person_add",
        "person_book",
        "person_check",
        "person_filled",
        "person_play",
        "pie_chart",
        "planner_review",
        "play_arrow",
        "precision_manufacturing",
        "print",
        "progress_activity",
        "progress_activity_widget",
        "publics",
        "qr_code_2",
        "qr_code_scanner",
        "qweather100",
        "qweather1001",
        "qweather1002",
        "qweather1003",
        "qweather1004",
        "qweather1005",
        "qweather1006",
        "qweather1007",
        "qweather1008",
        "qweather1009",
        "qweather101",
        "qweather1010",
        "qweather1011",
        "qweather1012",
        "qweather1013",
        "qweather1014",
        "qweather1015",
        "qweather1016",
        "qweather1017",
        "qweather1018",
        "qweather1019",
        "qweather102",
        "qweather1020",
        "qweather1021",
        "qweather1022",
        "qweather1023",
        "qweather1024",
        "qweather1025",
        "qweather1026",
        "qweather1027",
        "qweather1028",
        "qweather1029",
        "qweather103",
        "qweather1030",
        "qweather1031",
        "qweather1032",
        "qweather1033",
        "qweather1034",
        "qweather1035",
        "qweather1036",
        "qweather1037",
        "qweather1038",
        "qweather1039",
        "qweather104",
        "qweather1040",
        "qweather1041",
        "qweather1042",
        "qweather1043",
        "qweather1044",
        "qweather1045",
        "qweather1046",
        "qweather1047",
        "qweather1048",
        "qweather1049",
        "qweather1050",
        "qweather1051",
        "qweather1052",
        "qweather1053",
        "qweather1054",
        "qweather1055",
        "qweather1056",
        "qweather1057",
        "qweather1058",
        "qweather1059",
        "qweather1060",
        "qweather1061",
        "qweather1062",
        "qweather1063",
        "qweather1064",
        "qweather1065",
        "qweather1066",
        "qweather1067",
        "qweather1068",
        "qweather1069",
        "qweather1071",
        "qweather1072",
        "qweather1073",
        "qweather1074",
        "qweather1075",
        "qweather1076",
        "qweather1077",
        "qweather1078",
        "qweather1079",
        "qweather1080",
        "qweather1081",
        "qweather1082",
        "qweather1084",
        "qweather1085",
        "qweather1086",
        "qweather1087",
        "qweather1088",
        "qweather1089",
        "qweather1201",
        "qweather1202",
        "qweather1203",
        "qweather1204",
        "qweather1205",
        "qweather1206",
        "qweather1207",
        "qweather1208",
        "qweather1209",
        "qweather1210",
        "qweather1211",
        "qweather1212",
        "qweather1213",
        "qweather1214",
        "qweather1215",
        "qweather1216",
        "qweather1217",
        "qweather1218",
        "qweather1219",
        "qweather1221",
        "qweather1241",
        "qweather1242",
        "qweather1243",
        "qweather1244",
        "qweather1245",
        "qweather1246",
        "qweather1247",
        "qweather1248",
        "qweather1249",
        "qweather1250",
        "qweather1251",
        "qweather1271",
        "qweather1272",
        "qweather1273",
        "qweather1274",
        "qweather150",
        "qweather151",
        "qweather152",
        "qweather153",
        "qweather1601",
        "qweather1602",
        "qweather1603",
        "qweather1604",
        "qweather1605",
        "qweather1606",
        "qweather1607",
        "qweather1608",
        "qweather1609",
        "qweather1610",
        "qweather1701",
        "qweather1702",
        "qweather1703",
        "qweather1801",
        "qweather1802",
        "qweather1803",
        "qweather1804",
        "qweather1805",
        "qweather2001",
        "qweather2002",
        "qweather2003",
        "qweather2004",
        "qweather2005",
        "qweather2006",
        "qweather2007",
        "qweather2029",
        "qweather2030",
        "qweather2031",
        "qweather2032",
        "qweather2033",
        "qweather2050",
        "qweather2051",
        "qweather2052",
        "qweather2053",
        "qweather2054",
        "qweather2070",
        "qweather2071",
        "qweather2072",
        "qweather2073",
        "qweather2074",
        "qweather2075",
        "qweather2076",
        "qweather2077",
        "qweather2078",
        "qweather2079",
        "qweather2080",
        "qweather2081",
        "qweather2082",
        "qweather2083",
        "qweather2084",
        "qweather2085",
        "qweather2100",
        "qweather2101",
        "qweather2102",
        "qweather2103",
        "qweather2104",
        "qweather2105",
        "qweather2106",
        "qweather2107",
        "qweather2108",
        "qweather2109",
        "qweather2111",
        "qweather2120",
        "qweather2121",
        "qweather2122",
        "qweather2123",
        "qweather2124",
        "qweather2125",
        "qweather2126",
        "qweather2127",
        "qweather2128",
        "qweather2129",
        "qweather2130",
        "qweather2131",
        "qweather2132",
        "qweather2133",
        "qweather2134",
        "qweather2135",
        "qweather2150",
        "qweather2151",
        "qweather2152",
        "qweather2153",
        "qweather2154",
        "qweather2155",
        "qweather2156",
        "qweather2157",
        "qweather2158",
        "qweather2159",
        "qweather2160",
        "qweather2161",
        "qweather2162",
        "qweather2163",
        "qweather2164",
        "qweather2165",
        "qweather2166",
        "qweather2190",
        "qweather2191",
        "qweather2192",
        "qweather2193",
        "qweather2200",
        "qweather2201",
        "qweather2202",
        "qweather2203",
        "qweather2204",
        "qweather2205",
        "qweather2207",
        "qweather2208",
        "qweather2209",
        "qweather2210",
        "qweather2211",
        "qweather2212",
        "qweather2213",
        "qweather2214",
        "qweather2215",
        "qweather2216",
        "qweather2217",
        "qweather2218",
        "qweather2300",
        "qweather2301",
        "qweather2302",
        "qweather2303",
        "qweather2304",
        "qweather2305",
        "qweather2306",
        "qweather2307",
        "qweather2308",
        "qweather2309",
        "qweather2311",
        "qweather2312",
        "qweather2313",
        "qweather2314",
        "qweather2315",
        "qweather2316",
        "qweather2317",
        "qweather2318",
        "qweather2319",
        "qweather2320",
        "qweather2321",
        "qweather2322",
        "qweather2323",
        "qweather2324",
        "qweather2325",
        "qweather2326",
        "qweather2327",
        "qweather2328",
        "qweather2330",
        "qweather2331",
        "qweather2332",
        "qweather2333",
        "qweather2341",
        "qweather2343",
        "qweather2345",
        "qweather2346",
        "qweather2348",
        "qweather2349",
        "qweather2350",
        "qweather2351",
        "qweather2352",
        "qweather2353",
        "qweather2354",
        "qweather2355",
        "qweather2356",
        "qweather2357",
        "qweather2358",
        "qweather2359",
        "qweather2360",
        "qweather2361",
        "qweather2362",
        "qweather2363",
        "qweather2364",
        "qweather2365",
        "qweather2366",
        "qweather2367",
        "qweather2368",
        "qweather2369",
        "qweather2370",
        "qweather2371",
        "qweather2372",
        "qweather2373",
        "qweather2374",
        "qweather2375",
        "qweather2376",
        "qweather2377",
        "qweather2378",
        "qweather2379",
        "qweather2380",
        "qweather2381",
        "qweather2382",
        "qweather2383",
        "qweather2384",
        "qweather2385",
        "qweather2386",
        "qweather2387",
        "qweather2388",
        "qweather2389",
        "qweather2390",
        "qweather2391",
        "qweather2392",
        "qweather2393",
        "qweather2394",
        "qweather2395",
        "qweather2396",
        "qweather2397",
        "qweather2398",
        "qweather2399",
        "qweather2400",
        "qweather2409",
        "qweather2411",
        "qweather2412",
        "qweather2413",
        "qweather2414",
        "qweather2415",
        "qweather2416",
        "qweather2417",
        "qweather2418",
        "qweather2419",
        "qweather2420",
        "qweather2421",
        "qweather2422",
        "qweather2423",
        "qweather2424",
        "qweather2425",
        "qweather2426",
        "qweather300",
        "qweather301",
        "qweather302",
        "qweather303",
        "qweather304",
        "qweather305",
        "qweather306",
        "qweather307",
        "qweather308",
        "qweather309",
        "qweather310",
        "qweather311",
        "qweather312",
        "qweather313",
        "qweather314",
        "qweather315",
        "qweather316",
        "qweather317",
        "qweather318",
        "qweather350",
        "qweather351",
        "qweather399",
        "qweather400",
        "qweather401",
        "qweather402",
        "qweather403",
        "qweather404",
        "qweather405",
        "qweather406",
        "qweather407",
        "qweather408",
        "qweather409",
        "qweather410",
        "qweather456",
        "qweather457",
        "qweather499",
        "qweather500",
        "qweather501",
        "qweather502",
        "qweather503",
        "qweather504",
        "qweather507",
        "qweather508",
        "qweather509",
        "qweather510",
        "qweather511",
        "qweather512",
        "qweather513",
        "qweather514",
        "qweather515",
        "qweather800",
        "qweather801",
        "qweather802",
        "qweather803",
        "qweather804",
        "qweather805",
        "qweather806",
        "qweather807",
        "qweather900",
        "qweather901",
        "qweather999",
        "qweather9998",
        "qweather9999",
        "radio_button_partial",
        "receipt_long",
        "receipt_long_filled",
        "refresh",
        "remove",
        "reorder",
        "reset_iso",
        "resize",
        "responsive_layout",
        "restaurant",
        "rotate_auto",
        "rotate_right",
        "rounded_corner",
        "routine",
        "save",
        "schedule",
        "schedule_widget",
        "school",
        "school_filled",
        "science",
        "sdk",
        "search",
        "search_activity",
        "search_filled",
        "sell",
        "send_money",
        "sensor_door",
        "sentiment_dissatisfied",
        "sentiment_sad",
        "sentiment_very_satisfied",
        "settings",
        "settings_backup_restore",
        "shapes",
        "shower",
        "signature",
        "slow_motion_video",
        "speed",
        "speed_2",
        "speed_3",
        "speed_4",
        "splash_icon",
        "splash_icon_dark",
        "sports_volleyball",
        "square_foot",
        "stairs",
        "star",
        "star_filled",
        "star_half",
        "start",
        "stat_minus_2",
        "steps",
        "storefront",
        "stream",
        "stylus_laser_pointer",
        "support",
        "swap_vert",
        "swipe_left",
        "syringe",
        "tab_inactive",
        "table_restaurant",
        "tactic",
        "tag",
        "target",
        "temp_preferences_eco",
        "texture",
        "thumb_down",
        "thumb_up",
        "timer",
        "toll",
        "translate",
        "tune",
        "undo",
        "unfold_less",
        "unfold_more",
        "upcoming",
        "update",
        "verified",
        "visibility",
        "visibility_off",
        "voice_selection",
        "volunteer_activism",
        "vpn_key",
        "wallpaper",
        "wand_stars",
        "warning",
        "water_drop",
        "water_voc",
        "widgets",
        "widgets_filled",
        "wifi",
        "wifi_password",
        "work"
    )

    const val account_balance_wallet: Int = 0
    const val account_tree: Int = 1
    const val adb: Int = 2
    const val add: Int = 3
    const val add_2: Int = 4
    const val add_card: Int = 5
    const val add_circle: Int = 6
    const val add_link: Int = 7
    const val ads_click: Int = 8
    const val air: Int = 9
    const val airline_stops: Int = 10
    const val alt_route: Int = 11
    const val alternate_email: Int = 12
    const val animation: Int = 13
    const val apk_document: Int = 14
    const val apk_install: Int = 15
    const val approval: Int = 16
    const val arrow_back: Int = 17
    const val arrow_downward: Int = 18
    const val arrow_forward: Int = 19
    const val arrow_range: Int = 20
    const val arrow_split: Int = 21
    const val arrow_upward: Int = 22
    const val article: Int = 23
    const val attach_file: Int = 24
    const val azm: Int = 25
    const val background_replace: Int = 26
    const val backspace: Int = 27
    const val barcode: Int = 28
    const val bathtub: Int = 29
    const val bathtub_filled: Int = 30
    const val beach_access: Int = 31
    const val bed: Int = 32
    const val block: Int = 33
    const val body_fat: Int = 34
    const val book_5: Int = 35
    const val book_5_filled: Int = 36
    const val bookmark: Int = 37
    const val build: Int = 38
    const val build_filled: Int = 39
    const val cached: Int = 40
    const val cake: Int = 41
    const val calendar: Int = 42
    const val calendar_add_on: Int = 43
    const val calendar_month_filled: Int = 44
    const val call: Int = 45
    const val category: Int = 46
    const val category_search: Int = 47
    const val category_search_filled: Int = 48
    const val celebration: Int = 49
    const val center_focus_strong: Int = 50
    const val check: Int = 51
    const val check_circle: Int = 52
    const val check_widget: Int = 53
    const val circle: Int = 54
    const val circle_filled: Int = 55
    const val close: Int = 56
    const val cloud: Int = 57
    const val cloud_download: Int = 58
    const val cloud_filled: Int = 59
    const val cloud_upload: Int = 60
    const val code: Int = 61
    const val cognition: Int = 62
    const val collapse_content: Int = 63
    const val colorize: Int = 64
    const val commit: Int = 65
    const val compare_arrows: Int = 66
    const val construction: Int = 67
    const val content_paste: Int = 68
    const val contrast: Int = 69
    const val conversion_path: Int = 70
    const val cookie: Int = 71
    const val cool_to_dry: Int = 72
    const val copy_all: Int = 73
    const val corporate_fare: Int = 74
    const val credit_card: Int = 75
    const val credit_card_filled: Int = 76
    const val crossword: Int = 77
    const val currency_pound: Int = 78
    const val dark_mode: Int = 79
    const val data_object: Int = 80
    const val database: Int = 81
    const val database_filled: Int = 82
    const val deblur: Int = 83
    const val delete: Int = 84
    const val deployed_code: Int = 85
    const val deployed_code_filled: Int = 86
    const val deployed_code_update: Int = 87
    const val deployed_code_update_filled: Int = 88
    const val description: Int = 89
    const val desktop_windows: Int = 90
    const val directions_alt: Int = 91
    const val directions_alt_filled: Int = 92
    const val directions_bus: Int = 93
    const val directions_car: Int = 94
    const val directions_run: Int = 95
    const val do_not_disturb_on: Int = 96
    const val domain: Int = 97
    const val download: Int = 98
    const val draw: Int = 99
    const val eco: Int = 100
    const val edit: Int = 101
    const val emergency: Int = 102
    const val empty_dashboard: Int = 103
    const val error: Int = 104
    const val event_busy: Int = 105
    const val event_repeat: Int = 106
    const val event_upcoming: Int = 107
    const val expand_circle_right: Int = 108
    const val expand_content: Int = 109
    const val explore: Int = 110
    const val exposure_plus_1: Int = 111
    const val exposure_plus_1_widget: Int = 112
    const val family_star: Int = 113
    const val female: Int = 114
    const val fiber_new: Int = 115
    const val filter_alt: Int = 116
    const val filter_alt_filled: Int = 117
    const val filter_vintage: Int = 118
    const val flag: Int = 119
    const val flash_on: Int = 120
    const val folder_managed: Int = 121
    const val format_paint: Int = 122
    const val forum: Int = 123
    const val forward: Int = 124
    const val function: Int = 125
    const val genetics: Int = 126
    const val github: Int = 127
    const val globe_book: Int = 128
    const val group: Int = 129
    const val group_search: Int = 130
    const val groups: Int = 131
    const val handshake: Int = 132
    const val height: Int = 133
    const val help: Int = 134
    const val hfut_badge: Int = 135
    const val hfut_badge_white: Int = 136
    const val hfut_building: Int = 137
    const val history: Int = 138
    const val home: Int = 139
    const val horizontal_split: Int = 140
    const val humidity_high: Int = 141
    const val humidity_low: Int = 142
    const val humidity_mid: Int = 143
    const val hvac_max_defrost: Int = 144
    const val ic_360: Int = 145
    const val ic_launcher: Int = 146
    const val ic_launcher_background: Int = 147
    const val image: Int = 148
    const val image_search: Int = 149
    const val info: Int = 150
    const val ink_pen: Int = 151
    const val invert_colors: Int = 152
    const val ios_share: Int = 153
    const val joystick: Int = 154
    const val key: Int = 155
    const val keyboard_arrow_down: Int = 156
    const val keyboard_arrow_left: Int = 157
    const val keyboard_arrow_right: Int = 158
    const val keyboard_command_key: Int = 159
    const val kid_star: Int = 160
    const val kid_star_filled: Int = 161
    const val label: Int = 162
    const val lasso_select: Int = 163
    const val laundry: Int = 164
    const val leaderboard: Int = 165
    const val leaderboard_filled: Int = 166
    const val light_mode: Int = 167
    const val lightbulb: Int = 168
    const val lightbulb_filled: Int = 169
    const val line_end_circle: Int = 170
    const val link: Int = 171
    const val link_2: Int = 172
    const val link_off: Int = 173
    const val local_laundry_service: Int = 174
    const val local_library: Int = 175
    const val location_on: Int = 176
    const val lock: Int = 177
    const val lock_reset: Int = 178
    const val login: Int = 179
    const val logo_dev: Int = 180
    const val logout: Int = 181
    const val mail: Int = 182
    const val male: Int = 183
    const val manufacturing: Int = 184
    const val massage: Int = 185
    const val meeting_room: Int = 186
    const val meeting_room_filled: Int = 187
    const val mode_heat: Int = 188
    const val monitor_heart: Int = 189
    const val monochrome_photos: Int = 190
    const val mop: Int = 191
    const val more_vert: Int = 192
    const val multiple_stop: Int = 193
    const val near_me: Int = 194
    const val near_me_filled: Int = 195
    const val net: Int = 196
    const val newsstand: Int = 197
    const val notifications: Int = 198
    const val notifications_active: Int = 199
    const val notifications_unread: Int = 200
    const val orbit: Int = 201
    const val package_2: Int = 202
    const val paid: Int = 203
    const val palette: Int = 204
    const val partner_exchange: Int = 205
    const val password: Int = 206
    const val pause: Int = 207
    const val percent: Int = 208
    const val person: Int = 209
    const val person_add: Int = 210
    const val person_book: Int = 211
    const val person_check: Int = 212
    const val person_filled: Int = 213
    const val person_play: Int = 214
    const val pie_chart: Int = 215
    const val planner_review: Int = 216
    const val play_arrow: Int = 217
    const val precision_manufacturing: Int = 218
    const val print: Int = 219
    const val progress_activity: Int = 220
    const val progress_activity_widget: Int = 221
    const val publics: Int = 222
    const val qr_code_2: Int = 223
    const val qr_code_scanner: Int = 224
    const val qweather100: Int = 225
    const val qweather1001: Int = 226
    const val qweather1002: Int = 227
    const val qweather1003: Int = 228
    const val qweather1004: Int = 229
    const val qweather1005: Int = 230
    const val qweather1006: Int = 231
    const val qweather1007: Int = 232
    const val qweather1008: Int = 233
    const val qweather1009: Int = 234
    const val qweather101: Int = 235
    const val qweather1010: Int = 236
    const val qweather1011: Int = 237
    const val qweather1012: Int = 238
    const val qweather1013: Int = 239
    const val qweather1014: Int = 240
    const val qweather1015: Int = 241
    const val qweather1016: Int = 242
    const val qweather1017: Int = 243
    const val qweather1018: Int = 244
    const val qweather1019: Int = 245
    const val qweather102: Int = 246
    const val qweather1020: Int = 247
    const val qweather1021: Int = 248
    const val qweather1022: Int = 249
    const val qweather1023: Int = 250
    const val qweather1024: Int = 251
    const val qweather1025: Int = 252
    const val qweather1026: Int = 253
    const val qweather1027: Int = 254
    const val qweather1028: Int = 255
    const val qweather1029: Int = 256
    const val qweather103: Int = 257
    const val qweather1030: Int = 258
    const val qweather1031: Int = 259
    const val qweather1032: Int = 260
    const val qweather1033: Int = 261
    const val qweather1034: Int = 262
    const val qweather1035: Int = 263
    const val qweather1036: Int = 264
    const val qweather1037: Int = 265
    const val qweather1038: Int = 266
    const val qweather1039: Int = 267
    const val qweather104: Int = 268
    const val qweather1040: Int = 269
    const val qweather1041: Int = 270
    const val qweather1042: Int = 271
    const val qweather1043: Int = 272
    const val qweather1044: Int = 273
    const val qweather1045: Int = 274
    const val qweather1046: Int = 275
    const val qweather1047: Int = 276
    const val qweather1048: Int = 277
    const val qweather1049: Int = 278
    const val qweather1050: Int = 279
    const val qweather1051: Int = 280
    const val qweather1052: Int = 281
    const val qweather1053: Int = 282
    const val qweather1054: Int = 283
    const val qweather1055: Int = 284
    const val qweather1056: Int = 285
    const val qweather1057: Int = 286
    const val qweather1058: Int = 287
    const val qweather1059: Int = 288
    const val qweather1060: Int = 289
    const val qweather1061: Int = 290
    const val qweather1062: Int = 291
    const val qweather1063: Int = 292
    const val qweather1064: Int = 293
    const val qweather1065: Int = 294
    const val qweather1066: Int = 295
    const val qweather1067: Int = 296
    const val qweather1068: Int = 297
    const val qweather1069: Int = 298
    const val qweather1071: Int = 299
    const val qweather1072: Int = 300
    const val qweather1073: Int = 301
    const val qweather1074: Int = 302
    const val qweather1075: Int = 303
    const val qweather1076: Int = 304
    const val qweather1077: Int = 305
    const val qweather1078: Int = 306
    const val qweather1079: Int = 307
    const val qweather1080: Int = 308
    const val qweather1081: Int = 309
    const val qweather1082: Int = 310
    const val qweather1084: Int = 311
    const val qweather1085: Int = 312
    const val qweather1086: Int = 313
    const val qweather1087: Int = 314
    const val qweather1088: Int = 315
    const val qweather1089: Int = 316
    const val qweather1201: Int = 317
    const val qweather1202: Int = 318
    const val qweather1203: Int = 319
    const val qweather1204: Int = 320
    const val qweather1205: Int = 321
    const val qweather1206: Int = 322
    const val qweather1207: Int = 323
    const val qweather1208: Int = 324
    const val qweather1209: Int = 325
    const val qweather1210: Int = 326
    const val qweather1211: Int = 327
    const val qweather1212: Int = 328
    const val qweather1213: Int = 329
    const val qweather1214: Int = 330
    const val qweather1215: Int = 331
    const val qweather1216: Int = 332
    const val qweather1217: Int = 333
    const val qweather1218: Int = 334
    const val qweather1219: Int = 335
    const val qweather1221: Int = 336
    const val qweather1241: Int = 337
    const val qweather1242: Int = 338
    const val qweather1243: Int = 339
    const val qweather1244: Int = 340
    const val qweather1245: Int = 341
    const val qweather1246: Int = 342
    const val qweather1247: Int = 343
    const val qweather1248: Int = 344
    const val qweather1249: Int = 345
    const val qweather1250: Int = 346
    const val qweather1251: Int = 347
    const val qweather1271: Int = 348
    const val qweather1272: Int = 349
    const val qweather1273: Int = 350
    const val qweather1274: Int = 351
    const val qweather150: Int = 352
    const val qweather151: Int = 353
    const val qweather152: Int = 354
    const val qweather153: Int = 355
    const val qweather1601: Int = 356
    const val qweather1602: Int = 357
    const val qweather1603: Int = 358
    const val qweather1604: Int = 359
    const val qweather1605: Int = 360
    const val qweather1606: Int = 361
    const val qweather1607: Int = 362
    const val qweather1608: Int = 363
    const val qweather1609: Int = 364
    const val qweather1610: Int = 365
    const val qweather1701: Int = 366
    const val qweather1702: Int = 367
    const val qweather1703: Int = 368
    const val qweather1801: Int = 369
    const val qweather1802: Int = 370
    const val qweather1803: Int = 371
    const val qweather1804: Int = 372
    const val qweather1805: Int = 373
    const val qweather2001: Int = 374
    const val qweather2002: Int = 375
    const val qweather2003: Int = 376
    const val qweather2004: Int = 377
    const val qweather2005: Int = 378
    const val qweather2006: Int = 379
    const val qweather2007: Int = 380
    const val qweather2029: Int = 381
    const val qweather2030: Int = 382
    const val qweather2031: Int = 383
    const val qweather2032: Int = 384
    const val qweather2033: Int = 385
    const val qweather2050: Int = 386
    const val qweather2051: Int = 387
    const val qweather2052: Int = 388
    const val qweather2053: Int = 389
    const val qweather2054: Int = 390
    const val qweather2070: Int = 391
    const val qweather2071: Int = 392
    const val qweather2072: Int = 393
    const val qweather2073: Int = 394
    const val qweather2074: Int = 395
    const val qweather2075: Int = 396
    const val qweather2076: Int = 397
    const val qweather2077: Int = 398
    const val qweather2078: Int = 399
    const val qweather2079: Int = 400
    const val qweather2080: Int = 401
    const val qweather2081: Int = 402
    const val qweather2082: Int = 403
    const val qweather2083: Int = 404
    const val qweather2084: Int = 405
    const val qweather2085: Int = 406
    const val qweather2100: Int = 407
    const val qweather2101: Int = 408
    const val qweather2102: Int = 409
    const val qweather2103: Int = 410
    const val qweather2104: Int = 411
    const val qweather2105: Int = 412
    const val qweather2106: Int = 413
    const val qweather2107: Int = 414
    const val qweather2108: Int = 415
    const val qweather2109: Int = 416
    const val qweather2111: Int = 417
    const val qweather2120: Int = 418
    const val qweather2121: Int = 419
    const val qweather2122: Int = 420
    const val qweather2123: Int = 421
    const val qweather2124: Int = 422
    const val qweather2125: Int = 423
    const val qweather2126: Int = 424
    const val qweather2127: Int = 425
    const val qweather2128: Int = 426
    const val qweather2129: Int = 427
    const val qweather2130: Int = 428
    const val qweather2131: Int = 429
    const val qweather2132: Int = 430
    const val qweather2133: Int = 431
    const val qweather2134: Int = 432
    const val qweather2135: Int = 433
    const val qweather2150: Int = 434
    const val qweather2151: Int = 435
    const val qweather2152: Int = 436
    const val qweather2153: Int = 437
    const val qweather2154: Int = 438
    const val qweather2155: Int = 439
    const val qweather2156: Int = 440
    const val qweather2157: Int = 441
    const val qweather2158: Int = 442
    const val qweather2159: Int = 443
    const val qweather2160: Int = 444
    const val qweather2161: Int = 445
    const val qweather2162: Int = 446
    const val qweather2163: Int = 447
    const val qweather2164: Int = 448
    const val qweather2165: Int = 449
    const val qweather2166: Int = 450
    const val qweather2190: Int = 451
    const val qweather2191: Int = 452
    const val qweather2192: Int = 453
    const val qweather2193: Int = 454
    const val qweather2200: Int = 455
    const val qweather2201: Int = 456
    const val qweather2202: Int = 457
    const val qweather2203: Int = 458
    const val qweather2204: Int = 459
    const val qweather2205: Int = 460
    const val qweather2207: Int = 461
    const val qweather2208: Int = 462
    const val qweather2209: Int = 463
    const val qweather2210: Int = 464
    const val qweather2211: Int = 465
    const val qweather2212: Int = 466
    const val qweather2213: Int = 467
    const val qweather2214: Int = 468
    const val qweather2215: Int = 469
    const val qweather2216: Int = 470
    const val qweather2217: Int = 471
    const val qweather2218: Int = 472
    const val qweather2300: Int = 473
    const val qweather2301: Int = 474
    const val qweather2302: Int = 475
    const val qweather2303: Int = 476
    const val qweather2304: Int = 477
    const val qweather2305: Int = 478
    const val qweather2306: Int = 479
    const val qweather2307: Int = 480
    const val qweather2308: Int = 481
    const val qweather2309: Int = 482
    const val qweather2311: Int = 483
    const val qweather2312: Int = 484
    const val qweather2313: Int = 485
    const val qweather2314: Int = 486
    const val qweather2315: Int = 487
    const val qweather2316: Int = 488
    const val qweather2317: Int = 489
    const val qweather2318: Int = 490
    const val qweather2319: Int = 491
    const val qweather2320: Int = 492
    const val qweather2321: Int = 493
    const val qweather2322: Int = 494
    const val qweather2323: Int = 495
    const val qweather2324: Int = 496
    const val qweather2325: Int = 497
    const val qweather2326: Int = 498
    const val qweather2327: Int = 499
    const val qweather2328: Int = 500
    const val qweather2330: Int = 501
    const val qweather2331: Int = 502
    const val qweather2332: Int = 503
    const val qweather2333: Int = 504
    const val qweather2341: Int = 505
    const val qweather2343: Int = 506
    const val qweather2345: Int = 507
    const val qweather2346: Int = 508
    const val qweather2348: Int = 509
    const val qweather2349: Int = 510
    const val qweather2350: Int = 511
    const val qweather2351: Int = 512
    const val qweather2352: Int = 513
    const val qweather2353: Int = 514
    const val qweather2354: Int = 515
    const val qweather2355: Int = 516
    const val qweather2356: Int = 517
    const val qweather2357: Int = 518
    const val qweather2358: Int = 519
    const val qweather2359: Int = 520
    const val qweather2360: Int = 521
    const val qweather2361: Int = 522
    const val qweather2362: Int = 523
    const val qweather2363: Int = 524
    const val qweather2364: Int = 525
    const val qweather2365: Int = 526
    const val qweather2366: Int = 527
    const val qweather2367: Int = 528
    const val qweather2368: Int = 529
    const val qweather2369: Int = 530
    const val qweather2370: Int = 531
    const val qweather2371: Int = 532
    const val qweather2372: Int = 533
    const val qweather2373: Int = 534
    const val qweather2374: Int = 535
    const val qweather2375: Int = 536
    const val qweather2376: Int = 537
    const val qweather2377: Int = 538
    const val qweather2378: Int = 539
    const val qweather2379: Int = 540
    const val qweather2380: Int = 541
    const val qweather2381: Int = 542
    const val qweather2382: Int = 543
    const val qweather2383: Int = 544
    const val qweather2384: Int = 545
    const val qweather2385: Int = 546
    const val qweather2386: Int = 547
    const val qweather2387: Int = 548
    const val qweather2388: Int = 549
    const val qweather2389: Int = 550
    const val qweather2390: Int = 551
    const val qweather2391: Int = 552
    const val qweather2392: Int = 553
    const val qweather2393: Int = 554
    const val qweather2394: Int = 555
    const val qweather2395: Int = 556
    const val qweather2396: Int = 557
    const val qweather2397: Int = 558
    const val qweather2398: Int = 559
    const val qweather2399: Int = 560
    const val qweather2400: Int = 561
    const val qweather2409: Int = 562
    const val qweather2411: Int = 563
    const val qweather2412: Int = 564
    const val qweather2413: Int = 565
    const val qweather2414: Int = 566
    const val qweather2415: Int = 567
    const val qweather2416: Int = 568
    const val qweather2417: Int = 569
    const val qweather2418: Int = 570
    const val qweather2419: Int = 571
    const val qweather2420: Int = 572
    const val qweather2421: Int = 573
    const val qweather2422: Int = 574
    const val qweather2423: Int = 575
    const val qweather2424: Int = 576
    const val qweather2425: Int = 577
    const val qweather2426: Int = 578
    const val qweather300: Int = 579
    const val qweather301: Int = 580
    const val qweather302: Int = 581
    const val qweather303: Int = 582
    const val qweather304: Int = 583
    const val qweather305: Int = 584
    const val qweather306: Int = 585
    const val qweather307: Int = 586
    const val qweather308: Int = 587
    const val qweather309: Int = 588
    const val qweather310: Int = 589
    const val qweather311: Int = 590
    const val qweather312: Int = 591
    const val qweather313: Int = 592
    const val qweather314: Int = 593
    const val qweather315: Int = 594
    const val qweather316: Int = 595
    const val qweather317: Int = 596
    const val qweather318: Int = 597
    const val qweather350: Int = 598
    const val qweather351: Int = 599
    const val qweather399: Int = 600
    const val qweather400: Int = 601
    const val qweather401: Int = 602
    const val qweather402: Int = 603
    const val qweather403: Int = 604
    const val qweather404: Int = 605
    const val qweather405: Int = 606
    const val qweather406: Int = 607
    const val qweather407: Int = 608
    const val qweather408: Int = 609
    const val qweather409: Int = 610
    const val qweather410: Int = 611
    const val qweather456: Int = 612
    const val qweather457: Int = 613
    const val qweather499: Int = 614
    const val qweather500: Int = 615
    const val qweather501: Int = 616
    const val qweather502: Int = 617
    const val qweather503: Int = 618
    const val qweather504: Int = 619
    const val qweather507: Int = 620
    const val qweather508: Int = 621
    const val qweather509: Int = 622
    const val qweather510: Int = 623
    const val qweather511: Int = 624
    const val qweather512: Int = 625
    const val qweather513: Int = 626
    const val qweather514: Int = 627
    const val qweather515: Int = 628
    const val qweather800: Int = 629
    const val qweather801: Int = 630
    const val qweather802: Int = 631
    const val qweather803: Int = 632
    const val qweather804: Int = 633
    const val qweather805: Int = 634
    const val qweather806: Int = 635
    const val qweather807: Int = 636
    const val qweather900: Int = 637
    const val qweather901: Int = 638
    const val qweather999: Int = 639
    const val qweather9998: Int = 640
    const val qweather9999: Int = 641
    const val radio_button_partial: Int = 642
    const val receipt_long: Int = 643
    const val receipt_long_filled: Int = 644
    const val refresh: Int = 645
    const val remove: Int = 646
    const val reorder: Int = 647
    const val reset_iso: Int = 648
    const val resize: Int = 649
    const val responsive_layout: Int = 650
    const val restaurant: Int = 651
    const val rotate_auto: Int = 652
    const val rotate_right: Int = 653
    const val rounded_corner: Int = 654
    const val routine: Int = 655
    const val save: Int = 656
    const val schedule: Int = 657
    const val schedule_widget: Int = 658
    const val school: Int = 659
    const val school_filled: Int = 660
    const val science: Int = 661
    const val sdk: Int = 662
    const val search: Int = 663
    const val search_activity: Int = 664
    const val search_filled: Int = 665
    const val sell: Int = 666
    const val send_money: Int = 667
    const val sensor_door: Int = 668
    const val sentiment_dissatisfied: Int = 669
    const val sentiment_sad: Int = 670
    const val sentiment_very_satisfied: Int = 671
    const val settings: Int = 672
    const val settings_backup_restore: Int = 673
    const val shapes: Int = 674
    const val shower: Int = 675
    const val signature: Int = 676
    const val slow_motion_video: Int = 677
    const val speed: Int = 678
    const val speed_2: Int = 679
    const val speed_3: Int = 680
    const val speed_4: Int = 681
    const val splash_icon: Int = 682
    const val splash_icon_dark: Int = 683
    const val sports_volleyball: Int = 684
    const val square_foot: Int = 685
    const val stairs: Int = 686
    const val star: Int = 687
    const val star_filled: Int = 688
    const val star_half: Int = 689
    const val start: Int = 690
    const val stat_minus_2: Int = 691
    const val steps: Int = 692
    const val storefront: Int = 693
    const val stream: Int = 694
    const val stylus_laser_pointer: Int = 695
    const val support: Int = 696
    const val swap_vert: Int = 697
    const val swipe_left: Int = 698
    const val syringe: Int = 699
    const val tab_inactive: Int = 700
    const val table_restaurant: Int = 701
    const val tactic: Int = 702
    const val tag: Int = 703
    const val target: Int = 704
    const val temp_preferences_eco: Int = 705
    const val texture: Int = 706
    const val thumb_down: Int = 707
    const val thumb_up: Int = 708
    const val timer: Int = 709
    const val toll: Int = 710
    const val translate: Int = 711
    const val tune: Int = 712
    const val undo: Int = 713
    const val unfold_less: Int = 714
    const val unfold_more: Int = 715
    const val upcoming: Int = 716
    const val update: Int = 717
    const val verified: Int = 718
    const val visibility: Int = 719
    const val visibility_off: Int = 720
    const val voice_selection: Int = 721
    const val volunteer_activism: Int = 722
    const val vpn_key: Int = 723
    const val wallpaper: Int = 724
    const val wand_stars: Int = 725
    const val warning: Int = 726
    const val water_drop: Int = 727
    const val water_voc: Int = 728
    const val widgets: Int = 729
    const val widgets_filled: Int = 730
    const val wifi: Int = 731
    const val wifi_password: Int = 732
    const val work: Int = 733

    val size: Int get() = names.size

    fun nameOf(id: Int): String = names[id]

    fun resourceOf(id: Int): DrawableResource =
        Res.allDrawableResources.getValue(names[id])
}

@Composable
fun painterResource(id: Int): Painter = composePainterResource(Drawables.resourceOf(id))

fun drawableResource(id: Int): DrawableResource = Drawables.resourceOf(id)
