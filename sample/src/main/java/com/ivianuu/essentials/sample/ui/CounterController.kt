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

import android.os.Parcelable
import android.view.View
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.base.BaseController
import com.ivianuu.essentials.ui.traveler.ControllerKey
import com.ivianuu.essentials.ui.traveler.requireKey
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_counter.*

/**
 * @author Manuel Wrage (IVIanuu)
 */
class CounterController : BaseController() {

    override val layoutRes = R.layout.fragment_counter

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        val key = requireKey<CounterControllerKey>()
        title.text = "Count: ${key.count}"

        go_up.setOnClickListener { travelerRouter.navigateTo(CounterControllerKey(key.count + 1)) }
        go_down.setOnClickListener { travelerRouter.exit() }
        dialog.setOnClickListener { travelerRouter.navigateTo(SomeDialogKey) }
    }


}

@Parcelize
data class CounterControllerKey(val count: Int) : ControllerKey(), Parcelable {
    override fun createController(data: Any?) = CounterController()
}