# 🔧 修復 GitHub Actions 工作流

## 問題診斷

您看到的 `github-pages` artifact 表示：
- ❌ 工作流沒有生成 APK
- ❌ 可能是 Gradle 構建失敗
- ❌ 需要檢查構建日誌

---

## 🔍 第 1 步：查看構建日誌

### 查看詳細錯誤

1. 訪問 GitHub 倉庫 → **Actions** 標籤
2. 點擊最新的工作流運行
3. 點擊 **Build APK with Gradle** 步驟
4. 查看紅色的錯誤信息

### 常見錯誤

| 錯誤 | 原因 | 解決方案 |
|------|------|--------|
| `gradlew: command not found` | 權限問題 | 檢查 chmod +x gradlew |
| `SDK not found` | SDK 未安裝 | 等待 Android SDK 安裝 |
| `Gradle sync failed` | build.gradle 錯誤 | 檢查依賴配置 |
| `Java version mismatch` | Java 版本不對 | 確認使用 Java 11 |

---

## ✅ 第 2 步：使用修復後的工作流

我為您準備了一個改進的工作流配置。請按以下步驟操作：

### 2.1 編輯工作流文件

編輯 `.github/workflows/build-apk.yml`，替換為以下內容：

```yaml
name: Build APK

on:
  push:
    branches:
      - main
      - master
      - develop
  pull_request:
    branches:
      - main
      - master
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    
    permissions:
      contents: read
      actions: read

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 11
        uses: actions/setup-java@v4
        with:
          java-version: '11'
          distribution: 'temurin'
          cache: gradle

      - name: Set up Android SDK
        uses: android-actions/setup-android@v3
        with:
          api-level: 34
          ndk-version: '25.2.9519653'

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Build APK with Gradle
        run: |
          ./gradlew --version
          ./gradlew clean assembleDebug --stacktrace

      - name: Check APK exists
        run: |
          if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
            echo "✅ APK 已生成"
            ls -lh app/build/outputs/apk/debug/app-debug.apk
          else
            echo "❌ APK 未找到"
            find app/build -name "*.apk" -type f
            exit 1
          fi

      - name: Upload APK as Artifact
        if: success()
        uses: actions/upload-artifact@v4
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/app-debug.apk
          retention-days: 30

      - name: Create Release
        if: startsWith(github.ref, 'refs/tags/')
        uses: softprops/action-gh-release@v1
        with:
          files: app/build/outputs/apk/debug/app-debug.apk
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

      - name: Upload build logs on failure
        if: failure()
        uses: actions/upload-artifact@v4
        with:
          name: build-logs
          path: |
            app/build/outputs/
            build/
          retention-days: 7
```

### 2.2 提交更新

```bash
cd VRPlayerApp

# 編輯工作流文件後
git add .github/workflows/build-apk.yml
git commit -m "Fix: Improve GitHub Actions workflow"
git push origin main
```

---

## 🧪 第 3 步：手動觸發構建

1. 訪問 GitHub 倉庫
2. 點擊 **Actions** 標籤
3. 點擊左側 **Build APK** 工作流
4. 點擊 **Run workflow** → **Run workflow**
5. 等待完成

---

## 📋 檢查清單

### 構建前檢查

- [ ] `build.gradle` 配置正確
- [ ] `AndroidManifest.xml` 有效
- [ ] 所有依賴都在 `build.gradle` 中
- [ ] `gradlew` 文件存在且可執行

### 構建後檢查

- [ ] 工作流顯示 ✅ 成功
- [ ] **Artifacts** 中有 `app-debug` 文件
- [ ] APK 文件大小 > 1MB（正常 APK 應該 10-50MB）
- [ ] 沒有 `github-pages` artifact

---

## 🐛 常見問題排查

### Q1：還是出現 `github-pages` artifact？

**A**: 這表示構建失敗。檢查：

1. **查看構建日誌**
   - Actions → 工作流 → Build APK 步驟
   - 查看紅色錯誤信息

2. **檢查 build.gradle**
   ```gradle
   android {
       compileSdk 34  // 確保是 34
       
       defaultConfig {
           minSdk 21
           targetSdk 34
       }
   }
   ```

3. **檢查依賴**
   ```gradle
   dependencies {
       implementation 'com.google.android.exoplayer:exoplayer:2.19.1'
       // 確保所有依賴都列出
   }
   ```

### Q2：構建超時？

**A**: 首次構建需要 8-10 分鐘下載 SDK。如果超過 15 分鐘，可能是網絡問題。

### Q3：Java 版本錯誤？

**A**: 確保工作流使用 Java 11：
```yaml
- uses: actions/setup-java@v4
  with:
    java-version: '11'
```

### Q4：gradlew 權限錯誤？

**A**: 確保有 chmod 步驟：
```yaml
- run: chmod +x gradlew
```

---

## 📊 正確的 Artifacts 應該是什麼樣？

### ✅ 正確

```
Artifacts
Produced during runtime
Name          Size      Digest
app-debug     25.3 MB   sha256:...
```

### ❌ 錯誤

```
Artifacts
Produced during runtime
Name              Size      Digest
github-pages      84.1 KB   sha256:...
```

---

## 🔄 完整修復流程

### 第 1 步：更新工作流文件

```bash
# 編輯 .github/workflows/build-apk.yml
# 使用上面提供的改進版本
```

### 第 2 步：提交並推送

```bash
git add .github/workflows/build-apk.yml
git commit -m "Fix: Update GitHub Actions workflow"
git push origin main
```

### 第 3 步：手動觸發

1. GitHub → Actions → Build APK
2. Run workflow → Run workflow

### 第 4 步：查看結果

1. 等待構建完成（3-5 分鐘）
2. 檢查 Artifacts
3. 應該看到 `app-debug` 文件（20-50MB）

### 第 5 步：下載 APK

1. 點擊工作流運行
2. 向下滾動找 **Artifacts**
3. 下載 `app-debug` ZIP 文件
4. 解壓得到 `app-debug.apk`

---

## 💡 調試技巧

### 添加詳細日誌

編輯工作流，在構建步驟添加：

```yaml
- name: Build APK with Gradle
  run: |
    ./gradlew --version
    ./gradlew clean assembleDebug --stacktrace --debug
```

### 查看生成的文件

```yaml
- name: List build outputs
  if: always()
  run: |
    echo "=== Build outputs ==="
    find app/build/outputs -type f -name "*.apk" 2>/dev/null || echo "No APK found"
    echo "=== Full build directory ==="
    ls -la app/build/outputs/ 2>/dev/null || echo "No outputs directory"
```

---

## 📞 進一步支援

如果問題仍未解決，請：

1. **檢查 build.gradle**
   - 確保所有依賴都正確
   - 確保 compileSdk 是 34

2. **檢查 AndroidManifest.xml**
   - 確保沒有語法錯誤
   - 確保所有權限都列出

3. **查看完整日誌**
   - Actions → 工作流 → 每個步驟的日誌

4. **本地測試**
   ```bash
   cd VRPlayerApp
   ./gradlew clean assembleDebug
   ```

---

## ✅ 驗證成功

構建成功的標誌：

- ✅ 工作流顯示綠色 ✅
- ✅ Artifacts 中有 `app-debug` 文件
- ✅ APK 文件大小 > 10MB
- ✅ 沒有 `github-pages` artifact
- ✅ 可以下載並安裝 APK

---

**現在重新嘗試構建，應該能成功！** 🚀

如有問題，查看上面的排查指南。
