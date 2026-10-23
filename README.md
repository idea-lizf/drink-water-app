# 喝水记录 (DrinkWater)

一个**不联网、不登录、数据只存本机**的安卓喝水记录 App。

- Kotlin + Jetpack Compose（Material 3）+ Room 本地数据库
- 无任何权限声明（连网络权限都没有，物理上无法联网）
- 最低支持 Android 8.0，适配到 Android 16
- 详细设计见 [docs/架构.md](docs/架构.md)

如果你是前端开发者，先看这张对照表，全程无障碍：

| 前端世界 | 本项目对应 |
|---|---|
| Chrome 浏览器 | 模拟器（Android Studio 的 Emulator） |
| `npm run dev` | Android Studio 的 **Run ▶**（或 Live Edit 热更新） |
| 改代码保存页面自动刷新 | **Live Edit**（Compose 特性，实时生效） |
| Storybook 组件预览 | Compose Preview（`@Preview` 注解） |
| DevTools Console | **Logcat** 面板 |
| DevTools Debugger 断点 | Android Studio 断点调试（一模一样） |
| `npm run build` 出 dist | Gradle 打包出 APK |
| 部署上线 | 装 APK 到手机 |
| `node_modules` | Gradle 缓存（自动管理，不用管） |
| `package.json` | `gradle/libs.versions.toml`（依赖版本）+ `app/build.gradle.kts` |

---

## 环境要求（本机已全部就绪）

| 工具 | 位置 / 说明 |
|---|---|
| Android Studio | `/Applications/Android Studio.app`（自带 JDK，无需另装 Java） |
| Android SDK | `~/Library/Android/sdk` |
| 模拟器 | 已建好虚拟设备 `Medium_Phone_API_37.0`（Android 16） |
| Gradle | 项目自带 wrapper（`./gradlew`），首次使用自动下载 |

---

## 一、从代码启动（跑起来）

### 方式 1：Android Studio（推荐日常使用）

1. 打开 Android Studio → **Open** → 选择本项目目录 `drink-water-app`
2. 首次打开会自动 **Gradle Sync**（右下角进度条），等待完成
3. 顶部工具栏设备下拉框选 **Medium_Phone_API_37.0**
4. 点绿色 **Run ▶**（或按 `Control + R`）
5. 模拟器窗口弹出 → App 自动安装并启动

### 方式 2：命令行

```bash
# 1. 启动模拟器（-no-snapshot-load 是冷启动，可避开 GPU 快照崩溃问题）
~/Library/Android/sdk/emulator/emulator -avd Medium_Phone_API_37.0 -no-snapshot-load -gpu swiftshader_indirect &

# 2. 构建并安装启动（JAVA_HOME 指向 Android Studio 自带 JDK）
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :app:assembleDebug

~/Library/Android/sdk/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk
~/Library/Android/sdk/platform-tools/adb shell am start -n com.drinkwater.app/.MainActivity
```

> 提示：把 `~/Library/Android/sdk/platform-tools` 加进 PATH，以后敲 `adb` 就不用写全路径：
> ```bash
> echo 'export PATH=~/Library/Android/sdk/platform-tools:$PATH' >> ~/.zshrc && source ~/.zshrc
> ```

---

## 二、修改代码

### 代码地图（改哪里）

```
app/src/main/java/com/drinkwater/app/
├── MainActivity.kt              # App 入口，就一个 Activity
├── ui/
│   ├── theme/Theme.kt           # 主题配色（想改颜色来这里）
│   ├── AppScaffold.kt           # 底部导航结构
│   ├── today/TodayScreen.kt     # 「今日」页界面
│   ├── today/TodayViewModel.kt  # 「今日」页逻辑（增删记录）
│   ├── stats/StatsScreen.kt     # 「统计」页界面
│   ├── stats/StatsViewModel.kt  # 「统计」页逻辑（7/30天图表）
│   ├── components/BarChart.kt   # 柱状图组件
└── data/                        # 数据层（Room 数据库 + 统计计算）
    ├── DrinkRecord.kt           # 数据表结构（每条喝水记录）
    ├── DrinkRecordDao.kt        # 增删查 SQL
    ├── AppDatabase.kt           # 数据库实例
    ├── DrinkRepository.kt       # 数据仓库
    └── DrinkStats.kt            # 按天分组统计的纯逻辑

app/src/main/res/values/strings.xml   # App 名称等文案
```

### 常见修改示例

**改快捷按钮档位**（如想要 100/200/300ml）：
`ui/today/TodayScreen.kt` 里搜 `listOf(150, 250, 500)`，改成你要的数字。

**改主题色**：`ui/theme/Theme.kt` 里的 `LightColors` / `DarkColors`，`primary` 就是主色。

**改 App 名称**：`res/values/strings.xml` 里的 `app_name`。

### 改完怎么生效

