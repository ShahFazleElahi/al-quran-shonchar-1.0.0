package com.alquran.shonchar

import android.app.Activity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat

class CustomToast(private val activity: Activity) {

    companion object {
        const val TOAST_POSITIVE = 0
        const val TOAST_NEGATIVE = 1
    }

    fun show(text: String, type: Int) {
        activity.run {
            val layout = layoutInflater.inflate(
                R.layout.layout_toast,
                findViewById(R.id.toast_linear)
            )
            val  toastLayout = layout.findViewById<ImageView>(R.id.image)
            when(type) {
                TOAST_POSITIVE -> {
                    layout.setBackgroundResource(R.drawable.toast_positive)
                    toastLayout.setImageDrawable(ResourcesCompat.getDrawable(resources,
                        R.drawable.ic_check, null)
                    )
                }
                TOAST_NEGATIVE ->  {
                    layout.setBackgroundResource(R.drawable.toast_negative)
                    toastLayout.setImageDrawable(ResourcesCompat.getDrawable(resources,
                        R.drawable.error, null)
                    )
                }
            }
            layout.findViewById<TextView>(R.id.text).text = text
            val toast = Toast(this)
            toast.duration = Toast.LENGTH_LONG
            @Suppress("DEPRECATION")
            toast.view = layout
            toast.show()
        }
    }
}