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
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.base.BaseViewModelFragment
import com.ivianuu.essentials.ui.common.BaseViewModel
import com.ivianuu.essentials.util.ext.d
import com.ivianuu.essentials.util.ext.main
import com.ivianuu.essentials.util.ext.observeK
import com.ivianuu.essentials.util.ext.toLiveData
import com.ivianuu.traveler.keys.FragmentClassKey
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author Manuel Wrage (IVIanuu)
 */
class MultipleCountersFragment : BaseViewModelFragment<DummyViewModel>() {

    override val layoutRes = R.layout.fragment_multiple_counters

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.lifecycle

        if (savedInstanceState == null) {

        }
    }

}

class DummyViewModel @Inject constructor() : BaseViewModel() {

    init {
        Observable.interval(1, TimeUnit.SECONDS)
            .doOnSubscribe { d { "on sub" } }
            .doOnNext { d { "on next" } }
            .doOnDispose { d { "on dispose" } }
            .main()
            .toLiveData()
            .observeK(this) { d { "changed $it" } }
    }

}

object MultipleCountersKey : FragmentClassKey(MultipleCountersFragment::class)