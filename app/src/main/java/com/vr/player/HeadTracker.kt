package com.vr.player

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.Matrix

class HeadTracker(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR) 
    // 使用 GAME_ROTATION_VECTOR 比較不會受磁場干擾，適合 VR

    // 儲存最新的旋轉矩陣 (4x4)
    private val rotationMatrix = FloatArray(16)
    
    // 用於修正手機預設橫向/直向的矩陣
    private val adjustedMatrix = FloatArray(16)

    init {
        Matrix.setIdentityM(rotationMatrix, 0)
    }

    fun start() {
        sensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    // 讓 Renderer 來拿最新的矩陣
    fun getLastHeadView(headView: FloatArray) {
        synchronized(this) {
            System.arraycopy(adjustedMatrix, 0, headView, 0, 16)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            synchronized(this) {
                // 將傳感器的向量轉換為旋轉矩陣
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

                // 因為手機是橫著放的 (Landscape)，我們需要重新映射座標軸
                // 否則上下看會變成左右轉
                SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z,
                    adjustedMatrix
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 不需要處理
    }
}
