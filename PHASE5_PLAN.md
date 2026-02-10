# 第五階段：UI 改進與性能優化

## 目標

1. **UI 改進** - 添加進度條、音量控制、設置菜單
2. **性能優化** - 優化幀率和功耗

---

## 第一部分：UI 改進方案

### 1. 進度條 (SeekBar)

**功能**：
- 顯示當前播放進度
- 支援拖動進度條快進/快退
- 顯示當前時間和總時長

**實現方式**：
- 使用 `SeekBar` 控制元件
- 在 `MainActivity` 中綁定 ExoPlayer 的進度更新
- 每 500ms 更新一次進度條

**代碼位置**：
- `activity_main.xml` - 添加 SeekBar 和時間文字
- `MainActivity.kt` - 添加進度監聽和更新邏輯

### 2. 音量控制 (VolumeBar)

**功能**：
- 顯示當前音量級別
- 支援遙控器調整音量
- 實時音量反饋

**實現方式**：
- 使用 `SeekBar` 作為音量控制
- 監聽 D-Pad 左/右鍵調整音量
- 使用 `AudioManager` 控制系統音量

**代碼位置**：
- `activity_main.xml` - 添加音量 SeekBar
- `MainActivity.kt` - 添加音量控制邏輯

### 3. 設置菜單 (Settings)

**功能**：
- 亮度調整
- 字幕設置
- 播放速度控制
- 關於應用

**實現方式**：
- 使用 `BottomSheetDialog` 或 `PopupMenu`
- 遙控器菜單鍵打開設置
- 保存設置到 `SharedPreferences`

**代碼位置**：
- `SettingsFragment.kt` - 新建設置頁面
- `MainActivity.kt` - 添加菜單打開邏輯
- `activity_main.xml` - 添加設置按鈕

---

## 第二部分：性能優化方案

### 1. 幀率優化

**問題**：
- 連續渲染可能導致高幀率，浪費電池
- 不必要的重繪

**解決方案**：
- 使用 `RENDERMODE_WHEN_DIRTY` 而不是 `RENDERMODE_CONTINUOUSLY`
- 只在有新幀時才重繪
- 限制最大幀率為 60fps

**代碼位置**：
- `VRRenderer.kt` - 調整渲染模式
- `MainActivity.kt` - 配置渲染策略

### 2. 功耗優化

**問題**：
- OpenGL 渲染耗電
- 陀螺儀持續工作
- 屏幕常亮

**解決方案**：
- 在播放暫停時停止渲染
- 在應用後台時停止陀螺儀
- 使用 `PowerManager` 管理屏幕喚醒鎖

**代碼位置**：
- `HeadTracker.kt` - 添加暫停/恢復邏輯
- `MainActivity.kt` - 生命週期管理
- `VRRenderer.kt` - 條件渲染

### 3. 內存優化

**問題**：
- 大型影片幀緩存
- 未釋放的資源

**解決方案**：
- 及時釋放 OpenGL 資源
- 使用 `WeakReference` 管理引用
- 監控內存使用

**代碼位置**：
- `VRRenderer.kt` - 資源清理
- `MainActivity.kt` - 生命週期清理

---

## 實現步驟

### 步驟 1：更新 activity_main.xml
- 添加進度條 (SeekBar)
- 添加音量控制 (SeekBar)
- 添加時間顯示 (TextView)
- 添加設置按鈕

### 步驟 2：新建 SettingsFragment.kt
- 設置菜單頁面
- 各項設置的邏輯

### 步驟 3：更新 MainActivity.kt
- 進度條監聽和更新
- 音量控制邏輯
- 設置菜單打開
- 生命週期優化

### 步驟 4：更新 VRRenderer.kt
- 渲染模式調整
- 條件渲染邏輯
- 資源清理

### 步驟 5：更新 HeadTracker.kt
- 暫停/恢復邏輯
- 功耗優化

---

## 預期成果

### UI 改進後
- ✨ 進度條顯示播放進度
- ✨ 音量控制可調整音量
- ✨ 設置菜單提供更多選項
- ✨ 更專業的播放器界面

### 性能優化後
- ⚡ 幀率穩定在 60fps
- ⚡ 功耗降低 30-50%
- ⚡ 內存使用更穩定
- ⚡ 應用更流暢

---

## 技術棧

| 組件 | 技術 | 用途 |
|------|------|------|
| UI 控制 | SeekBar, TextView, Button | 進度條、音量、時間顯示 |
| 設置存儲 | SharedPreferences | 保存用戶設置 |
| 音量控制 | AudioManager | 系統音量管理 |
| 性能監控 | FrameMetrics | 幀率監控 |
| 功耗管理 | PowerManager | 屏幕喚醒鎖 |

