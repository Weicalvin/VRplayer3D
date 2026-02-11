package com.vr.player

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.util.Log

/**
 * 觸摸屏手勢控制器
 * 支援單擊、雙擊、滑動、捏合等手勢
 */
class GestureController(context: Context) {

    companion object {
        private const val TAG = "GestureController"
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    // 回調介面
    interface GestureListener {
        fun onSingleTap()                    // 單擊
        fun onDoubleTap()                    // 雙擊
        fun onSwipeLeft()                    // 左滑
        fun onSwipeRight()                   // 右滑
        fun onSwipeUp()                      // 上滑
        fun onSwipeDown()                    // 下滑
        fun onPinch(scale: Float)            // 捏合縮放
        fun onLongPress()                    // 長按
    }

    private var listener: GestureListener? = null

    private val gestureDetector: GestureDetector
    private val scaleGestureDetector: ScaleGestureDetector

    private var startX = 0f
    private var startY = 0f

    init {
        gestureDetector = GestureDetector(context, GestureListener())
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    /**
     * 設定手勢監聽器
     */
    fun setGestureListener(listener: GestureListener) {
        this.listener = listener
    }

    /**
     * 處理觸摸事件
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_UP -> {
                handleSwipe(event.x, event.y)
            }
        }

        return true
    }

    /**
     * 處理滑動手勢
     */
    private fun handleSwipe(endX: Float, endY: Float) {
        val deltaX = endX - startX
        val deltaY = endY - startY

        // 判斷滑動方向
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            // 水平滑動
            if (Math.abs(deltaX) > SWIPE_THRESHOLD) {
                if (deltaX > 0) {
                    listener?.onSwipeRight()
                    Log.d(TAG, "右滑")
                } else {
                    listener?.onSwipeLeft()
                    Log.d(TAG, "左滑")
                }
            }
        } else {
            // 垂直滑動
            if (Math.abs(deltaY) > SWIPE_THRESHOLD) {
                if (deltaY > 0) {
                    listener?.onSwipeDown()
                    Log.d(TAG, "下滑")
                } else {
                    listener?.onSwipeUp()
                    Log.d(TAG, "上滑")
                }
            }
        }
    }

    /**
     * 內部手勢監聽器
     */
    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            listener?.onSingleTap()
            Log.d(TAG, "單擊")
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            listener?.onDoubleTap()
            Log.d(TAG, "雙擊")
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            listener?.onLongPress()
            Log.d(TAG, "長按")
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return true
        }
    }

    /**
     * 內部縮放監聽器
     */
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            val scaleFactor = detector?.scaleFactor ?: 1f
            listener?.onPinch(scaleFactor)
            Log.d(TAG, "捏合: $scaleFactor")
            return true
        }
    }
}
