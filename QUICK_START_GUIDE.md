# 🚀 VR Telegram Player - 快速開始指南

## ⚡ 5 分鐘快速設置

### 第 1 步：準備環境（2 分鐘）

```bash
# 1. 安裝 Android Studio
# 訪問：https://developer.android.com/studio
# 下載並安裝最新版本

# 2. 安裝 Android SDK
# 打開 Android Studio → Settings → SDK Manager
# 安裝 API 34 SDK 和 Build Tools

# 3. 確認 Java 版本
java -version
# 應該是 Java 11+
```

### 第 2 步：打開項目（1 分鐘）

```bash
# 方式 1：使用 Android Studio
# File → Open → 選擇 VRPlayerApp 文件夾

# 方式 2：使用命令行
cd VRPlayerApp
./gradlew --version  # 檢查 Gradle
```

### 第 3 步：配置後端地址（1 分鐘）

編輯文件：`app/src/main/java/com/vr/player/TelegramManager.kt`

找到第 20 行：
```kotlin
private const val BACKEND_URL = "http://192.168.1.100:8000"
```

改成您的後端服務器 IP：
```kotlin
private const val BACKEND_URL = "http://YOUR_IP:8000"
```

### 第 4 步：編譯應用（1 分鐘）

```bash
# 使用 Gradle 編譯
./gradlew assembleDebug

# 或使用 Android Studio
# Build → Build Bundle(s) / APK(s) → Build APK(s)
```

---

## 📱 安裝到設備

### 方式 1：使用 ADB（推薦）

```bash
# 連接 Android 設備
adb devices

# 安裝 APK
adb install app/build/outputs/apk/debug/app-debug.apk

# 運行應用
adb shell am start -n com.vr.player/.MainActivity
```

### 方式 2：使用 Android Studio

1. 連接 Android 設備
2. 點擊 **Run** 按鈕
3. 選擇您的設備
4. 應用會自動安裝並運行

### 方式 3：手動安裝

1. 將 `app-debug.apk` 複製到手機
2. 打開檔案管理器
3. 點擊 APK 檔案
4. 按照提示安裝

---

## 🎮 使用應用

### 啟動應用

1. 在手機上找到 **VR Player Pro** 應用
2. 點擊打開
3. 應該看到黑色 VR 屏幕和底部控制面板

### 播放本地影片

1. 點擊 **「選擇影片 (VR Mode)」** 按鈕
2. 選擇一個 MP4 影片
3. 影片會自動開始播放
4. 屏幕分成左右兩半（VR 模式）

### 播放 Telegram 影片

1. 確保後端服務正在運行
2. 點擊 **「選擇影片」** 按鈕
3. 等待加載 Telegram 影片列表
4. 選擇一個影片開始下載
5. 下載完成後自動播放

### 控制播放

**遙控器**（如果有）：
- D-Pad Up/Down：調整瞳距
- D-Pad Left/Right：調整音量
- Enter：播放/暫停

**觸摸屏**：
- 單擊：顯示/隱藏控制面板
- 雙擊：播放/暫停
- 左滑：快退 10 秒
- 右滑：快進 10 秒

---

## 🔧 後端服務設置

### 在 Windows 上運行後端

```bash
# 1. 打開命令提示符
# 2. 進入後端目錄
cd TelegramVRBackend

# 3. 啟動後端服務
python main_bot_fixed.py

# 應該看到：
# INFO:     Uvicorn running on http://0.0.0.0:8000
```

### 驗證後端

```bash
# 在瀏覽器中打開
http://localhost:8000/health

# 應該看到：
# {"status": "ok"}
```

### 查看 API 文檔

```
http://localhost:8000/docs
```

---

## 📊 項目結構

```
VRPlayerApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/vr/player/
│   │   │   ├── MainActivity.kt              ← 主活動
│   │   │   ├── VRRenderer.kt                ← VR 渲染
│   │   │   ├── HeadTracker.kt               ← 陀螺儀
│   │   │   ├── TelegramManager.kt           ← Telegram API
│   │   │   └── GestureController.kt         ← 觸摸控制
│   │   └── res/layout/activity_main.xml     ← UI 佈局
│   └── build.gradle                         ← 依賴配置
└── README.md                                ← 詳細說明
```

---

## 🐛 常見問題

### Q: 應用無法連接後端？
**A**: 檢查 BACKEND_URL 是否正確，確保後端服務正在運行。

### Q: 影片無法播放？
**A**: 確認影片格式為 MP4，檢查文件權限。

### Q: 陀螺儀不工作？
**A**: 確認設備有陀螺儀傳感器，某些模擬器不支援。

### Q: 下載速度慢？
**A**: 檢查網絡連接，優化後端服務器。

---

## 📚 更多資源

- **完整代碼文檔**：`COMPLETE_CODE_SUMMARY.md`
- **部署指南**：`FINAL_DEPLOYMENT_GUIDE.md`
- **開發文檔**：`VR_DEVELOPMENT.md`
- **Telegram 集成**：`TELEGRAM_INTEGRATION.md`

---

## 🎯 下一步

1. ✅ 編譯和安裝應用
2. ✅ 測試本地影片播放
3. ✅ 配置後端服務
4. ✅ 測試 Telegram 影片下載
5. ✅ 測試 VR 功能（陀螺儀、瞳距調整）
6. ✅ 測試觸摸屏控制
7. 🚀 發佈到 Google Play Store

---

**祝您使用愉快！** 🎉

有任何問題，請查看詳細文檔或檢查 logcat 日誌。
