# 🧹 清理 GitHub 工作流錯誤

## 問題

您的 GitHub 倉庫中有一個錯誤的文件：
- **文件**：`.github/workflows/blank.yml`
- **問題**：包含 Gradle 配置代碼而不是工作流 YAML
- **結果**：GitHub Actions 無法解析

---

## ✅ 解決方案

### 第 1 步：刪除錯誤的文件

在您的本地倉庫中執行：

```bash
cd VRPlayerApp

# 刪除錯誤的工作流文件
rm .github/workflows/blank.yml

# 確認只有 build-apk.yml
ls -la .github/workflows/
# 應該只看到 build-apk.yml
```

### 第 2 步：提交並推送

```bash
# 提交刪除
git add .github/workflows/
git commit -m "Remove: Delete incorrect blank.yml workflow"
git push origin main
```

### 第 3 步：驗證

1. 訪問 GitHub 倉庫
2. 點擊 **Actions** 標籤
3. 應該看到 ✅ 綠色的 **Build APK** 工作流
4. 不應該看到紅色的錯誤

---

## 📋 檢查清單

- [ ] 刪除了 `.github/workflows/blank.yml`
- [ ] 只保留 `.github/workflows/build-apk.yml`
- [ ] 提交並推送到 GitHub
- [ ] GitHub Actions 不再顯示錯誤
- [ ] 可以手動觸發 **Build APK** 工作流

---

## 🔍 驗證正確的工作流文件

### 正確的文件結構

```
.github/
└── workflows/
    └── build-apk.yml          ✅ 正確
```

### 錯誤的文件結構

```
.github/
└── workflows/
    ├── build-apk.yml          ✅ 正確
    └── blank.yml              ❌ 需要刪除
```

---

## 🚀 完成後的步驟

### 1. 手動觸發構建

1. GitHub → **Actions** 標籤
2. 點擊 **Build APK** 工作流
3. 點擊 **Run workflow** → **Run workflow**

### 2. 等待完成

- 首次：8-10 分鐘
- 後續：3-5 分鐘

### 3. 檢查 Artifacts

應該看到：
```
Artifacts
Produced during runtime
Name          Size      
app-debug     25.3 MB   
```

---

## 💡 如何避免此問題

### 不要做

❌ 在工作流文件中粘貼 Gradle 配置  
❌ 在工作流文件中粘貼 Java 代碼  
❌ 在工作流文件中粘貼 XML 配置  

### 應該做

✅ 工作流文件只包含 YAML 代碼  
✅ 使用 `.github/workflows/build-apk.yml` 作為工作流  
✅ 其他配置放在相應的文件中  

---

## 📝 正確的工作流文件格式

```yaml
name: Build APK

on:
  push:
    branches:
      - main
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      # ... 其他步驟
```

---

## ✨ 完成！

刪除錯誤文件後，GitHub Actions 應該正常工作。

**現在重新觸發構建試試！** 🚀
