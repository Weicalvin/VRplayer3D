# VR Player + Telegram 整合方案

## 🎯 項目目標

建立一個應用，整合以下功能：
1. ✅ **VR 播放器** - 雙眼渲染、光學校正、陀螺儀追蹤
2. ✅ **Telegram 影片播放** - 從 Telegram 頻道/群組播放影片
3. ✅ **Telegram 影片下載** - 下載 Telegram 中的影片到本地
4. ✅ **觸摸屏控制** - 手勢識別、滑動控制
5. ✅ **UI 改進** - 進度條、音量控制、設置菜單

---

## 📋 技術方案

### 方案 A：使用 Telegram Bot API（推薦）⭐⭐⭐⭐⭐

**優點**：
- ✅ 官方支援
- ✅ 安全可靠
- ✅ 易於實現
- ✅ 無需反編譯

**實現方式**：
1. 建立 Telegram Bot（通過 BotFather）
2. 使用 Bot API 獲取影片鏈接
3. 使用 ExoPlayer 播放
4. 使用 HTTP 下載影片

**限制**：
- 需要 Bot Token
- 只能訪問公開頻道
- 需要用戶授權

---

### 方案 B：使用 TDLib（Telegram Desktop Library）⭐⭐⭐

**優點**：
- ✅ 完整的 Telegram 功能
- ✅ 支援私人頻道
- ✅ 官方支援

**實現方式**：
1. 集成 TDLib（C++ 庫）
2. 通過 JNI 調用
3. 實現登入和影片下載

**限制**：
- 複雜度高
- 需要 C++ 編譯
- 檔案大小大

---

### 方案 C：使用 Pyrogram（Python 庫）⭐⭐⭐

**優點**：
- ✅ 功能完整
- ✅ 易於使用
- ✅ 支援私人頻道

**實現方式**：
1. 建立 Python 後端服務
2. Android 應用通過 HTTP API 調用
3. 後端負責 Telegram 認證和下載

**限制**：
- 需要後端服務
- 網路延遲
- 額外的伺服器成本

---

## 🏗️ 推薦架構

### 架構圖

```
┌─────────────────────────────────────────────────┐
│           Android VR Player App                  │
├─────────────────────────────────────────────────┤
│                                                  │
│  ┌──────────────────────────────────────────┐  │
│  │     UI Layer (觸摸控制)                   │  │
│  │  - 進度條、音量、設置                     │  │
│  │  - 手勢識別（滑動、點擊）                 │  │
│  └──────────────────────────────────────────┘  │
│                     ↓                           │
│  ┌──────────────────────────────────────────┐  │
│  │   VR Rendering Layer                     │  │
│  │  - OpenGL 雙眼渲染                       │  │
│  │  - 光學校正                              │  │
│  │  - 陀螺儀追蹤                            │  │
│  └──────────────────────────────────────────┘  │
│                     ↓                           │
│  ┌──────────────────────────────────────────┐  │
│  │   Media Layer (ExoPlayer)                │  │
│  │  - 本地影片播放                          │  │
│  │  - Telegram 影片播放                     │  │
│  └──────────────────────────────────────────┘  │
│                     ↓                           │
│  ┌──────────────────────────────────────────┐  │
│  │   Telegram Integration Layer             │  │
│  │  - Bot API 調用                          │  │
│  │  - 影片鏈接獲取                          │  │
│  │  - 下載管理                              │  │
│  └──────────────────────────────────────────┘  │
│                     ↓                           │
└─────────────────────────────────────────────────┘
                     ↓
    ┌───────────────────────────────┐
    │   Telegram Bot API            │
    │   (官方 Telegram 服務)        │
    └───────────────────────────────┘
```

---

## 📝 實現步驟

### 第一步：設定 Telegram Bot

1. **建立 Bot**
   - 訪問 https://t.me/BotFather
   - 發送 `/newbot`
   - 按照提示建立 Bot
   - 記錄 Bot Token

2. **配置 Bot**
   - 設定 Bot 描述
   - 設定 Bot 命令
   - 啟用內聯模式

### 第二步：實現 Telegram 功能模塊

#### TelegramManager.kt
```kotlin
class TelegramManager(val botToken: String) {
    
    // 獲取頻道影片列表
    suspend fun getChannelVideos(channelId: String): List<TelegramVideo> {
        // 使用 Bot API 獲取影片
    }
    
    // 獲取影片下載鏈接
    suspend fun getVideoUrl(videoId: String): String {
        // 返回影片直連 URL
    }
    
    // 下載影片
    suspend fun downloadVideo(videoUrl: String, savePath: String) {
        // 使用 OkHttp 下載
    }
}
```

#### TelegramVideo.kt
```kotlin
data class TelegramVideo(
    val id: String,
    val title: String,
    val duration: Long,
    val thumbnailUrl: String,
    val videoUrl: String,
    val fileSize: Long,
    val uploadDate: Long
)
```

### 第三步：實現觸摸控制

