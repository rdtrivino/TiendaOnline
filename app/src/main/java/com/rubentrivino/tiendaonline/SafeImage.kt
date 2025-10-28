package com.rubentrivino.tiendaonline

import android.net.Uri
import android.widget.ImageView
import com.rubentrivino.tiendaonline.R

fun ImageView.loadAny(src: String?, placeholderRes: Int = R.drawable.placeholder) {
    if (src.isNullOrBlank()) {
        setImageResourceSafe(placeholderRes)
        return
    }

    try {
        if (src.startsWith("content://") || src.startsWith("file://")) {
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageURI(Uri.parse(src))
            return
        }

        val resId = context.resources.getIdentifier(src, "drawable", context.packageName)
        if (resId != 0) {
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageResource(resId)
            return
        }

        scaleType = ImageView.ScaleType.CENTER_CROP
        setImageURI(Uri.parse(src))
    } catch (_: SecurityException) {
        setImageResourceSafe(placeholderRes)
    } catch (_: Exception) {
        setImageResourceSafe(placeholderRes)
    }
}

private fun ImageView.setImageResourceSafe(resId: Int) {
    try {
        setImageResource(resId)
    } catch (_: Exception) {}
}
