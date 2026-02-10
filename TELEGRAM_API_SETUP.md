# Telegram API 設定指南

## 第 1 步：獲取 API ID 和 API Hash

### 步驟 1.1：訪問 Telegram 開發者頁面

1. 打開瀏覽器，訪問 https://my.telegram.org
2. 用您的 Telegram 帳號登入
   - 輸入手機號碼
   - 輸入驗證碼（會發送到您的 Telegram 應用）

### 步驟 1.2：建立應用

1. 進入 "API development tools"
2. 點擊 "Create new application"
3. 填寫表單：
   - **App title**：VR Telegram Player
   - **Short name**：vr_telegram_player
   - **URL**：http://localhost:8000（本地測試）
   - **Platform**：Android
   - **Description**：VR video player for Telegram channels

4. 點擊 "Create my app!"

### 步驟 1.3：記錄 API 憑證

頁面會顯示：
- **api_id**：一個數字（例如：123456）
- **api_hash**：一個長字符串（例如：abcdef1234567890abcdef1234567890）

**⚠️ 重要**：妥善保管這些信息，不要分享給任何人！

---

## 第 2 步：設定 Python 後端

### 步驟 2.1：安裝 Python 和依賴

#### 在 Linux/Mac 上：
```bash
# 安裝 Python 3.9+
python3 --version

# 建立虛擬環境
python3 -m venv telegram_backend
source telegram_backend/bin/activate

# 安裝依賴
pip install pyrogram tgcrypto fastapi uvicorn python-dotenv
```

#### 在 Windows 上：
```bash
# 安裝 Python 3.9+
python --version

# 建立虛擬環境
python -m venv telegram_backend
telegram_backend\Scripts\activate

# 安裝依賴
pip install pyrogram tgcrypto fastapi uvicorn python-dotenv
```

#### 在 NAS 上：
```bash
# 根據 NAS 系統（如 Synology、QNAP）
# 通常可以通過 SSH 連接並執行上述命令

# 或使用 Docker（推薦）
docker pull python:3.9
docker run -it python:3.9 bash
# 然後執行上述命令
```

### 步驟 2.2：建立配置檔案

#### .env 檔案
```
# Telegram API 憑證
TELEGRAM_API_ID=YOUR_API_ID
TELEGRAM_API_HASH=YOUR_API_HASH

# 後端配置
HOST=0.0.0.0
PORT=8000
DEBUG=True

# 存儲配置
DOWNLOAD_PATH=/home/user/telegram_downloads
SESSION_PATH=/home/user/telegram_sessions

# 對於 NAS
# DOWNLOAD_PATH=/volume1/telegram_downloads
# SESSION_PATH=/volume1/telegram_sessions
```

### 步驟 2.3：建立主程序

