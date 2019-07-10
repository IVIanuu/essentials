/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.ui.simple

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.ivianuu.essentials.R
import kotlinx.android.synthetic.main.es_view_tab_layout.es_tab_layout
import kotlinx.android.synthetic.main.es_view_view_pager.es_view_pager

/**
 * Controller which hosts a tab layout and a view pager
 */
abstract class TabsController : ToolbarController() {

    override val layoutRes: Int
        get() = R.layout.es_controller_tabs

    val tabLayout: TabLayout
        get() = es_tab_layout

    val viewPager: ViewPager
        get() = es_view_pager

    protected val adapter by lazy { adapter() }

    override fun onViewCreated(view: View, savedViewState: Bundle?) {
        super.onViewCreated(view, savedViewState)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    protected abstract fun adapter(): PagerAdapter

}