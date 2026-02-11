package com.vr.player

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

/**
 * Telegram API 管理器
 * 負責與後端服務通訊，獲取和下載影片
 */
class TelegramManager(private val context: Context) {

    companion object {
        private const val TAG = "TelegramManager"
        private const val BACKEND_URL = "http://192.168.1.100:8000"  // 改成您的後端地址
        private const val TIMEOUT_SECONDS = 30L
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    /**
     * 數據模型
     */
    data class TelegramVideo(
        val updateId: Int,
        val messageId: Int,
        val chatId: Long,
        val chatTitle: String,
        val fileId: String,
        val fileUniqueId: String,
        val fileName: String,
        val fileSize: Long,
        val duration: Int,
        val width: Int,
        val height: Int,
        val date: Long
    )

    data class ApiResponse<T>(
        val success: Boolean,
        val data: T?,
        val error: String?
    )

    // ==================== 公開方法 ====================

    /**
     * 健康檢查
     */
    fun healthCheck(callback: (Boolean) -> Unit) {
        coroutineScope.launch {
            try {
                val request = Request.Builder()
                    .url("$BACKEND_URL/health")
                    .get()
                    .build()

                val response = httpClient.newCall(request).execute()
                val isHealthy = response.isSuccessful

                withContext(Dispatchers.Main) {
                    callback(isHealthy)
                }

                Log.d(TAG, "健康檢查: ${if (isHealthy) "成功" else "失敗"}")
            } catch (e: Exception) {
                Log.e(TAG, "健康檢查異常", e)
                withContext(Dispatchers.Main) {
                    callback(false)
                }
            }
        }
    }

    /**
     * 獲取 Telegram 更新（影片列表）
     */
    fun getUpdates(callback: (List<TelegramVideo>?) -> Unit) {
        coroutineScope.launch {
            try {
                val request = Request.Builder()
                    .url("$BACKEND_URL/updates")
                    .get()
                    .build()

                val response = httpClient.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: return@launch
                    val jsonObject = JSONObject(responseBody)
                    val videosArray = jsonObject.optJSONArray("videos") ?: JSONArray()

                    val videos = mutableListOf<TelegramVideo>()
                    for (i in 0 until videosArray.length()) {
                        val videoJson = videosArray.getJSONObject(i)
                        videos.add(parseVideoJson(videoJson))
                    }

                    withContext(Dispatchers.Main) {
                        callback(videos)
                    }

                    Log.d(TAG, "獲取 ${videos.size} 個影片")
                } else {
                    Log.e(TAG, "API 錯誤: ${response.code}")
                    withContext(Dispatchers.Main) {
                        callback(null)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "獲取更新異常", e)
                withContext(Dispatchers.Main) {
                    callback(null)
                }
            }
        }
    }

    /**
     * 下載影片
     */
    fun downloadVideo(
        fileId: String,
        fileName: String,
        onProgress: (progress: Int) -> Unit,
        onComplete: (filePath: String?) -> Unit
    ) {
        coroutineScope.launch {
            try {
                val request = Request.Builder()
                    .url("$BACKEND_URL/file/$fileId/download")
                    .get()
                    .build()

                val response = httpClient.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body ?: return@launch
                    val contentLength = responseBody.contentLength()

                    // 建立下載目錄
                    val downloadDir = File(context.getExternalFilesDir(null), "videos")
                    downloadDir.mkdirs()

                    val file = File(downloadDir, fileName)
                    var downloadedBytes = 0L

                    FileOutputStream(file).use { fileOutput ->
                        responseBody.byteStream().use { inputStream ->
                            val buffer = ByteArray(8192)
                            var bytesRead: Int

                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                fileOutput.write(buffer, 0, bytesRead)
                                downloadedBytes += bytesRead

                                val progress = if (contentLength > 0) {
                                    ((downloadedBytes * 100) / contentLength).toInt()
                                } else {
                                    0
                                }

                                withContext(Dispatchers.Main) {
                                    onProgress(progress)
                                }
                            }
                        }
                    }

                    Log.d(TAG, "下載完成: ${file.absolutePath}")
                    withContext(Dispatchers.Main) {
                        onComplete(file.absolutePath)
                    }
                } else {
                    Log.e(TAG, "下載失敗: ${response.code}")
                    withContext(Dispatchers.Main) {
                        onComplete(null)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "下載異常", e)
                withContext(Dispatchers.Main) {
                    onComplete(null)
                }
            }
        }
    }

    /**
     * 獲取已下載的檔案列表
     */
    fun getDownloads(callback: (List<String>?) -> Unit) {
        coroutineScope.launch {
            try {
                val request = Request.Builder()
                    .url("$BACKEND_URL/downloads")
                    .get()
                    .build()

                val response = httpClient.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: return@launch
                    val jsonObject = JSONObject(responseBody)
                    val filesArray = jsonObject.optJSONArray("files") ?: JSONArray()

                    val files = mutableListOf<String>()
                    for (i in 0 until filesArray.length()) {
                        val fileJson = filesArray.getJSONObject(i)
                        files.add(fileJson.getString("filename"))
                    }

                    withContext(Dispatchers.Main) {
                        callback(files)
                    }

                    Log.d(TAG, "獲取 ${files.size} 個已下載檔案")
                } else {
                    Log.e(TAG, "API 錯誤: ${response.code}")
                    withContext(Dispatchers.Main) {
                        callback(null)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "獲取下載列表異常", e)
                withContext(Dispatchers.Main) {
                    callback(null)
                }
            }
        }
    }

    /**
     * 刪除已下載的檔案
     */
    fun deleteFile(fileName: String, callback: (Boolean) -> Unit) {
        coroutineScope.launch {
            try {
                val request = Request.Builder()
                    .url("$BACKEND_URL/file/$fileName")
                    .delete()
                    .build()

                val response = httpClient.newCall(request).execute()
                val success = response.isSuccessful

                withContext(Dispatchers.Main) {
                    callback(success)
                }

                Log.d(TAG, "刪除檔案: ${if (success) "成功" else "失敗"}")
            } catch (e: Exception) {
                Log.e(TAG, "刪除檔案異常", e)
                withContext(Dispatchers.Main) {
                    callback(false)
                }
            }
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 解析影片 JSON
     */
    private fun parseVideoJson(json: JSONObject): TelegramVideo {
        return TelegramVideo(
            updateId = json.optInt("update_id", 0),
            messageId = json.optInt("message_id", 0),
            chatId = json.optLong("chat_id", 0),
            chatTitle = json.optString("chat_title", "Unknown"),
            fileId = json.optString("file_id", ""),
            fileUniqueId = json.optString("file_unique_id", ""),
            fileName = json.optString("file_name", "video.mp4"),
            fileSize = json.optLong("file_size", 0),
            duration = json.optInt("duration", 0),
            width = json.optInt("width", 0),
            height = json.optInt("height", 0),
            date = json.optLong("date", 0)
        )
    }

    /**
     * 清理資源
     */
    fun cleanup() {
        coroutineScope.cancel()
        httpClient.dispatcher.executorService.shutdown()
    }
}