#### main.py
```python
import os
from fastapi import FastAPI, HTTPException
from fastapi.responses import FileResponse
from pyrogram import Client
from pyrogram.types import Chat
from dotenv import load_dotenv
import logging
from pathlib import Path

# 載入環境變數
load_dotenv()

# 配置日誌
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# 初始化 FastAPI
app = FastAPI(title="Telegram VR Player Backend")

# 配置
API_ID = int(os.getenv("TELEGRAM_API_ID"))
API_HASH = os.getenv("TELEGRAM_API_HASH")
DOWNLOAD_PATH = os.getenv("DOWNLOAD_PATH", "./downloads")
SESSION_PATH = os.getenv("SESSION_PATH", "./sessions")

# 確保目錄存在
Path(DOWNLOAD_PATH).mkdir(parents=True, exist_ok=True)
Path(SESSION_PATH).mkdir(parents=True, exist_ok=True)

# 初始化 Telegram 客戶端
client = Client(
    "vr_player",
    api_id=API_ID,
    api_hash=API_HASH,
    workdir=SESSION_PATH
)

@app.on_event("startup")
async def startup():
    """應用啟動時連接 Telegram"""
    logger.info("啟動 Telegram 客戶端...")
    await client.start()
    logger.info("Telegram 客戶端已連接")

@app.on_event("shutdown")
async def shutdown():
    """應用關閉時斷開連接"""
    logger.info("關閉 Telegram 客戶端...")
    await client.stop()

@app.get("/health")
async def health_check():
    """健康檢查"""
    return {"status": "ok"}

@app.get("/channels")
async def get_channels():
    """
    獲取用戶訂閱的所有頻道
    
    返回格式：
    [
        {
            "id": 123456789,
            "title": "頻道名稱",
            "username": "channel_username",
            "members_count": 1000,
            "description": "頻道描述"
        }
    ]
    """
    try:
        channels = []
        async for dialog in client.get_dialogs():
            if dialog.is_channel:
                chat = dialog.chat
                channels.append({
                    "id": chat.id,
                    "title": chat.title,
                    "username": chat.username or "",
                    "members_count": chat.members_count or 0,
                    "description": chat.description or ""
                })
        
        logger.info(f"找到 {len(channels)} 個頻道")
        return channels
    
    except Exception as e:
        logger.error(f"獲取頻道失敗: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/channel/{channel_id}/videos")
async def get_channel_videos(channel_id: int, limit: int = 50):
    """
    獲取頻道中的影片
    
    參數：
    - channel_id: 頻道 ID
    - limit: 返回的最大影片數量
    
    返回格式：
    [
        {
            "id": 123,
            "title": "影片標題",
            "duration": 3600,
            "file_size": 104857600,
            "date": 1609459200,
            "thumbnail": "..."
        }
    ]
    """
    try:
        videos = []
        count = 0
        
        async for message in client.get_chat_history(channel_id, limit=limit):
            if message.video:
                video = message.video
                videos.append({
                    "id": message.id,
                    "title": message.caption or f"Video {message.id}",
                    "duration": video.duration,
                    "file_size": video.file_size,
                    "date": message.date.timestamp(),
                    "mime_type": video.mime_type
                })
                count += 1
        
        logger.info(f"頻道 {channel_id} 找到 {count} 個影片")
        return videos
    
    except Exception as e:
        logger.error(f"獲取影片失敗: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/video/{channel_id}/{message_id}/download")
async def download_video(channel_id: int, message_id: int):
    """
    下載影片
    
    返回格式：
    {
        "status": "success",
        "file_path": "/path/to/video.mp4",
        "file_size": 104857600
    }
    """
    try:
        message = await client.get_messages(channel_id, message_id)
        
        if not message.video:
            raise HTTPException(status_code=404, detail="Message is not a video")
        
        # 下載影片
        logger.info(f"開始下載影片 {message_id}...")
        file_path = await client.download_media(
            message,
            file_name=f"{message_id}.mp4",
            progress=progress_callback
        )
        
        logger.info(f"影片已下載到: {file_path}")
        
        return {
            "status": "success",
            "file_path": str(file_path),
            "file_size": message.video.file_size
        }
    
    except Exception as e:
        logger.error(f"下載失敗: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/video/{channel_id}/{message_id}/stream")
async def stream_video(channel_id: int, message_id: int):
    """
    串流播放影片（直接返回影片 URL）
    
    返回格式：
    {
        "status": "success",
        "url": "https://..."
    }
    """
    try:
        message = await client.get_messages(channel_id, message_id)
        
        if not message.video:
            raise HTTPException(status_code=404, detail="Message is not a video")
        
        # 獲取可下載的 URL
        # 注意：Telegram 的影片 URL 可能需要特殊處理
        logger.info(f"獲取影片 {message_id} 的串流 URL...")
        
        return {
            "status": "success",
            "message_id": message_id,
            "channel_id": channel_id,
            "file_size": message.video.file_size
        }
    
    except Exception as e:
        logger.error(f"獲取串流 URL 失敗: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/downloads")
async def list_downloads():
    """
    列出已下載的影片
    
    返回格式：
    [
        {
            "filename": "123.mp4",
            "file_size": 104857600,
            "date_modified": 1609459200
        }
    ]
    """
    try:
        downloads = []
        for file in Path(DOWNLOAD_PATH).glob("*.mp4"):
            stat = file.stat()
            downloads.append({
                "filename": file.name,
                "file_size": stat.st_size,
                "date_modified": stat.st_mtime
            })
        
        return downloads
    
    except Exception as e:
        logger.error(f"列表下載失敗: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/file/{filename}")
async def get_file(filename: str):
    """
    下載已保存的影片檔案
    """
    try:
        file_path = Path(DOWNLOAD_PATH) / filename
        
        if not file_path.exists():
            raise HTTPException(status_code=404, detail="File not found")
        
        return FileResponse(
            path=file_path,
            media_type="video/mp4",
            filename=filename
        )
    
    except Exception as e:
        logger.error(f"獲取檔案失敗: {e}")
        raise HTTPException(status_code=500, detail=str(e))

def progress_callback(current, total):
    """下載進度回調"""
    percent = (current / total) * 100
    logger.info(f"下載進度: {percent:.1f}%")

if __name__ == "__main__":
    import uvicorn
    
    host = os.getenv("HOST", "0.0.0.0")
    port = int(os.getenv("PORT", 8000))
    debug = os.getenv("DEBUG", "False").lower() == "true"
    
    uvicorn.run(
        "main:app",
        host=host,
        port=port,
        reload=debug,
        log_level="info"
    )
```

