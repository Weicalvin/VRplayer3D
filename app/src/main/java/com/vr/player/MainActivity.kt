package com.vr.player

import android.app.Activity
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var vrSurfaceView: GLSurfaceView
    private lateinit var btnPickVideo: Button
    private lateinit var btnSettings: Button
    private lateinit var seekBarProgress: SeekBar
    private lateinit var seekBarVolume: SeekBar
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalTime: TextView
    private lateinit var tvVolumeLevel: TextView
    private lateinit var tvStatus: TextView
    
    private lateinit var renderer: VRRenderer
    private var player: ExoPlayer? = null
    private lateinit var audioManager: AudioManager
    
    private val PICK_VIDEO_REQUEST = 1
    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            updateProgress()
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Views
        vrSurfaceView = findViewById(R.id.vr_surface_view)
        btnPickVideo = findViewById(R.id.btn_pick_video)
        btnSettings = findViewById(R.id.btn_settings)
        seekBarProgress = findViewById(R.id.seek_bar_progress)
        seekBarVolume = findViewById(R.id.seek_bar_volume)
        tvCurrentTime = findViewById(R.id.tv_current_time)
        tvTotalTime = findViewById(R.id.tv_total_time)
        tvVolumeLevel = findViewById(R.id.tv_volume_level)
        tvStatus = findViewById(R.id.tv_status)

        // Initialize Audio Manager
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        seekBarVolume.max = maxVolume
        seekBarVolume.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        // Initialize Player
        player = ExoPlayer.Builder(this).build()
        player?.repeatMode = ExoPlayer.REPEAT_MODE_ONE
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        tvStatus.text = "播放中..."
                        updateTotalTime()
                    }
                    Player.STATE_ENDED -> {
                        tvStatus.text = "播放完成"
                    }
                    Player.STATE_BUFFERING -> {
                        tvStatus.text = "緩衝中..."
                    }
                    else -> {}
                }
            }
        })

        // Initialize Renderer
        renderer = VRRenderer(this)
        renderer.setPlayer(player!!)

        vrSurfaceView.setEGLContextClientVersion(2)
        vrSurfaceView.setRenderer(renderer)
        vrSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        // Setup Button Listeners
        btnPickVideo.setOnClickListener {
            openFilePicker()
        }

        btnSettings.setOnClickListener {
            showSettingsMenu()
        }

        // Setup SeekBar Listeners
        seekBarProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && player != null) {
                    val duration = player!!.duration
                    val newPosition = (progress.toLong() * duration) / 100
                    player!!.seekTo(newPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                    updateVolumeDisplay()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        updateVolumeDisplay()
    }

    override fun onResume() {
        super.onResume()
        vrSurfaceView.onResume()
        handler.post(updateProgressRunnable)
    }

    override fun onPause() {
        super.onPause()
        vrSurfaceView.onPause()
        player?.pause()
        handler.removeCallbacks(updateProgressRunnable)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> {
                    renderer.ipdOffset += 0.05f
                    showIpdToast()
                    return true
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    renderer.ipdOffset -= 0.05f
                    showIpdToast()
                    return true
                }
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    // 減少音量
                    val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                    if (currentVolume > 0) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume - 1, 0)
                        seekBarVolume.progress = currentVolume - 1
                        updateVolumeDisplay()
                    }
                    return true
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    // 增加音量
                    val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    if (currentVolume < maxVolume) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume + 1, 0)
                        seekBarVolume.progress = currentVolume + 1
                        updateVolumeDisplay()
                    }
                    return true
                }
                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                    if (player?.isPlaying == true) player?.pause() else player?.play()
                    return true
                }
                KeyEvent.KEYCODE_MENU -> {
                    showSettingsMenu()
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun updateProgress() {
        if (player != null && player!!.duration > 0) {
            val currentPosition = player!!.currentPosition
            val duration = player!!.duration
            val progress = ((currentPosition * 100) / duration).toInt()
            seekBarProgress.progress = progress
            
            tvCurrentTime.text = formatTime(currentPosition)
        }
    }

    private fun updateTotalTime() {
        if (player != null && player!!.duration > 0) {
            tvTotalTime.text = formatTime(player!!.duration)
        }
    }

    private fun updateVolumeDisplay() {
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        tvVolumeLevel.text = "$currentVolume/$maxVolume"
    }

    private fun formatTime(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        val hours = (milliseconds / (1000 * 60 * 60)) % 24
        
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    private fun showIpdToast() {
        val displayValue = String.format("%.2f", renderer.ipdOffset)
        Toast.makeText(this, "IPD 偏移: $displayValue", Toast.LENGTH_SHORT).show()
    }

    private fun showSettingsMenu() {
        Toast.makeText(this, "設置菜單 (開發中)", Toast.LENGTH_SHORT).show()
        // TODO: Implement settings menu
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "video/*"
        }
        startActivityForResult(intent, PICK_VIDEO_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                playVideo(uri)
            }
        }
    }

    private fun playVideo(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
        tvStatus.text = "正在加載..."
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
        handler.removeCallbacks(updateProgressRunnable)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        renderer.stopHeadTracking()
        player?.release()
        player = null
        handler.removeCallbacks(updateProgressRunnable)
    }
}
