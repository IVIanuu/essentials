package com.ivianuu.essentials.sample.ui

import android.view.View
import com.ivianuu.compose.ViewByLayoutRes
import com.ivianuu.compose.common.Route
import com.ivianuu.compose.set
import com.ivianuu.compose.state
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.compose.AppBar
import com.ivianuu.essentials.ui.compose.Scaffold
import kotlinx.android.synthetic.main.counter.view.*

fun CounterRoute() = Route {
    Scaffold(
        appBar = { AppBar(title = "Counter") },
        content = {
            ViewByLayoutRes<View>(layoutRes = R.layout.counter) {
                val (count, setCount) = state { 0 }

                set(count) {
                    count_text.text = "Count: $count"
                    increment.setOnClickListener { setCount(count + 1) }
                    decrement.setOnClickListener { setCount(count - 1) }
                }
            }
        }
    )
}