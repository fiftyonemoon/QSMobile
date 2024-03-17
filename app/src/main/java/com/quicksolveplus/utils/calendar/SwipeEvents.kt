package com.quicksolveplus.utils.calendar

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

/**
 * 29/03/23.
 *
 * @author hardkgosai.
 */
class SwipeEvents(private val view: View, private val swipeCallback: SwipeCallback) :
    View.OnTouchListener {

    private var gestureDetector: GestureDetector

    private var x1 = 0f
    private var x2: Float = 0f
    private var y1: Float = 0f
    private var y2: Float = 0f

    private val gestureListener: GestureDetector.SimpleOnGestureListener =
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(event: MotionEvent): Boolean {
                x1 = event.x
                y1 = event.y
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                swipeCallback.onClick(view)
                return true
            }
        }

    init {
        gestureDetector = GestureDetector(view.context, gestureListener)
        view.setOnTouchListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                x2 = event.x
                y2 = event.y
                val xDiff = x2 - x1

                if (abs(xDiff) > 150) {
                    if (x1 < x2) {
                        swipeCallback.onSwipeRight()
                    } else {
                        swipeCallback.onSwipeLeft()
                    }
                }
            }
        }
        return gestureDetector.onTouchEvent(event)
    }

    interface SwipeCallback {
        fun onSwipeRight()
        fun onSwipeLeft()
        fun onClick(view: View)
    }

}