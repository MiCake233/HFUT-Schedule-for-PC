# 聚在工大 · Windows 桌面版（HFUT-Schedule Desktop）

<p align="center">
  <img src="/docs/img/desktop-icon.png" width="128" alt="HFUT-Schedule 桌面版图标">
</p>

> 本项目是 [**Chiu-xaH/HFUT-Schedule**](https://github.com/Chiu-xaH/HFUT-Schedule) 的 **Windows 桌面移植版**：
> 用 Kotlin Multiplatform + Compose Multiplatform 把原 Android 应用的界面与逻辑**直接复用**到电脑上，
> 不是重写、也不是网页套壳。项目灵感、界面设计与绝大部分业务代码均来自上游项目，
> 遵循 **GNU GPL-3.0** 协议，来源与致谢见下方章节。

## 下载（Windows 10 / 11 x64）

到本仓库的 [Releases](../../releases/latest) 页面下载：

| 文件 | 说明 |
| --- | --- |
| `HFUT-Schedule-Setup-x.y.z.msi` | 安装包，双击安装（**自带 JDK 21 运行时**，无需另装 Java），安装后开始菜单/桌面有快捷方式 |
| `HFUT-Schedule-x.y.z-portable.zip` | 便携版，解压后双击 `HFUT-Schedule.exe` 即可，不写注册表 |

## 桌面版能做什么

| 能力 | 说明 |
| --- | --- |
| 课表 | 周视图 + 学期切换 + 多课表来源 + 拖拽排序 + 空白格点击建单次日程，网格宽度做了上限（窗口拉宽也不会把课程格拉胖） |
| 登录 | 统一身份认证（CAS）账号密码登录，验证码在线显示，登录态存在本地；无需模拟器 |
| 一卡通 | 卡片余额/流水/账单分析、消费报表、限额设置 |
| 成绩 / 考试 | 成绩查询与 GPA 分析、考试安排、教学计划完成情况、总学分、挂科率、教师检索、课程检索 |
| 生活服务 | 天气与校园地图、楼层导向、第三方应用入口、失物招领、通知中心 |
| 洗浴 / 水电 / 洗衣 | 淋浴使用码、电费余额与充值入口、洗衣机状态 |
| 图书馆 / 校车 | 借阅记录、馆藏检索、班车时刻 |
| Supabase | 信息共建、日程共建、网址导航、存储 |
| 其他 | 节假日、第二课堂、招生信息、校友、辅导员（工作）、AI 助手、校园邮箱…… |
| 桌面端专属 | 窗口尺寸/位置记忆、`--snapshot` 离屏渲染、ICS 日历导出（代替系统日历写入） |

## 电脑上确实做不到的功能（入口已从界面移除）

| 功能 | 原因 |
| --- | --- |
| 相机扫码 | 没有摄像头调用链 |
| 内嵌 WebView 页面（WebVPN、办事大厅、淋浴登录、慧新登录） | 桌面端没有 WebView，改用**系统默认浏览器**打开 |
| 桌面小组件 / 快捷方式排序 / 控制中心磁贴 / 通知栏实时活动 | Android 独有的系统概念 |
| 学期报告（PDF 导出）、个人信息页 | 依赖 Android 的导出与专属布局 |
| 选课 / 退课 / 转专业 | 页面实现依赖 Android 专属控件 |
| OCR 验证码自动识别、AIDL 互联、应用内增量更新 | Android 系统能力 |

以上入口在桌面端**不会出现**（由 `PlatformFeatures` 统一判定），不会让用户点进去看到占位页。

## 从源码构建

前置：JDK 21、Android SDK（仅在编译 `:app` 的 Android 目标时需要）、[WiX Toolset 3.11](https://github.com/wixtoolset/wix3/releases)（仅在生成 MSI 时需要）。

```powershell
# 只编译桌面运行时依赖的 JVM 目标
.\gradlew.bat :app:compileKotlinJvm

# 免安装目录（build/compose/binaries/main/app/HFUT-Schedule/）
.\gradlew.bat :desktopApp:createDistributable

# 生成 MSI 安装包（需要 WiX，设置 WIX_PATH 或在 PATH 里有 candle/light）
.\gradlew.bat :desktopApp:packageMsi
```

验证任务（本项目所有改动都靠它们做回归）：

```powershell
.\gradlew.bat :desktopApp:snapshotHome      # 离屏渲染主界面
.\gradlew.bat :desktopApp:loginProbe        # CAS 登录链路诊断
.\gradlew.bat :desktopApp:verifyIcs         # 日历导出 .ics
.\gradlew.bat :desktopApp:verifyDrawables   # 图标注册表
.\gradlew.bat :desktopApp:verifyFuncs       # 桌面端功能过滤
```

移植细节与每个版本踩过的坑见 [`docs/桌面版移植说明.md`](docs/桌面版移植说明.md)，使用说明见 [`docs/桌面版使用说明.txt`](docs/桌面版使用说明.txt)。

## 来源与致谢

* 上游项目：[**Chiu-xaH/HFUT-Schedule**](https://github.com/Chiu-xaH/HFUT-Schedule) —— 本桌面版**灵感来源于此**，并直接复用了其绝大部分界面与业务逻辑。请先给上游项目点个 Star。
* 本仓库在 `desktop-windows` 分支上叠加桌面移植：新增 `:desktopApp` 模块、把 Android 源码逐步搬到 `jvmShared` 共享源集、以 `expect/actual` 划出平台边界。
* 上游作者与贡献者名单见 [`docs/Developer.md`](docs/Developer.md)。
* 图标：为规避校徽版权，桌面版使用**卡通风格自制徽标**（`desktopApp/icons/app-512.png`，由 `research/make-icon.ps1` 类似的脚本生成），并非合肥工业大学官方校徽。
* 本项目按 **GNU General Public License v3.0** 发布，与上游一致，详见 [`LICENSE`](LICENSE)。

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