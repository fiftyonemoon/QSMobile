package com.quicksolveplus.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView

/**
 * 17/04/23.
 *
 * @author hardkgosai.
 */
class NonScrollingListView : ListView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMeasureSpecX =
            MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, heightMeasureSpecX)
        val params = layoutParams
        params.height = measuredHeight
    }
}