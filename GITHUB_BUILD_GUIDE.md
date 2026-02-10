# 🚀 GitHub Actions 自動構建指南

## 📋 概述

使用 GitHub Actions 自動構建 APK，無需本地安裝 Android Studio！

**優勢**：
- ✅ 無需本地開發環境
- ✅ 自動構建和測試
- ✅ 直接下載 APK
- ✅ 自動發佈到 Release
- ✅ 完全免費（GitHub 免費用戶每月 2,000 分鐘）

---

## 🔧 第 1 步：上傳到 GitHub

### 1.1 創建 GitHub 倉庫

1. 訪問 https://github.com/new
2. 填寫倉庫信息：
   - **Repository name**: `VRPlayerApp`
   - **Description**: `VR Telegram Player for Android`
   - **Visibility**: Public（推薦）或 Private
3. 點擊 **Create repository**

### 1.2 上傳代碼

```bash
cd VRPlayerApp

# 初始化 Git 倉庫
git init
git add .
git commit -m "Initial commit: VR Telegram Player"

# 添加遠程倉庫
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/VRPlayerApp.git

# 推送到 GitHub
git push -u origin main
```

**替換 `YOUR_USERNAME` 為您的 GitHub 用戶名**

---

## 🤖 第 2 步：GitHub Actions 工作流

### 2.1 工作流文件已存在

文件位置：`.github/workflows/build-apk.yml`

該文件已包含完整的構建配置：
- ✅ 設置 JDK 11
- ✅ 設置 Android SDK
- ✅ 編譯 APK
- ✅ 上傳 Artifacts
- ✅ 自動發佈 Release

### 2.2 工作流說明

```yaml
name: Build APK

on:
  push:           # 推送代碼時觸發
    branches:
      - main
      - master
  pull_request:   # 提交 PR 時觸發
    branches:
      - main
      - master
  workflow_dispatch:  # 手動觸發

jobs:
  build:
    runs-on: ubuntu-latest  # 在 Ubuntu 上運行
    
    steps:
      # 1. 檢出代碼
      - uses: actions/checkout@v3
      
      # 2. 設置 Java 環境
      - uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
      
      # 3. 設置 Android SDK
      - uses: android-actions/setup-android@v2
      
      # 4. 編譯 APK
      - run: ./gradlew assembleDebug
      
      # 5. 上傳 Artifacts
      - uses: actions/upload-artifact@v3
```

---

## 🎯 第 3 步：自動構建

### 3.1 推送代碼觸發構建

```bash
# 修改任何文件
echo "# VR Player" > README.md

# 提交並推送
git add .
git commit -m "Update README"
git push origin main
```

構建會自動開始！

### 3.2 手動觸發構建

1. 訪問 GitHub 倉庫
2. 點擊 **Actions** 標籤
3. 選擇 **Build APK** 工作流
4. 點擊 **Run workflow** → **Run workflow**

### 3.3 查看構建結果

1. 點擊 **Actions** 標籤
2. 點擊最新的工作流運行
3. 查看 **Build APK** 步驟的日誌
4. 如果成功，會看到 ✅ 標記

---

## 📥 第 4 步：下載 APK

### 4.1 從 Artifacts 下載

1. 訪問 GitHub 倉庫 → **Actions**
2. 點擊最新的工作流運行
3. 向下滾動找到 **Artifacts** 部分
4. 點擊 **app-debug** 下載 ZIP 文件
5. 解壓得到 `app-debug.apk`

### 4.2 從 Release 下載（標籤發佈）

```bash
# 創建標籤
git tag v1.0.0
git push origin v1.0.0
```

APK 會自動發佈到 Release 頁面！

1. 訪問 GitHub 倉庫 → **Releases**
2. 點擊最新 Release
3. 下載 `app-debug.apk`

---

## 💻 第 5 步：安裝 APK

### 5.1 使用 ADB 安裝

```bash
# 連接 Android 設備
adb devices

# 安裝 APK
adb install app-debug.apk

# 運行應用
adb shell am start -n com.vr.player/.MainActivity
```

### 5.2 手動安裝

1. 將 `app-debug.apk` 複製到手機
2. 打開檔案管理器
3. 點擊 APK 檔案
4. 按照提示安裝

---

## 🔄 工作流詳解

### 構建流程

```
1. 代碼推送到 GitHub
   ↓
2. GitHub Actions 觸發
   ↓
3. 創建 Ubuntu 虛擬機
   ↓
4. 安裝 JDK 11
   ↓
5. 安裝 Android SDK
   ↓
6. 執行 ./gradlew assembleDebug
   ↓
7. 生成 app-debug.apk
   ↓
8. 上傳到 Artifacts
   ↓
9. 完成！✅
```

### 構建時間

- **首次構建**：8-10 分鐘（需要下載 SDK）
- **後續構建**：3-5 分鐘（使用緩存）

---

## 📊 工作流狀態

### 查看構建狀態

