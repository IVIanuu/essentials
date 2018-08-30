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

import android.os.Bundle
import android.view.View
import com.ivianuu.compass.Destination
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.state.StateFragment
import com.ivianuu.essentials.ui.state.withState
import com.ivianuu.essentials.util.ext.d
import com.ivianuu.essentials.util.ext.viewModel
import kotlinx.android.synthetic.main.fragment_counter.*

@Destination(CounterFragment::class)
object CounterDestination

/**
 * @author Manuel Wrage (IVIanuu)
 */
class CounterFragment : StateFragment() {

    override val layoutRes = R.layout.fragment_counter

    private val viewModel get() = viewModel<CounterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.subscribe { postInvalidate() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        increase.setOnClickListener { viewModel.increaseClicked() }
        decrease.setOnClickListener { viewModel.decreaseClicked() }
        reset.setOnClickListener { viewModel.resetClicked() }
    }

    override fun invalidate() {
        d { "invalidate" }
        withState(viewModel) { count.text = "Count: $it" }
    }
}