- **Live Edit（推荐）**：模拟器运行中，Studio 里改代码 → `Cmd + S` 保存 → 模拟器界面**实时刷新**，不用重装（首次使用：Settings → Editor → Live Edit 确认已开启 "Push changes automatically"）
- **Run ▶**：改完重新点运行，会自动重装启动（约 10~20 秒）
- 小改文案/颜色用 Live Edit；加了新文件、改了依赖（`libs.versions.toml`）、改了数据库表结构，必须重新 Run 甚至 Re-Sync

---

## 三、打包 APK

### Debug 包（自用足够，推荐）

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :app:assembleDebug
```

产物位置：**`app/build/outputs/apk/debug/app-debug.apk`**

debug 包已含调试签名，任何手机都能直接安装，个人自用**完全够用**。

### Release 包（更小更快，需自备签名密钥，可选）

自用场景不是必需。如果想要：生成一个自己的密钥库并配置 `app/build.gradle.kts` 的 `signingConfigs`，然后 `./gradlew :app:assembleRelease`。需要时再说一声，我来配。

---

## 四、调试（相当于 DevTools）

### 看日志（Console）

Android Studio 底部点 **Logcat** 标签页：
- 左上选进程 `com.drinkwater.app`
- 想看自己的日志，代码里写 `Log.d("DrinkWater", "今天的总量=$total")`，Logcat 搜索框输 `DrinkWater` 过滤

### 断点调试（Debugger）

1. 代码行号右侧空白处点一下 → 打红点断点
2. 用 **Debug ▶**（虫子图标，`Control + D`）代替 Run 启动
3. 触发断点即暂停，看变量、单步、条件断点，和 Chrome DevTools 一模一样

### 常用 adb 命令速查

```bash
adb devices                          # 列出已连接的设备
adb install -r xxx.apk               # 安装/覆盖安装 APK
adb shell am start -n com.drinkwater.app/.MainActivity   # 启动 App
adb uninstall com.drinkwater.app     # 卸载
adb shell pm clear com.drinkwater.app  # 清空 App 数据（重置记录）
adb exec-out screencap -p > /tmp/screen.png   # 截屏到电脑
```

---

## 五、日常开发闭环（推荐流程）

```
改代码 → Cmd+S（Live Edit 实时看效果）
      → 满意后偶尔 Run ▶ 完整验证一遍
      → 阶段性完成：./gradlew :app:assembleDebug 打个新 APK
      → 用新 APK 装到真机替换旧版
```

**重要：模拟器和真机的数据是独立的。** 模拟器里练手的记录不会同步到手机；真机上的记录只存在于手机里（这正是"数据保存在本机"的设计）。卸载 App = 删除全部记录。

---

## 六、真机上线（装到手机）

### 方式 A：USB 直连（开发期推荐）

1. 手机开启开发者选项：**设置 → 关于手机 → 连续点「版本号」7 次**，提示已进入开发者模式
2. **设置 → 系统 → 开发者选项** → 打开 **USB 调试**
3. USB 线连电脑，手机弹窗「允许 USB 调试吗？」→ 勾选一律允许 → **允许**
4. Android Studio 顶部设备下拉框选您的手机 → **Run ▶**（自动编译、安装、启动一气呵成）

> 小米/华为/OPPO 等国产机若不识别，开发者选项里再打开「USB 安装」「USB 调试（安全设置）」。

### 方式 B：直接发 APK 文件（无需 USB 线）

1. 把 `app/build/outputs/apk/debug/app-debug.apk` 发到手机（微信文件传输助手 / AirDrop / 网盘均可）
2. 手机上点开 APK → 系统提示「未知来源」→ 允许安装
3. 装完即用，**不需要任何权限**（App 本身零权限）

升级版本：重新发新 APK 覆盖安装即可，**旧记录保留**。

---

## 七、常见问题（本机实战经验）

**Q：模拟器启动闪退 / 日志报 `Failed to restore previous context: 12297`**
GPU 快照恢复的兼容问题。用冷启动命令启动：
```bash
~/Library/Android/sdk/emulator/emulator -avd Medium_Phone_API_37.0 -no-snapshot-load -gpu swiftshader_indirect
```

**Q：命令行 `adb devices` 显示 `unauthorized`**
模拟器屏幕上有个「允许 USB 调试吗？」弹窗，勾选一律允许后点「允许」即可。

**Q：命令行构建报 `Unable to locate a Java Runtime`**
先执行 `export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"`（Android Studio 内操作不受影响）。

**Q：改了 `libs.versions.toml` / `build.gradle.kts` 后构建异常**
Android Studio 顶部会提示 **Sync Now**，点它同步依赖；不行就 File → Invalidate Caches → Restart。

**Q：想重置模拟器里的所有喝水记录**
```bash
adb shell pm clear com.drinkwater.app
```

**Q：手机上想清空记录重新开始**
卸载重装，或以后我加个「清空数据」按钮。