#### TouchController.kt
```kotlin
class TouchController(val glSurfaceView: GLSurfaceView) {
    
    // 識別手勢
    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> onTouchDown(event)
            MotionEvent.ACTION_MOVE -> onTouchMove(event)
            MotionEvent.ACTION_UP -> onTouchUp(event)
        }
        return true
    }
    
    // 滑動控制（調整進度）
    private fun onTouchMove(event: MotionEvent) {
        val deltaX = event.x - lastX
        if (abs(deltaX) > SWIPE_THRESHOLD) {
            if (deltaX > 0) {
                // 向右滑動 - 快進
                seekForward()
            } else {
                // 向左滑動 - 快退
                seekBackward()
            }
        }
    }
    
    // 雙擊控制（播放/暫停）
    private fun onTouchDown(event: MotionEvent) {
        if (isDoubleTap(event)) {
            togglePlayPause()
        }
    }
}
```

### 第四步：整合所有功能

#### MainActivity.kt（更新）
```kotlin
class MainActivity : AppCompatActivity() {
    
    private lateinit var telegramManager: TelegramManager
    private lateinit var touchController: TouchController
    private lateinit var vrRenderer: VRRenderer
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化 Telegram 管理器
        telegramManager = TelegramManager(BOT_TOKEN)
        
        // 初始化觸摸控制
        touchController = TouchController(glSurfaceView)
        
        // 初始化 VR 渲染器
        vrRenderer = VRRenderer()
        
        // 設定事件監聽
        setupEventListeners()
    }
    
    // 播放 Telegram 影片
    private fun playTelegramVideo(video: TelegramVideo) {
        lifecycleScope.launch {
            val videoUrl = telegramManager.getVideoUrl(video.id)
            player.setMediaItem(MediaItem.fromUri(videoUrl))
            player.play()
        }
    }
    
    // 下載 Telegram 影片
    private fun downloadTelegramVideo(video: TelegramVideo) {
        lifecycleScope.launch {
            val savePath = "${getExternalFilesDir(null)}/downloads/${video.title}.mp4"
            telegramManager.downloadVideo(video.videoUrl, savePath)
            showToast("影片已下載到: $savePath")
        }
    }
}
```

---

## 🎮 觸摸控制方案

### 手勢識別

| 手勢 | 功能 |
|------|------|
| **單擊** | 顯示/隱藏控制面板 |
| **雙擊** | 播放/暫停 |
| **左滑** | 快退 10 秒 |
| **右滑** | 快進 10 秒 |
| **上滑** | 增加亮度 |
| **下滑** | 減少亮度 |
| **捏合縮放** | 調整 VR 縮放 |

### 實現

```kotlin
class GestureDetector(context: Context) : GestureDetector.SimpleOnGestureListener() {
    
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        // 顯示/隱藏控制面板
        toggleControlPanel()
        return true
    }
    
    override fun onDoubleTap(e: MotionEvent): Boolean {
        // 播放/暫停
        togglePlayPause()
        return true
    }
    
    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        when {
            abs(distanceX) > abs(distanceY) -> {
                // 水平滑動 - 調整進度
                if (distanceX > 0) seekBackward() else seekForward()
            }
            abs(distanceY) > abs(distanceX) -> {
                // 垂直滑動 - 調整亮度
                if (distanceY > 0) decreaseBrightness() else increaseBrightness()
            }
        }
        return true
    }
}
```

---

## 📦 依賴庫

```gradle
dependencies {
    // Telegram API
    implementation 'com.squareup.okhttp3:okhttp:4.11.0'
    implementation 'com.google.code.gson:gson:2.10.1'
    
    // 影片播放
    implementation 'androidx.media3:media3-exoplayer:1.1.1'
    
    // 下載管理
    implementation 'androidx.work:work-runtime-ktx:2.8.1'
    
    // 手勢識別
    implementation 'androidx.gesture:gesture:1.0.0-alpha02'
    
    // VR 相關（已有）
    implementation 'androidx.opengl:opengl:1.0.0'
}
```

---

## 🔐 安全考慮

### Bot Token 管理
```kotlin
// ❌ 不要硬編碼 Token
// val BOT_TOKEN = "123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11"

// ✅ 從 BuildConfig 或遠程配置獲取
val BOT_TOKEN = BuildConfig.TELEGRAM_BOT_TOKEN
```

### 權限要求
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

---

## 📊 開發時間表

| 階段 | 任務 | 預計時間 |
|------|------|---------|
| 1 | 設定 Telegram Bot | 1 小時 |
| 2 | 實現 TelegramManager | 2 小時 |
| 3 | 實現觸摸控制 | 2 小時 |
| 4 | 整合所有功能 | 2 小時 |
| 5 | 測試和調試 | 2 小時 |
| **總計** | - | **9 小時** |

---

## 🚀 下一步

1. **建立 Telegram Bot**
   - 訪問 BotFather 建立 Bot
   - 記錄 Bot Token

2. **實現 Telegram 功能模塊**
   - 編寫 TelegramManager
   - 實現 API 調用

3. **實現觸摸控制**
   - 編寫 GestureDetector
   - 實現手勢識別

4. **整合和測試**
   - 整合所有模塊
   - 進行完整測試

---

**準備好開始實現了嗎？** 🎉

