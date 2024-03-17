package com.quicksolveplus.modifiers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

/**
 * 01/03/23.
 *
 * @author hardkgosai.
 */

object Glidifiers {

    fun ImageView.loadGlide(any: Any?, placeholder: Drawable? = null) {
        Glide.with(context).load(any).placeholder(placeholder)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(this)
    }

    fun ImageView.loadGlideAsBitmap(
        any: Any?, placeholder: Drawable? = null, function: (bitmap: Bitmap?) -> Unit
    ) {
        loadGlideAsBitmap(context, any) { bitmap, isReady ->
            if (isReady) {
                setImageBitmap(bitmap)
                function(bitmap)
            } else setImageDrawable(placeholder)
        }
    }

    fun loadGlideAsBitmap(
        context: Context, any: Any?, function: (bitmap: Bitmap?, isReady: Boolean) -> Unit
    ) {
        Glide.with(context).asBitmap().load(any).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap, transition: Transition<in Bitmap?>?
                ) {
                    function(resource, true)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    function(null, false)
                }
            })
    }

}