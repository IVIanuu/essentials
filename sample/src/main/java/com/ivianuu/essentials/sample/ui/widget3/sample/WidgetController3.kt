package com.ivianuu.essentials.sample.ui.widget3.sample

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.updateLayoutParams
import com.ivianuu.essentials.sample.ui.widget3.android.ViewGroupWidget
import com.ivianuu.essentials.sample.ui.widget3.android.ViewWidget
import com.ivianuu.essentials.sample.ui.widget3.core.BuildContext
import com.ivianuu.essentials.sample.ui.widget3.es.WidgetController

class WidgetController3 : WidgetController() {
    override fun BuildContext.build() {
        +ViewGroupWidget(
            createView = { LinearLayout(it.context) },
            updateView = {
                it as LinearLayout
                it.updateLayoutParams {
                    width = MATCH_PARENT
                    height = MATCH_PARENT
                }
                it.orientation = VERTICAL
                it.gravity = Gravity.CENTER
                it.setBackgroundColor(Color.GREEN)
            },
            destroyView = {},
            children = {
                +ViewWidget(
                    createView = { AppCompatTextView(it.context) },
                    updateView = {
                        it as TextView
                        it.text = "Hello"
                        it.setBackgroundColor(Color.BLUE)
                    },
                    destroyView = {}
                )
                +ViewWidget(
                    createView = { AppCompatTextView(it.context) },
                    updateView = {
                        it as TextView
                        it.text = "World"
                        it.setBackgroundColor(Color.RED)
                    },
                    destroyView = {}
                )
            }
        )
    }
}