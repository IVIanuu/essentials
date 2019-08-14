package com.ivianuu.essentials.sample.ui

import android.view.View
import com.ivianuu.compose.View
import com.ivianuu.compose.common.Route
import com.ivianuu.compose.layoutRes
import com.ivianuu.compose.state
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.compose.AppBar
import com.ivianuu.essentials.ui.compose.Scaffold
import kotlinx.android.synthetic.main.counter.view.*

fun CounterRoute() = Route {
    Scaffold(
        appBar = { AppBar(title = "Counter") },
        content = {
            val (count, setCount) = state { 0 }
            View<View> {
                layoutRes(R.layout.counter)

                bindView {
                    count_text.text = "Count: $count"
                    increment.setOnClickListener { setCount(count + 1) }
                    decrement.setOnClickListener { setCount(count - 1) }
                }
            }
        }
    )
}