### 步驟 2.4：首次運行（進行身份驗證）

```bash
# 啟動後端
python main.py

# 首次運行時，會提示輸入手機號碼和驗證碼
# 按照提示完成身份驗證
# 驗證成功後，會在 sessions 目錄中保存會話檔案
```

---

## 第 3 步：部署到本地或 NAS

### 本地部署

#### Linux/Mac
```bash
# 在後台運行
nohup python main.py > telegram_backend.log 2>&1 &

# 或使用 systemd
sudo nano /etc/systemd/system/telegram-backend.service
```

#### Windows
```bash
# 使用 Task Scheduler 或直接運行
python main.py
```

### NAS 部署

#### 使用 Docker（推薦）

##### Dockerfile
```dockerfile
FROM python:3.9-slim

WORKDIR /app

# 安裝依賴
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 複製應用
COPY . .

# 建立目錄
RUN mkdir -p /app/downloads /app/sessions

# 暴露端口
EXPOSE 8000

# 運行應用
CMD ["python", "main.py"]
```

##### requirements.txt
```
pyrogram==2.0.104
tgcrypto==1.2.5
fastapi==0.104.1
uvicorn==0.24.0
python-dotenv==1.0.0
```

##### docker-compose.yml
```yaml
version: '3.8'

services:
  telegram-backend:
    build: .
    container_name: telegram-vr-backend
    ports:
      - "8000:8000"
    volumes:
      - ./downloads:/app/downloads
      - ./sessions:/app/sessions
      - ./.env:/app/.env
    environment:
      - TELEGRAM_API_ID=${TELEGRAM_API_ID}
      - TELEGRAM_API_HASH=${TELEGRAM_API_HASH}
      - DOWNLOAD_PATH=/app/downloads
      - SESSION_PATH=/app/sessions
    restart: unless-stopped
```

##### 運行 Docker
```bash
# 建立並運行容器
docker-compose up -d

# 查看日誌
docker-compose logs -f

# 停止容器
docker-compose down
```

#### 直接在 NAS 上運行

1. SSH 連接到 NAS
2. 安裝 Python 3.9+
3. 按照本地部署步驟操作

---

## 第 4 步：測試後端

### 使用 curl 測試

```bash
# 健康檢查
curl http://localhost:8000/health

# 獲取頻道列表
curl http://localhost:8000/channels

# 獲取特定頻道的影片
curl http://localhost:8000/channel/123456789/videos

# 下載影片
curl http://localhost:8000/video/123456789/456/download
```

### 使用 Postman 測試

1. 下載 Postman：https://www.postman.com/downloads/
2. 建立新的 Collection
3. 添加請求：
   - GET http://localhost:8000/health
   - GET http://localhost:8000/channels
   - GET http://localhost:8000/channel/{channel_id}/videos
   - GET http://localhost:8000/video/{channel_id}/{message_id}/download

---

## 🔐 安全建議

### 生產環境配置

1. **使用 HTTPS**
   ```bash
   # 生成自簽名證書
   openssl req -x509 -newkey rsa:4096 -nodes -out cert.pem -keyout key.pem -days 365
   ```

2. **添加身份驗證**
   ```python
   from fastapi.security import HTTPBearer
   
   security = HTTPBearer()
   
   @app.get("/channels", dependencies=[Depends(security)])
   async def get_channels():
       ...
   ```

3. **限制 API 速率**
   ```bash
   pip install slowapi
   ```

4. **使用反向代理**
   ```nginx
   # Nginx 配置
   server {
       listen 80;
       server_name your-domain.com;
       
       location / {
           proxy_pass http://localhost:8000;
       }
   }
   ```

---

## 📊 API 端點總結

| 方法 | 端點 | 說明 |
|------|------|------|
| GET | /health | 健康檢查 |
| GET | /channels | 獲取所有頻道 |
| GET | /channel/{id}/videos | 獲取頻道影片 |
| GET | /video/{cid}/{mid}/download | 下載影片 |
| GET | /video/{cid}/{mid}/stream | 串流播放 |
| GET | /downloads | 列表下載 |
| GET | /file/{filename} | 獲取檔案 |

---

## 🆘 常見問題

### Q1：首次運行時提示需要驗證碼

**A：** 這是正常的。Telegram 會發送驗證碼到您的 Telegram 應用，輸入驗證碼即可。

### Q2：如何更新會話？

**A：** 刪除 sessions 目錄中的檔案，重新運行應用即可。

### Q3：如何在 NAS 上持久化運行？

**A：** 使用 Docker Compose 或系統服務（systemd、launchd 等）。

### Q4：如何遠程訪問？

**A：** 使用反向代理（Nginx）或 VPN。

---

**準備好開始了嗎？** 🚀

