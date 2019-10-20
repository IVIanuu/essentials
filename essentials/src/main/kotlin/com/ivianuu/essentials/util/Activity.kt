package com.ivianuu.essentials.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService

fun Activity.hideInputMethod() {
    getSystemService<InputMethodManager>()!!.hideSoftInputFromWindow(
        window.peekDecorView().windowToken,
        0
    )
}

fun Activity.showInputMethod(view: View, flags: Int = 0) {
    getSystemService<InputMethodManager>()!!.showSoftInput(view, flags)
}