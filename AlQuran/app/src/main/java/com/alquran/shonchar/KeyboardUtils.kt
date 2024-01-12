package com.alquran.shonchar

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment

object KeyboardUtils {
    fun hideKeyboard(activity: Activity): Boolean {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        return run {
            var focus = activity.currentFocus
            if (focus == null) focus = View(activity)
            imm.hideSoftInputFromWindow(focus.windowToken, 0)
        }
    }

    fun hideKeyboard(fragment: Fragment): Boolean {
        val imm = fragment.requireContext()
            .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        return run {
            var focus = fragment.requireActivity().currentFocus
            if (focus == null) focus = View(fragment.requireContext())
            imm.hideSoftInputFromWindow(focus.windowToken, 0)
        }
    }

    fun hideKeyboard(editText: EditText): Boolean {
        val imm =
            editText.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}