1. 訪問 GitHub 倉庫
2. 點擊 **Actions** 標籤
3. 查看工作流列表

**狀態指示**：
- 🟢 成功（✅）
- 🔴 失敗（❌）
- 🟡 進行中（⏳）

### 添加狀態徽章

在 README.md 中添加：

```markdown
![Build Status](https://github.com/YOUR_USERNAME/VRPlayerApp/workflows/Build%20APK/badge.svg)
```

---

## 🐛 常見問題

### Q1：構建失敗怎麼辦？

**檢查日誌**：
1. 點擊失敗的工作流
2. 點擊 **Build APK** 步驟
3. 查看錯誤信息

**常見錯誤**：
- `Gradle sync failed` → 檢查 build.gradle
- `SDK not found` → 等待 SDK 下載完成
- `Permission denied` → 檢查 gradlew 權限

### Q2：如何修改構建配置？

編輯 `.github/workflows/build-apk.yml`：

```yaml
# 修改構建類型（Debug → Release）
- run: ./gradlew assembleRelease

# 修改 Java 版本
java-version: '17'

# 添加環境變量
env:
  BACKEND_URL: http://your-server:8000
```

### Q3：如何簽名 Release APK？

添加簽名配置到 `build.gradle`：

```gradle
android {
    signingConfigs {
        release {
            storeFile file("keystore.jks")
            storePassword System.getenv("KEYSTORE_PASSWORD")
            keyAlias System.getenv("KEY_ALIAS")
            keyPassword System.getenv("KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

### Q4：如何自動發佈到 Google Play？

需要額外配置：

```yaml
- name: Deploy to Google Play
  uses: r0adkll/upload-google-play@v1
  with:
    serviceAccountJson: ${{ secrets.PLAY_STORE_SERVICE_ACCOUNT }}
    packageName: com.vr.player
    releaseFiles: app/build/outputs/apk/release/app-release.apk
    track: internal
```

---

## 🔐 安全性

### 管理 Secrets

如果需要存儲敏感信息（如簽名密鑰、API 密鑰）：

1. 訪問 GitHub 倉庫 → **Settings** → **Secrets and variables** → **Actions**
2. 點擊 **New repository secret**
3. 添加 Secret（例如 `KEYSTORE_PASSWORD`）
4. 在工作流中使用：`${{ secrets.KEYSTORE_PASSWORD }}`

### 環境變量

在工作流中設置環境變量：

```yaml
env:
  BACKEND_URL: http://192.168.1.100:8000
  VERSION_NAME: 1.0.0
```

---

## 📈 進階配置

### 4.1 多個工作流

創建多個工作流文件：

```
.github/workflows/
├── build-apk.yml          # Debug 構建
├── build-release.yml      # Release 構建
└── test.yml              # 單元測試
```

### 4.2 構建矩陣

同時構建多個配置：

```yaml
strategy:
  matrix:
    java-version: [11, 17]
    android-api: [21, 34]
```

### 4.3 構建緩存

加速構建：

```yaml
- name: Cache Gradle
  uses: actions/cache@v3
  with:
    path: ~/.gradle/caches
    key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle') }}
```

---

## 📚 完整工作流示例

### Debug 構建 + 自動發佈

```yaml
name: Build and Release

on:
  push:
    tags:
      - 'v*'

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
      
      - uses: android-actions/setup-android@v2
      
      - run: chmod +x gradlew
      
      - run: ./gradlew assembleDebug
      
      - name: Create Release
        uses: softprops/action-gh-release@v1
        with:
          files: app/build/outputs/apk/debug/app-debug.apk
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

---

## 🎯 完整流程總結

### 第 1 次設置（5 分鐘）

```bash
# 1. 創建 GitHub 倉庫
# 訪問 https://github.com/new

# 2. 上傳代碼
cd VRPlayerApp
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/VRPlayerApp.git
git branch -M main
git push -u origin main
```

### 後續構建（自動）

```bash
# 修改代碼
echo "# Updated" >> README.md

# 提交並推送
git add .
git commit -m "Update"
git push origin main

# ✅ GitHub Actions 自動構建！
# 訪問 GitHub Actions 下載 APK
```

---

## 📞 支援

- **GitHub Actions 文檔**：https://docs.github.com/en/actions
- **Android Gradle 文檔**：https://developer.android.com/build
- **工作流語法**：https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions

---

## ✅ 檢查清單

- [ ] 創建 GitHub 倉庫
- [ ] 上傳代碼到 GitHub
- [ ] 驗證 `.github/workflows/build-apk.yml` 存在
- [ ] 推送代碼觸發構建
- [ ] 查看 Actions 標籤確認構建成功
- [ ] 下載 APK 文件
- [ ] 安裝到 Android 設備
- [ ] 測試應用功能

---

**現在您可以完全在 GitHub 上構建 APK，無需本地開發環境！** 🎉

只需推送代碼，GitHub Actions 會自動為您構建！
