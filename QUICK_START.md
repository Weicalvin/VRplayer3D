# VR Player App - 快速開始指南

## 🎯 用手機操作 - 3 步完成

### 第 1 步：上傳到 GitHub（5 分鐘）

1. 訪問 https://github.com/signup（如果還沒有帳號）
2. 登入 GitHub
3. 點擊右上角 **+** → **New repository**
4. 填寫：
   - Repository name: `VRPlayerApp`
   - Description: `VR Video Player with Head Tracking`
   - 選擇 **Public**
5. 點擊 **Create repository**
6. 點擊 **Add file** → **Upload files**
7. 將 `/home/ubuntu/VRPlayerApp` 中的所有檔案拖入上傳框
8. 點擊 **Commit changes**

### 第 2 步：等待自動編譯（5-10 分鐘）

1. 進入倉庫頁面
2. 點擊 **Actions** 標籤
3. 看到 **Build APK** 工作流執行
4. 等待綠色 ✅ 標記表示編譯完成

### 第 3 步：用手機下載和安裝（5 分鐘）

1. **用手機瀏覽器訪問** GitHub Actions 頁面
   - 網址：`https://github.com/YOUR_USERNAME/VRPlayerApp/actions`
   - 將 `YOUR_USERNAME` 替換為您的 GitHub 用戶名

2. **下載 APK**
   - 點擊最新的 **Build APK** 工作流
   - 向下滾動找到 **Artifacts**
   - 點擊 **app-debug** 下載

3. **安裝 APK**
   - 打開手機 **設置** → **安全**
   - 啟用 **允許安裝未知來源應用**
   - 用檔案管理器打開下載的 `app-debug.apk`
   - 點擊 **安裝**

4. **運行應用**
   - 在手機桌面找到 **VR Player** 應用
   - 點擊打開

---

## 🎮 應用使用說明

### 基本操作

1. **選擇影片**
   - 點擊「選擇影片 (VR Mode)」按鈕
   - 選擇手機中的 MP4 影片檔案

2. **播放控制**
   - **進度條**：拖動調整播放進度
   - **音量**：使用音量滑塊或遙控器調整
   - **播放/暫停**：點擊按鈕或按遙控器確認鍵

3. **VR 功能**
   - **轉動手機**：畫面會跟著旋轉（陀螺儀頭部追蹤）
   - **調整瞳距**：使用遙控器 D-Pad 上/下鍵

### 遙控器按鍵

| 按鍵 | 功能 |
|------|------|
| D-Pad 上 | 增加瞳距 |
| D-Pad 下 | 減少瞳距 |
| D-Pad 左 | 減少音量 |
| D-Pad 右 | 增加音量 |
| D-Pad 中心 | 播放/暫停 |
| 菜單鍵 | 打開設置 |

---

## 📋 完整檔案清單

已上傳到 GitHub 的檔案：

```
VRPlayerApp/
├── .github/
│   └── workflows/
│       └── build-apk.yml          # GitHub Actions 工作流
├── app/
│   ├── src/main/
│   │   ├── java/com/vr/player/
│   │   │   ├── MainActivity.kt     # 主活動
│   │   │   ├── VRRenderer.kt       # VR 渲染引擎
│   │   │   └── HeadTracker.kt      # 陀螺儀追蹤
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml
│   │   │   └── values/
│   │   │       ├── colors.xml
│   │   │       └── strings.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew                         # Gradle Wrapper (Linux/Mac)
├── gradlew.bat                     # Gradle Wrapper (Windows)
├── README.md
├── GITHUB_SETUP.md                 # 詳細指南
└── QUICK_START.md                  # 本檔案
```

---

## ⚡ 快速更新應用

每次修改代碼後，只需：

1. 在 GitHub Web 界面編輯檔案，或
2. 用 Git 推送代碼：
   ```bash
   cd /home/ubuntu/VRPlayerApp
   git add .
   git commit -m "修改說明"
   git push
   ```

3. GitHub Actions 會自動重新編譯
4. 5-10 分鐘後，新的 APK 就可以下載

---

## 🆘 遇到問題？

### APK 無法安裝
- ✅ 確認手機啟用了「未知來源」安裝
- ✅ 確認手機 Android 版本 ≥ 5.0
- ✅ 卸載舊版本後重新安裝

### 編譯失敗
- ✅ 檢查 GitHub Actions 的錯誤日誌
- ✅ 確認所有檔案都已上傳
- ✅ 確認 `build.gradle` 沒有語法錯誤

### 應用閃退
- ✅ 檢查手機是否有足夠的儲存空間
- ✅ 確認手機有陀螺儀傳感器（大多數手機都有）
- ✅ 嘗試重新安裝應用

---

## 📞 支援

- GitHub Issues：在倉庫中提出問題
- Android 文檔：https://developer.android.com
- VR 開發資源：https://www.khronos.org/opengl/

---

## 🎉 祝賀！

您現在已經擁有一個完整的 VR 播放器應用！

**下一步可以嘗試：**
- 修改應用名稱和圖標
- 添加更多 VR 功能
- 發佈到 Google Play Store

祝您使用愉快！🚀

