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

package com.ivianuu.essentials.ui.base

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.autodispose.LifecycleScopeProvider
import com.ivianuu.essentials.ui.common.BackListener
import com.ivianuu.essentials.ui.common.CORRESPONDING_FRAGMENT_EVENTS
import com.ivianuu.essentials.ui.common.FragmentEvent
import com.ivianuu.essentials.ui.common.FragmentEvent.*
import com.ivianuu.essentials.ui.fragstack.FragStack
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

/**
 * Base fragment
 */
abstract class BaseFragment : Fragment(), BackListener, HasSupportFragmentInjector,
    LifecycleScopeProvider<FragmentEvent> {

    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    protected open val layoutRes = -1

    private val lifecycleSubject = BehaviorSubject.create<FragmentEvent>()

    private val stacks = mutableMapOf<Int, FragStack>()

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        lifecycleSubject.onNext(ATTACH)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            val savedStackStates =
                savedInstanceState.getParcelableArrayList<SavedStackState>(KEY_STACKS)

            savedStackStates.forEach {
                val stack = FragStack(childFragmentManager, it.containerId, it.tag, it.savedState)
                stacks[it.containerId] = stack
            }
        }

        lifecycleSubject.onNext(CREATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (layoutRes != -1) {
            inflater.inflate(layoutRes, container, false)
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleSubject.onNext(CREATE_VIEW)
    }

    override fun onStart() {
        super.onStart()
        lifecycleSubject.onNext(START)
    }

    override fun onResume() {
        super.onResume()
        lifecycleSubject.onNext(RESUME)
    }

    override fun onPause() {
        lifecycleSubject.onNext(PAUSE)
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val stackStates = stacks.values.map { stack ->
            val bundle = Bundle()
            stack.saveInstanceState(bundle)
            SavedStackState(
                stack.containerId,
                stack.tag,
                bundle
            )
        }

        outState.putParcelableArrayList(KEY_STACKS, ArrayList(stackStates))
    }

    override fun onStop() {
        lifecycleSubject.onNext(STOP)
        super.onStop()
    }

    override fun onDestroyView() {
        lifecycleSubject.onNext(DESTROY_VIEW)
        super.onDestroyView()
    }

    override fun onDestroy() {
        lifecycleSubject.onNext(DESTROY)
        super.onDestroy()
    }

    override fun onDetach() {
        lifecycleSubject.onNext(DETACH)
        super.onDetach()
    }

    override fun handleBack(): Boolean {
        return stacks.values
            .flatMap { stack -> stack.getBackstack().map { stack to it } }
            .sortedByDescending { it.second.transactionIndex }
            .map { it.first }
            .any { it.handleBack() }
    }

    override fun lifecycle() = lifecycleSubject

    override fun correspondingEvents()= CORRESPONDING_FRAGMENT_EVENTS

    override fun peekLifecycle() = lifecycleSubject.value

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

    fun getChildStack(containerId: Int, tag: String = ""): FragStack {
        return stacks.getOrPut(containerId) { FragStack(childFragmentManager, containerId, tag) }
    }

    private companion object {
        private const val KEY_STACKS = "BaseFragment.stacks"
    }

    @Parcelize
    data class SavedStackState(
        val containerId: Int,
        val tag: String,
        val savedState: Bundle
    ) : Parcelable
}