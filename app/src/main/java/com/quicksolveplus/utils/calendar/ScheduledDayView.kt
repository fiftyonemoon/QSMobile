package com.quicksolveplus.utils.calendar

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.text.SimpleDateFormat
import java.util.*

/**
 * 29/03/23.
 *
 * @author hardkgosai.
 */
class ScheduledDayView : AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    private var date: Date? = null
    private var decorators: List<ScheduledDayDecorator>? = null

    fun bind(date: Date?, decorators: List<ScheduledDayDecorator>?) {
        this.date = date
        this.decorators = decorators

        date?.let {
            val df = SimpleDateFormat("d", Locale.getDefault())
            val day = df.format(date).toInt()
            text = day.toString()
        }
    }

    fun decorate() {
        decorators?.map {
            it.decorate(this)
        }
    }

    fun getDate(): Date? {
        return date
    }

}