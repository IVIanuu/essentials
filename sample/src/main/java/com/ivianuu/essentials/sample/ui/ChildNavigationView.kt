/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.sample.ui

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import com.ivianuu.daggerextensions.AutoContribute
import com.ivianuu.daggerextensions.view.ViewInjection
import com.ivianuu.essentials.injection.PerView
import com.ivianuu.essentials.injection.ViewBindingModule
import com.ivianuu.essentials.ui.traveler.RouterHolder
import com.ivianuu.traveler.Router
import kotlinx.android.synthetic.main.view_child_navigation.view.*
import javax.inject.Inject

/**
 * @author Manuel Wrage (IVIanuu)
 */
@ViewBindingModule
@PerView
@AutoContribute
class ChildNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), RouterHolder {

    @Inject lateinit var destination: ChildNavigationDestination
    @field:LocalRouter @Inject override lateinit var router: Router

    override fun onFinishInflate() {
        super.onFinishInflate()
        ViewInjection.inject(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        title.text = "Container: ${destination.index}, Count: ${destination.count}"
        setBackgroundColor(COLORS[destination.index])

        pop_to_root.setOnClickListener { router.backToRoot() }
        prev.setOnClickListener { router.exit() }


        next.navigateOnClick {
            ChildNavigationDestination(destination.index, destination.count + 1)
        }

        next.setOnClickListener {
            router.navigateTo(ChildNavigationDestination(destination.index, destination.count + 1))
        }
    }

    private companion object {
        private val COLORS =
            arrayOf(Color.RED, Color.BLUE, Color.MAGENTA)
    }
}