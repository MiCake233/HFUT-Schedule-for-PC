# HFUT-Schedule for PC

<p align="center">
  <img src="/docs/img/desktop-icon.png" width="128" alt="HFUT-Schedule for PC">
</p>

本仓库是开源项目 [HFUT-Schedule](https://github.com/Chiu-xaH/HFUT-Schedule) 的 Windows 桌面端移植版本。
移植基于 Kotlin Multiplatform 与 Compose Multiplatform，复用上游的界面实现与业务逻辑。
许可为 Apache License 2.0。

> 程序展示的课程、考试安排、成绩、缴费、选课与通知等信息仅供参考，请以学校官方系统与官方通知为准；
> 因数据未加载、延迟、错误或程序不可用而错过重大考试、报名、缴费、选课等事项，作者概不负责。

## 下载

适用于 Windows 10 / 11 x64。安装文件见 [Releases](../../releases)。

| 文件 | 说明 |
| --- | --- |
| `HFUT-Schedule-Setup-x.y.z.msi` | 安装包。内置 JDK 21 运行时，无需预装 Java；高版本可覆盖升级。 |
| `HFUT-Schedule-x.y.z-portable.zip` | 便携版。解压后运行 `HFUT-Schedule.exe`。 |

## 功能

| 模块 | 内容 |
| --- | --- |
| 登录 | 统一身份认证（CAS）学号密码登录；会话凭据保存在本机，下次启动自动恢复，无需重复输入验证码；可一键退出登录并清除凭据。 |
| 课表 | 周视图，打开即定位当前周，可左右翻周或一键回到本周；切换学期、切换课表来源（含好友课表）；拖拽排序；点击空白格新建日程；方格高度、文字大小、内边距、不透明度、模糊、时间轴可调；课程表可导出为 PNG 图片。 |
| 今日课程面板 | 独立小窗口常驻桌面，显示今日课程的时间、课程名与地点；每 5 分钟自动刷新，可手动刷新；可始终置顶；窗口位置与大小自动记忆。 |
| 系统托盘 | 关闭窗口默认最小化到托盘；托盘菜单可显示/隐藏主窗口、开关今日课程面板与置顶、退出程序；命令行参数 `--minimized` 启动后仅驻留托盘。 |
| 提醒 | 上课与考试提醒，开始前 5～60 分钟（默认 20 分钟）通过系统托盘气泡提示，提前时间面板内可调。 |
| 启动与窗口 | 开机自启动开关（写入当前用户 Windows 启动项，开机后驻留托盘不弹窗）；主窗口尺寸与位置记忆。 |
| 更新 | 启动时自动检查新版本并弹窗提示，只认 GitHub 的**正式版**（预发布不会推送）；下载后按发布资产的 SHA-256 校验，校验不通过一律拒绝运行；「选项」内亦提供手动检查与打开发布页。 |
| 数据 | 通过系统文件对话框备份 / 还原本机数据目录；恢复前先解压到临时目录校验，失败不会动原数据；崩溃日志自动落盘，可在「修复」窗口查看、预览、清空与打开目录。 |
| 一卡通 | 余额、流水、账单分析、消费报表、限额。 |
| 教务 | 成绩查询与 GPA 分析、考试成绩详情、考试安排、教学计划完成情况、总学分、挂科率、课程检索、教师检索、空闲教室、选课、退课、转专业、个人信息、学期报告（可导出为图片）。 |
| 校园服务 | 图书馆检索与借阅记录、校车时刻、第二课堂、节假日、招生信息、校友、辅导员、淋浴使用码、电费、洗衣。 |
| 生活 | 天气、校园地图、楼层导向、第三方应用、失物招领、通知中心。 |
| 网络与导出 | WebVPN 设置页（校外访问教务）；网页类页面由系统默认浏览器打开；日程可导出为 `.ics` 文件。 |
| 其他 | Supabase（信息共建、日程、存储、网址导航）、AI 助手、校园邮箱、实验室、反馈。 |


## 安全与数据说明

- **数据目录**：`%APPDATA%\HFUT-Schedule`（便携版相同）。备份不含 `cache` 与 `update` 两个子目录。
- **密码**：桌面版**不保存**登录密码；旧版本曾写入的明文密码会在启动时清除。
- **登录状态**：为避免每次都输验证码，本机会保存 CAS 的会话凭据（TGC / SESSION），
  位于该目录下的 `prefs\*.properties`，**未额外加密**（仅受 Windows 用户目录权限保护）。
  与他人共用电脑时，请用「选项 → 启动 → 退出登录」清除，该操作会一并清掉本机保存的各平台 token。
- **崩溃日志**：写入 `%APPDATA%\HFUT-Schedule\logs`，仅保存在本机，内容含学号与堆栈，发送给他人前请自行检查。
- **自动更新**：只从本仓库的 GitHub Releases 读取版本信息；安装包下载后校验 SHA-256 与 MSI 文件头，
  两者任一不符即删除文件并拒绝运行。安装包当前未做代码签名，校验值以 GitHub API 返回的资产摘要为准。
- **网络**：程序直接访问学校系统与 GitHub；不内置任何遥测或第三方统计上报（统计相关功能复用上游，需自行配置）。

## 从源码构建

前置条件：JDK 21；编译 Android 目标需 Android SDK；生成 MSI 需 [WiX Toolset 3.11](https://github.com/wixtoolset/wix3/releases)，并由 `WIX_PATH` 指向其安装目录。

```powershell
.\gradlew.bat :app:compileKotlinJvm            # 桌面端依赖的 JVM 目标
.\gradlew.bat :desktopApp:createDistributable  # 免安装目录
.\gradlew.bat :desktopApp:packageMsi           # MSI 安装包
```

依赖版本：Compose Multiplatform 的 `org.jetbrains.*` 坐标是「对齐壳」（本体只有几百字节，
实现来自同名 `androidx.*` 坐标），壳与实现的版本在 `desktopApp/build.gradle.kts` 里用
`resolutionStrategy.force` 钉齐，避免同一个构件在发行包里出现两个版本。

## 自检

桌面端没有单元测试，验收靠下面这些可重复执行的 check（`packageMsi` 会自动先跑 `verifyClasspath`）：

```powershell
.\gradlew.bat :desktopApp:verifyClasspath  # 运行时 classpath 无重复类（同一 FQN 出现在两个 jar 即失败）
.\gradlew.bat :desktopApp:verifyEnv        # 不是调试构建（isNormalEnv / 密码未落盘）
.\gradlew.bat :desktopApp:verifyFuncs      # 桌面不可用的入口已从 UI 过滤
.\gradlew.bat :desktopApp:verifyWeek       # 学期开始时间 → 当前周
.\gradlew.bat :desktopApp:verifySession    # 本机凭据存在且用 TGC 换回 SESSION
.\gradlew.bat :desktopApp:verifyUpdate     # 只认正式版，能解析出 .msi 直链与 sha256
.\gradlew.bat :desktopApp:verifyIntegrity  # 摘要不符 / 非 MSI 必须被拦下
.\gradlew.bat :desktopApp:verifyBackup     # 正常恢复、zip 越界、空包三种场景
.\gradlew.bat :desktopApp:verifyIcs        # 日程导出为 .ics
.\gradlew.bat :desktopApp:verifyDrawables  # 天气等图标能从注册表按名字取到
```

安装版也可以直接复跑其中几项，例如 `HFUT-Schedule.exe --verify-classpath`（需从控制台启动才看得到输出）。

## 反馈与联系

- 问题**优先提交到本仓库 [Issues](../../issues)**：便于检索、跟踪，也方便其他人参考同类问题的处理方式。
- 也可直接邮件联系：`zoran.zhao@hotmail.com`。
- 提交时建议附上：问题现象、复现步骤、程序版本（「选项 → 版本信息」），必要时附崩溃日志
  （「选项」中打开「修复」窗口 → 日志目录）。

## 来源与许可

* 上游项目：[Chiu-xaH/HFUT-Schedule](https://github.com/Chiu-xaH/HFUT-Schedule)（Apache License 2.0）。
  本桌面版基于上游源码修改而成，复用了其绝大部分界面与业务逻辑；
  上游版权归原作者及贡献者所有，贡献者名单见 [`docs/Developer.md`](docs/Developer.md)。
* 修改说明：本仓库在 `desktop-windows` 分支上做了桌面移植改动，详见 [`NOTICE`](NOTICE)。
* 桌面移植部分：新增 `:desktopApp` 模块，把 Android 源码逐步迁入 `jvmShared` 共享源集，用 `expect/actual` 划分平台边界。
* 许可证：**Apache License 2.0**（与上游一致），详见 [`LICENSE`](LICENSE)。

---

# 聚在工大（HFUT-Schedule）
![封面](/src/img/cover.png)

## 下载
### 正式版
<div align="center">

[![GitHub](https://img.shields.io/github/v/release/Chiu-xaH/HFUT-Schedule?logo=github&label=GitHub&style=for-the-badge)](https://github.com/Chiu-xaH/HFUT-Schedule/releases/latest)
[![F-Droid](https://img.shields.io/f-droid/v/com.hfut.schedule?logo=fdroid&style=for-the-badge)](https://f-droid.org/packages/com.hfut.schedule)
[![F-Droid](https://img.shields.io/github/v/release/Chiu-xaH/HFUT-Schedule?logo=gitee&label=Gitee&style=for-the-badge)](https://gitee.com/chiu-xah/HFUT-Schedule/releases/tag/Android)

</div>

> F-Droid 发行的版本经过重签名（无法与其他渠道版本更新覆盖安装 Apk），且节奏较慢，不推荐 ；
> 
> Github 发行的版本有三个 Apk，分别对应 arm64, x86_64, 通用包（universal），按需选择安装（一般选 arm64）；
> 
> Gitee 发行的版本只有 arm64，且只有 arm64 版本支持增量更新。

### [开发版（Dev）](https://github.com/Chiu-xaH/HFUT-Schedule/releases/tag/Devevlop)
> 供尝鲜正在开发中的版本，每次随代码 Push 而更新发行的 Apk，不代表最终正式版。

### 资源包（可选）
- 图片验证码识别模型（应用内下载，位于【选项-偏好与配置-图片验证码自动填充】）
> 为减少 Apk 包体大小，验证码识别模型（约 11MB）拆分出来并支持可选下载；
- 游客体验专用数据包（应用内下载，位于【选项-偏好与配置-备份与恢复】）
> 为让其他开发者也可体验本应用，提供了游客数据包，导入后可解锁更多功能，但并非所有功能均可用，如有相关需求可联系开发者。

## 简介
为合肥⼯业⼤学（HFUT）本科⽣开发的校园服务聚合类 Android 应⽤，覆盖了教学、⽣活、校内服务等 40+ 场景与功能，⽀持离⽹使⽤；使⽤现代的技术栈开发，提供全⾯、简洁、⾼效的使⽤体验。

界面展示（v4.20.1.3,实际以最新版本为准）：

![截图](/src/img/ui.jpg)

## 亮点
界面: 使用着色器、实时模糊等特效，与 Material Design 3 融合，简约高效

动效：自研第二代全局转场动画体系，符合直觉的一镜到底动效

更新：250+ 版本迭代，平均每周至少更新一次，带来新的功能、重构及优化

性能：冷启动迅速，开屏即展示关键信息；内存与存储占用适中

## 开始使用
### 要求
Android 8.0 (SDK26+) 及以上，接入互联网
### 初次使用
保证接入互联网的环境下，填入学号与信息门户密码，登录，等待出现加载完成(底栏由暗变正常)，即可使用
### 刷新登陆
登陆后会获取所需的数据，自动缓存(例如课程表教务源、课程汇总等)，由于平台限制，有些平台不支持持久登录，在登录时会将过期的平台选择性地刷新
### 软件升级
在启动时自动检查更新，如有更新会在首页底栏【选项】显示小红点，选项界面会有下载提示，下载完成后点击安装，授权安装未知应用权限即可

## 文档库
### [更新日志](docs/update)

### [开发文档](docs/Developer.md) 
正在佛系完善，方便参与本项目（Pull Request）时能方便地知晓各处轮子地用法

### [Pull Request 规范](/docs/Rule.md)
如需参与本项目，请阅读

### [架构介绍](/docs/Arch.md)
待完善，目前有数据源图

### [DeepLink](docs/DeepLink.md)
如有需要跳转其他场景的需求，可发 issue

### [Aidl](docs/Aidl.md)
暂时停更，如有接入需求，可发 issue 使其重启

### [校内 Api 收集](docs/HfutApi.md)
已停更，大部分核心接口已写好，因 AI 的发展，其余接口可直接让 AI 从代码中梳理即可

### [功能差异化](/docs/FeatureAvailable.md)
因安卓版本不同以及不同厂商定制系统，聚在工大某些功能呈现会有差异

### [统计报表](/docs/Chart.md)
仓库代码体量、用户量及日流量（通过 Supabase 平台托管，安全统计，无隐私数据，不定期更新）

### 开发纪实
本主题随缘更新，都是自己在开发项目时候的一些经历（但我对于写文章很懒，佛系更新...）

#### [开发纪实：包体缩减](/docs/technical/PackageLess.md)
如何让聚在工大从巅峰期的 49MB 缩减至目前的 26MB

#### [开发纪实：容器共享](/docs/technical/ContainerShared.md) 
对官方库的改造再封装，以及无奈妥协，最终决定自制SharedNav

#### [开发纪实：KMP 改造](/docs/technical/ToKmp.md) 
待开发，等买了 MacBook

#### [开发纪实：聚在工大的 UI&UX 设计](/docs/technical/Design.md) (待更新)

#### [开发纪实：聚在工大的背景与前世今生](/docs/technical/Background.md) (待更新)


## [联系方式](zsh0908@outlook.com)
开发者: zsh0908@outlook.com

## [其他工具](/tools)
[图片验证码训练模型](/tools/Captcha-Ocr)

[校园网登录](/tools/Login-Web-Python)

[WebVpn](tools/WebVpn)

[PC版(集成WebVpn、校园网等工具)](/tools/Lite-For-PC) (待开发)

## 鸣谢
### 第三方库
[OkHttp](https://github.com/square/okhttp) 网络请求

[Retrofit](https://github.com/square/retrofit) 网络请求

[Gson](https://github.com/google/gson) JSON解析

[Jsoup](https://github.com/jhy/jsoup) XML/HTML解析

[Zxing](https://github.com/zxing/zxing) 二维码

[Haze](https://github.com/chrisbanes/haze) 层级模糊

[Accompanist](https://github.com/google/accompanist) 扩展工具包

[Glide](https://github.com/bumptech/glide) 图片

[EdDSA Java](https://github.com/str4d/ed25519-java) 加密(供和风天气API使用)

[Konfetti](https://github.com/DanielMartinus/Konfetti) 礼花动画

[Tesseract4Android](https://github.com/adaptech-cz/Tesseract4Android) 封装Tesseract (供识别图片验证码)

[MaterialKolor](https://github.com/jordond/MaterialKolor) 取色

[Reorderable](https://github.com/Calvin-LL/Reorderable) 列表拖拽

[LeakCanary](https://github.com/square/leakcanary) 内存泄漏工具

[AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) 液态玻璃

[DeviceCompat](https://github.com/getActivity/DeviceCompat) 设备识别 (供判断鸿蒙NEXT环境)

### 从本App解耦的库

[DiffUpdater](https://github.com/Chiu-xaH/DiffUpdater) 增量更新

[SharedNav](https://github.com/Chiu-xaH/SharedNav) 页面管理、容器共享、浮窗体系

[Mirror-Android](https://github.com/Chiu-xaH/Mirror-Android) 镜面效果（着色器）

### 开源项目
[holiday-cn](https://github.com/NateScarlet/holiday-cn) 节假日数据源

[webvpn-dlut](https://github.com/ESWZY/webvpn-dlut) WebVpn转换

[Tesseract](https://github.com/tesseract-ocr/tesseract) 用于训练OCR识别验证码的[基础模型](https://github.com/tesseract-ocr/tessdata)

[Supabase](https://github.com/supabase/supabase) 托管供提供数据库

### 开源社区
初期参考了 Space 课表(微信小程序)的一些功能设计，后期参考了若干开源 App (例如 [师韵-SmartHNU](https://github.com/JiaLiFuNia/SmartHNU))，还有对一些移动操作系统的借鉴，在此不一一列举了

如果你的项目参考了本 App 的一些 UI、UX，或者相关库（增量更新、导航库等），期望能在README或者应用内署名标注下，这样也能让好的 idea 传播给更多的开发者，促进开源社区发展

感谢其他高校开发者对本项目的肯定与参考，在此不一一列举了

若干开发者和用户的帮助：
- linsui 帮助上架 F-Droid
- James-Zhang2 提供 GPA 评定数据源
- tinyvan,Today1337, zxbmmmmmmmmm, Junpgle 提交 RR 助力开发
- 其他用户帮助推广、通过邮件、issue 等提供功能建议与反馈等，在此不一一列举了