# ⚡ GitHub 自動構建 - 3 分鐘快速指南

## 🚀 只需 3 步！

### 第 1 步：創建 GitHub 倉庫（1 分鐘）

1. 訪問 https://github.com/new
2. 填寫：
   - **Repository name**: `VRPlayerApp`
   - **Visibility**: Public
3. 點擊 **Create repository**

---

### 第 2 步：上傳代碼（1 分鐘）

在您的電腦上打開命令行，進入 VRPlayerApp 目錄：

```bash
cd VRPlayerApp

# 初始化並推送
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/VRPlayerApp.git
git push -u origin main
```

**替換 `YOUR_USERNAME` 為您的 GitHub 用戶名**

---

### 第 3 步：自動構建（1 分鐘）

1. 訪問 GitHub 倉庫
2. 點擊 **Actions** 標籤
3. 等待 **Build APK** 完成（3-5 分鐘）
4. 完成後點擊工作流
5. 向下滾動找到 **Artifacts**
6. 下載 **app-debug** ZIP 文件

---

## 📥 下載和安裝 APK

### 解壓 APK

```bash
# 解壓下載的 ZIP 文件
unzip app-debug.zip
# 得到 app-debug.apk
```

### 安裝到手機

```bash
# 連接手機
adb devices

# 安裝
adb install app-debug.apk
```

---

## 🔄 後續構建

每次修改代碼並推送時，GitHub 會自動構建：

```bash
# 修改代碼...

# 提交並推送
git add .
git commit -m "Update"
git push origin main

# ✅ 自動構建開始！
# 訪問 Actions 標籤下載新的 APK
```

---

## 📊 查看構建狀態

1. GitHub 倉庫 → **Actions** 標籤
2. 查看工作流列表
3. 點擊查看詳細日誌

**狀態**：
- 🟢 成功
- 🔴 失敗
- 🟡 進行中

---

## ⚙️ 修改後端 URL

如果需要修改後端地址：

1. 編輯 `app/src/main/java/com/vr/player/TelegramManager.kt`
2. 改第 20 行：
   ```kotlin
   private const val BACKEND_URL = "http://YOUR_IP:8000"
   ```
3. 提交並推送：
   ```bash
   git add .
   git commit -m "Update backend URL"
   git push origin main
   ```
4. GitHub 會自動用新配置重新構建！

---

## 🎯 完整流程

```
1. 創建 GitHub 倉庫
   ↓
2. 上傳代碼
   ↓
3. GitHub Actions 自動構建
   ↓
4. 下載 APK
   ↓
5. 安裝到手機
   ↓
6. 測試應用
```

---

## 💡 優勢

✅ **無需本地 Android Studio**  
✅ **自動構建**  
✅ **免費（GitHub 免費用戶）**  
✅ **每次推送自動重建**  
✅ **直接下載 APK**  

---

## 🐛 常見問題

**Q: 構建失敗了？**  
A: 點擊 Actions → 查看錯誤日誌

**Q: 多久構建完成？**  
A: 首次 8-10 分鐘，後續 3-5 分鐘

**Q: 如何修改構建配置？**  
A: 編輯 `.github/workflows/build-apk.yml`

---

## 📚 更多信息

詳細指南：`GITHUB_BUILD_GUIDE.md`

---

**就這麼簡單！** 🎉

現在您可以完全在 GitHub 上構建 APK！
