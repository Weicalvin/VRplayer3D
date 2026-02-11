package com.vr.player

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.Surface
import com.google.android.exoplayer2.ExoPlayer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class VRRenderer(private val context: Context) : GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private var surfaceTexture: SurfaceTexture? = null
    private var surface: Surface? = null
    private var updateSurface = false
    private var player: ExoPlayer? = null

    // 瞳距偏移量
    var ipdOffset: Float = 0.0f 

    // 畸變參數
    private val distortionK1 = 0.35f 
    private val distortionK2 = 0.20f

    // 頭部追蹤
    private val headTracker = HeadTracker(context)
    private val headViewMatrix = FloatArray(16)

    // 性能優化：幀率控制
    private var lastFrameTime = 0L
    private var frameCount = 0
    private var fps = 0
    private val targetFps = 60
    private val frameTimeMs = 1000L / targetFps

    private val squareCoords = floatArrayOf(
        -1.0f,  1.0f, 0.0f,
        -1.0f, -1.0f, 0.0f,
         1.0f, -1.0f, 0.0f,
         1.0f,  1.0f, 0.0f
    )

    private val textureCoords = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f
    )

    private var vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(squareCoords.size * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply { put(squareCoords); position(0) }
    }

    private var textureBuffer: FloatBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply { put(textureCoords); position(0) }
    }

    private var programHandle = 0
    private var textureHandle = 0
    private val stMatrix = FloatArray(16)

    fun setPlayer(exoPlayer: ExoPlayer) {
        this.player = exoPlayer
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        
        // 初始化矩陣
        Matrix.setIdentityM(headViewMatrix, 0)
        
        // 啟動頭部追蹤
        headTracker.start()
        
        // 性能優化：初始化幀時間
        lastFrameTime = System.currentTimeMillis()

        val vertexShaderCode = """
            attribute vec4 vPosition;
            attribute vec2 vTexCoord;
            varying vec2 texCoord;
            uniform mat4 uSTMatrix;
            uniform mat4 uHeadMatrix; 
            
            void main() {
                gl_Position = uHeadMatrix * vPosition; 
                texCoord = (uSTMatrix * vec4(vTexCoord, 0.0, 1.0)).xy;
            }
        """

        val fragmentShaderCode = """
            #extension GL_OES_EGL_image_external : require
            precision mediump float;
            varying vec2 texCoord;
            uniform samplerExternalOES sTexture;
            uniform float k1;
            uniform float k2;
            uniform float uScale;

            void main() {
                vec2 rPos = 2.0 * texCoord - 1.0;
                float r2 = rPos.x * rPos.x + rPos.y * rPos.y;
                float f = 1.0 + k1 * r2 + k2 * (r2 * r2);
                vec2 newCoord = rPos * f * uScale;
                newCoord = (newCoord + 1.0) / 2.0;

                if (newCoord.x < 0.0 || newCoord.x > 1.0 || newCoord.y < 0.0 || newCoord.y > 1.0) {
                    gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
                } else {
                    gl_FragColor = texture2D(sTexture, newCoord);
                }
            }
        """
        
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        programHandle = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        textureHandle = textures[0]

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureHandle)
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat())
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())

        surfaceTexture = SurfaceTexture(textureHandle)
        surfaceTexture!!.setOnFrameAvailableListener(this)
        surface = Surface(surfaceTexture)
        player?.setVideoSurface(surface)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    }

    override fun onDrawFrame(gl: GL10?) {
        // 性能優化：幀率控制
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastFrameTime
        
        // 如果距離上一幀的時間小於目標幀時間，則跳過渲染
        if (elapsedTime < frameTimeMs) {
            return
        }
        
        lastFrameTime = currentTime
        frameCount++
        
        // 每秒更新一次 FPS
        if (frameCount % targetFps == 0) {
            fps = frameCount
            frameCount = 0
        }

        // 每一幀都更新矩陣
        headTracker.getLastHeadView(headViewMatrix)

        synchronized(this) {
            if (updateSurface) {
                surfaceTexture?.updateTexImage()
                surfaceTexture?.getTransformMatrix(stMatrix)
                updateSurface = false
            }
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(programHandle)

        val positionHandle = GLES20.glGetAttribLocation(programHandle, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer)

        val texCoordHandle = GLES20.glGetAttribLocation(programHandle, "vTexCoord")
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 8, textureBuffer)

        val matrixHandle = GLES20.glGetUniformLocation(programHandle, "uSTMatrix")
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, stMatrix, 0)
        
        val headMatrixHandle = GLES20.glGetUniformLocation(programHandle, "uHeadMatrix")
        GLES20.glUniformMatrix4fv(headMatrixHandle, 1, false, headViewMatrix, 0)

        val k1Handle = GLES20.glGetUniformLocation(programHandle, "k1")
        val k2Handle = GLES20.glGetUniformLocation(programHandle, "k2")
        val scaleHandle = GLES20.glGetUniformLocation(programHandle, "uScale")
        GLES20.glUniform1f(k1Handle, distortionK1)
        GLES20.glUniform1f(k2Handle, distortionK2)
        GLES20.glUniform1f(scaleHandle, 0.8f)

        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels
        val halfWidth = width / 2
        val offsetPixels = (ipdOffset * 100).toInt()

        // 1. 左眼
        GLES20.glViewport(0 - offsetPixels, 0, halfWidth, height)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        // 2. 右眼
        GLES20.glViewport(halfWidth + offsetPixels, 0, halfWidth, height)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        updateSurface = true
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    fun stopHeadTracking() {
        headTracker.stop()
    }

    fun getFps(): Int {
        return fps
    }
}
