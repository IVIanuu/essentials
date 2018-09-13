package com.ivianuu.essentials.util.ext

import android.os.Handler

fun Handler.removeAll() {
    removeCallbacksAndMessages(null)
}