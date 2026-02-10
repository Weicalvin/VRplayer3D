# GitHub 自動編譯 APK 指南

本指南將幫助您將 VR Player 專案上傳到 GitHub，並自動生成 APK 檔案，然後用手機下載安裝。

---

## 📋 前置需求

- GitHub 帳號（免費註冊：https://github.com/signup）
- 手機（用於下載和安裝 APK）
- Git 工具（可選，但推薦）

---

## 🚀 第一步：建立 GitHub 倉庫

### 方法 1：使用 GitHub Web 界面（最簡單）

1. **登入 GitHub**
   - 訪問 https://github.com
   - 用您的帳號登入

2. **建立新倉庫**
   - 點擊右上角 **+** → **New repository**
   - 填寫倉庫名稱：`VRPlayerApp`
   - 描述：`VR Video Player with Head Tracking`
   - 選擇 **Public**（這樣可以免費使用 GitHub Actions）
   - 點擊 **Create repository**

3. **上傳檔案**
   - 在倉庫頁面，點擊 **Add file** → **Upload files**
   - 將 `/home/ubuntu/VRPlayerApp` 中的所有檔案拖入上傳框
   - 或者逐個上傳以下關鍵檔案：
     - `app/` 資料夾（整個）
     - `gradle/` 資料夾（整個）
     - `.github/workflows/` 資料夾（整個）
     - `build.gradle`
     - `settings.gradle`
     - `gradle.properties`
     - `gradlew`
     - `gradlew.bat`
   - 點擊 **Commit changes**

### 方法 2：使用 Git 命令行

如果您已安裝 Git，可以在終端執行：

```bash
cd /home/ubuntu/VRPlayerApp
git init
git add .
git commit -m "Initial commit: VR Player App"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/VRPlayerApp.git
git push -u origin main
```

將 `YOUR_USERNAME` 替換為您的 GitHub 用戶名。

---

## ⚙️ 第二步：啟用 GitHub Actions

1. **進入倉庫**
   - 訪問 https://github.com/YOUR_USERNAME/VRPlayerApp

2. **檢查 Actions**
   - 點擊 **Actions** 標籤
   - 您應該看到 **Build APK** 工作流
   - 如果還沒有自動執行，點擊 **Run workflow**

3. **等待編譯完成**
   - 編譯通常需要 5-10 分鐘
   - 您可以看到實時進度

---

## 📥 第三步：下載 APK

### 方法 1：從 Artifacts 下載（推薦）

1. **進入 Actions**
   - 點擊倉庫的 **Actions** 標籤

2. **選擇最新的 Build**
   - 點擊最新的 **Build APK** 執行記錄

3. **下載 APK**
   - 向下滾動到 **Artifacts** 部分
   - 點擊 **app-debug** 下載 ZIP 檔案
   - 解壓後得到 `app-debug.apk`

### 方法 2：從 Release 下載

如果您標記了版本（Tag），APK 會自動出現在 Release 頁面：

1. **建立 Release**
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

2. **進入 Release 頁面**
   - 點擊倉庫的 **Releases** 標籤
   - 點擊最新的 Release
   - 下載 `app-debug.apk`

---

## 📱 第四步：在手機上安裝 APK

### 在 Android 手機上安裝

1. **下載 APK**
   - 用手機瀏覽器訪問 GitHub
   - 或者用電腦下載後傳輸到手機

2. **啟用未知來源安裝**
   - 打開手機 **設置**
   - 進入 **安全** 或 **應用權限**
   - 啟用 **允許安裝未知來源應用**（或 **允許來自此來源的應用**）

3. **安裝 APK**
   - 用檔案管理器找到 `app-debug.apk`
   - 點擊安裝
   - 等待安裝完成

4. **運行應用**
   - 在手機桌面找到 **VR Player** 應用
   - 點擊打開

---

## 🔄 第五步：更新應用

每當您推送代碼到 GitHub 時，GitHub Actions 會自動編譯新的 APK：

1. **修改代碼**
   - 在本地修改檔案
   - 提交並推送：
     ```bash
     git add .
     git commit -m "修復 bug 或添加功能"
     git push
     ```

2. **自動編譯**
   - GitHub Actions 會自動執行
   - 新的 APK 會在 5-10 分鐘後生成

3. **下載新版本**
   - 進入 Actions 頁面
   - 下載最新的 APK
   - 在手機上卸載舊版本並安裝新版本

---

## 🐛 常見問題

### Q1：編譯失敗怎麼辦？

**A：** 檢查 GitHub Actions 的錯誤日誌：
1. 進入 **Actions** 標籤
2. 點擊失敗的工作流
3. 點擊 **Build APK** 步驟查看詳細錯誤信息
4. 常見原因：
   - `build.gradle` 配置錯誤
   - 缺少 Android SDK 版本
   - 代碼編譯錯誤

### Q2：APK 無法安裝？

**A：** 檢查以下項目：
- 手機是否啟用了「未知來源」安裝
- 手機是否已有舊版本應用（需要卸載）
- 手機 Android 版本是否符合要求（最低 API 21）

### Q3：如何修改應用名稱或圖標？

**A：** 編輯以下檔案：
- **應用名稱**：`app/src/main/AndroidManifest.xml` 中的 `android:label`
- **應用圖標**：`app/src/main/res/drawable/` 中的圖片檔案
- 修改後推送到 GitHub，GitHub Actions 會自動重新編譯

### Q4：如何簽名 APK？

**A：** 目前生成的是 Debug APK（開發版本）。如果要發佈到 Google Play，需要：
1. 建立簽名金鑰
2. 在 `build.gradle` 中配置簽名
3. 修改工作流為 `assembleRelease`

詳細步驟可參考 [Android 官方文檔](https://developer.android.com/studio/publish/app-signing)。

---

## 📊 工作流說明

`.github/workflows/build-apk.yml` 檔案定義了自動編譯流程：

1. **觸發條件**
   - 推送到 `main` 或 `master` 分支
   - 提交 Pull Request
   - 手動觸發（點擊 **Run workflow**）

2. **編譯步驟**
   - 檢出代碼
   - 設定 Java 環境
   - 設定 Android SDK
   - 執行 `./gradlew assembleDebug`
   - 上傳 APK 到 Artifacts

3. **輸出**
   - APK 檔案：`app/build/outputs/apk/debug/app-debug.apk`
   - 保留 30 天

---

## 🎉 完成！

現在您已經可以：
1. ✅ 在 GitHub 上管理代碼
2. ✅ 自動編譯 APK
3. ✅ 用手機下載和安裝應用

祝您使用愉快！如有任何問題，歡迎在 GitHub Issues 中提出。

---

## 📚 相關資源

- [GitHub 官方文檔](https://docs.github.com)
- [GitHub Actions 文檔](https://docs.github.com/en/actions)
- [Android 官方文檔](https://developer.android.com)
- [Gradle 官方文檔](https://gradle.org)

