# 🎬 VR Telegram Player - Android 應用

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Android](https://img.shields.io/badge/android-5.0%2B-blue)
![License](https://img.shields.io/badge/license-MIT-green)

一個功能完整的 VR 視頻播放器，集成 Telegram 後端，支持雙眼 VR 渲染、陀螺儀頭部追蹤和光學畸變校正。

## ✨ 主要功能

### 🎮 VR 渲染
- ✅ 雙眼分割渲染（Side-by-Side）
- ✅ 光學畸變校正（Barrel Distortion）
- ✅ 瞳距動態調整
- ✅ 60fps 性能優化

### 📱 頭部追蹤
- ✅ 陀螺儀實時追蹤
- ✅ 旋轉矩陣計算
- ✅ 坐標軸自動適配

### 📺 視頻播放
- ✅ ExoPlayer 支持多種格式
- ✅ 進度條和時間顯示
- ✅ 音量控制

### 🤖 Telegram 集成
- ✅ 獲取 Telegram 頻道影片
- ✅ 下載進度追蹤
- ✅ 本地文件管理

### 🎯 觸摸屏控制
- ✅ 單擊、雙擊、滑動、捏合
- ✅ 快進/快退
- ✅ 亮度調整

---

## 🚀 快速開始

### 方式 1：GitHub Actions 自動構建（推薦）

**只需 3 分鐘！無需本地 Android Studio**

詳細指南：[GITHUB_QUICK_START.md](./GITHUB_QUICK_START.md)

```bash
# 1. 創建 GitHub 倉庫
# 訪問 https://github.com/new

# 2. 上傳代碼
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/VRPlayerApp.git
git branch -M main
git push -u origin main

# 3. 等待自動構建完成
# 訪問 GitHub Actions 下載 APK
```

### 方式 2：本地構建

詳細指南：[QUICK_START_GUIDE.md](./QUICK_START_GUIDE.md)

---

## 專案結構

```
VRPlayerApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/vr/player/
│   │   │   └── MainActivity.kt          # 主活動，包含播放邏輯
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml    # 主佈局，包含 PlayerView
│   │   │   ├── values/
│   │   │   │   ├── strings.xml          # 字符串資源
│   │   │   │   └── colors.xml           # 顏色定義
│   │   │   └── mipmap/                  # 應用圖標
│   │   └── AndroidManifest.xml          # 應用清單
│   └── build.gradle                     # 模組構建配置
├── build.gradle                         # 專案構建配置
├── settings.gradle                      # Gradle 設定
└── gradle.properties                    # Gradle 屬性

```

## 📊 技術棧

| 組件 | 版本 | 用途 |
|------|------|------|
| Kotlin | 1.9+ | 應用開發 |
| Android SDK | 34 | 目標平台 |
| ExoPlayer | 2.19.1 | 視頻播放 |
| OkHttp | 4.11.0 | 網絡通訊 |
| OpenGL ES | 2.0 | VR 渲染 |
| Coroutines | 1.7.1 | 非同步處理 |

## 📱 系統要求

- **最低 API**：21 (Android 5.0)
- **目標 API**：34 (Android 14)
- **RAM**：2GB 最低
- **存儲**：500MB

## 🎮 使用指南

### 播放本地影片

1. 點擊 **「選擇影片 (VR Mode)」**
2. 選擇 MP4 影片
3. 自動開始播放

### 播放 Telegram 影片

1. 確保後端服務運行
2. 點擊 **「選擇影片」**
3. 選擇影片開始下載
4. 下載完成後自動播放

### 控制播放

**遙控器**：
- D-Pad Up/Down：調整瞳距
- D-Pad Left/Right：調整音量
- Enter：播放/暫停

**觸摸屏**：
- 單擊：顯示/隱藏控制面板
- 雙擊：播放/暫停
- 左滑：快退 10 秒
- 右滑：快進 10 秒

## 🔄 GitHub Actions 工作流

### 自動觸發

- ✅ 推送到 `main` 或 `master` 分支
- ✅ 提交 Pull Request
- ✅ 手動觸發（Actions 標籤）

### 構建時間

- **首次**：8-10 分鐘
- **後續**：3-5 分鐘（使用緩存）

### 下載 APK

1. 訪問 **Actions** 標籤
2. 點擊最新工作流
3. 向下滾動找 **Artifacts**
4. 下載 **app-debug**

---

## 📚 文檔

| 文檔 | 內容 |
|------|------|
| [GITHUB_QUICK_START.md](./GITHUB_QUICK_START.md) | 3 分鐘 GitHub 快速指南 |
| [GITHUB_BUILD_GUIDE.md](./GITHUB_BUILD_GUIDE.md) | 詳細 GitHub Actions 指南 |
| [QUICK_START_GUIDE.md](./QUICK_START_GUIDE.md) | 本地構建快速指南 |
| [FINAL_DEPLOYMENT_GUIDE.md](./FINAL_DEPLOYMENT_GUIDE.md) | 完整部署指南 |
| [COMPLETE_CODE_SUMMARY.md](./COMPLETE_CODE_SUMMARY.md) | 代碼詳細說明 |

## 🐛 常見問題

### Q: 構建失敗怎麼辦？

**A**: 查看 Actions 日誌：
1. 點擊失敗的工作流
2. 查看 **Build APK** 步驟的錯誤信息

### Q: 應用無法連接後端？

**A**: 檢查 BACKEND_URL：
1. 編輯 TelegramManager.kt
2. 改成您的服務器 IP
3. 重新推送代碼

### Q: 陀螺儀不工作？

**A**: 確認設備支援：
- 使用真實設備（模擬器可能不支援）
- 檢查 HeadTracker.kt 傳感器類型

## 🚀 下一步

1. ✅ 克隆或下載項目
2. ✅ 上傳到 GitHub
3. ✅ 等待自動構建
4. ✅ 下載 APK
5. ✅ 安裝到手機
6. ✅ 測試功能
7. 🎉 享受 VR 體驗！

---

## 📝 版本信息

| 項目 | 版本 |
|------|------|
| 應用版本 | 1.0 |
| 版本代碼 | 1 |
| 最後更新 | 2026-02-10 |

---

## 📄 許可證

MIT License - 詳見 LICENSE 文件

---

**開始您的 VR 之旅吧！** 🚀
