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

package com.ivianuu.essentials.ui.list

import android.view.View

interface ListModelListener {

    fun preCreateHolder(model: ListModel<*>) {
    }

    fun postCreateHolder(model: ListModel<*>, holder: ListHolder) {
    }

    fun preCreateView(model: ListModel<*>) {
    }

    fun postCreateView(model: ListModel<*>, view: View) {
    }

    fun preBind(model: ListModel<*>, holder: ListHolder) {
    }

    fun postBind(model: ListModel<*>, holder: ListHolder) {
    }

    fun preUnbind(model: ListModel<*>, holder: ListHolder) {
    }

    fun postUnbind(model: ListModel<*>, holder: ListHolder) {
    }

    fun preAttach(model: ListModel<*>, holder: ListHolder) {
    }

    fun postAttach(model: ListModel<*>, holder: ListHolder) {
    }

    fun preDetach(model: ListModel<*>, holder: ListHolder) {
    }

    fun postDetach(model: ListModel<*>, holder: ListHolder) {
    }

    fun onFailedToRecycleView(model: ListModel<*>, holder: ListHolder) {
    }

}

fun ListModel<*>.doOnPreCreateHolder(block: (model: ListModel<*>) -> Unit): ListModelListener =
    addListener(preCreateHolder = block)

fun ListModel<*>.doOnPreCreateHolder(block: (model: ListModel<*>, holder: ListHolder) -> Unit): ListModelListener =
    addListener(postCreateHolder = block)

fun ListModel<*>.doOnPreCreateView(block: (model: ListModel<*>) -> Unit): ListModelListener =
    addListener(preCreateView = block)

fun ListModel<*>.doOnPostCreateView(block: (model: ListModel<*>, view: View) -> Unit): ListModelListener =
    addListener(postCreateView = block)

fun ListModel<*>.doOnPreBind(block: (model: ListModel<*>, holder: ListHolder) -> Unit): ListModelListener =
    addListener(preBind = block)

fun ListModel<*>.doOnPostBind(block: (model: ListModel<*>, holder: ListHolder) -> Unit): ListModelListener =
    addListener(postBind = block)

fun ListModel<*>.doOnPreUnbind(block: (model: ListModel<*>, holder: ListHolder) -> Unit): ListModelListener =
    addListener(preUnbind = block)

fun ListModel<*>.doOnPostUnbind(block: (model: ListModel<*>, holder: ListHolder) -> Unit): ListModelListener =
    addListener(postUnbind = block)

fun ListModel<*>.doOnPreAttach(block: (model: ListModel<*>, holder: ListHolder) -> Unit): ListModelListener =
    addListener(preAttach = block)

fun ListModel<*>.doOnPostAttach(block: (model: ListModel<*>, holder: ListHolder) -> Unit): ListModelListener =
    addListener(postAttach = block)

fun ListModel<*>.doOnPreDetach(block: (model: ListModel<*>, holder: ListHolder) -> Unit): ListModelListener =
    addListener(preDetach = block)

fun ListModel<*>.doOnPostDetach(block: (model: ListModel<*>, holder: ListHolder) -> Unit): ListModelListener =
    addListener(postDetach = block)

fun ListModel<*>.doOnFailedToRecycleView(block: (model: ListModel<*>, holder: ListHolder) -> Unit): ListModelListener =
    addListener(onFailedToRecycleView = block)

fun ListModel<*>.addListener(
    preCreateHolder: ((model: ListModel<*>) -> Unit)? = null,
    postCreateHolder: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    preCreateView: ((model: ListModel<*>) -> Unit)? = null,
    postCreateView: ((model: ListModel<*>, view: View) -> Unit)? = null,
    preBind: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    postBind: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    preUnbind: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    postUnbind: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    preAttach: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    postAttach: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    preDetach: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    postDetach: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    onFailedToRecycleView: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null
): ListModelListener = ListModelListener(
    preCreateHolder, postCreateHolder,
    preCreateView, postCreateView,
    preBind, postBind,
    preUnbind, postUnbind,
    preAttach, postAttach,
    preDetach, postDetach,
    onFailedToRecycleView
).also(this::addListener)

fun ListController.addModelListener(
    preCreateHolder: ((model: ListModel<*>) -> Unit)? = null,
    postCreateHolder: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    preCreateView: ((model: ListModel<*>) -> Unit)? = null,
    postCreateView: ((model: ListModel<*>, view: View) -> Unit)? = null,
    preBind: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    postBind: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    preUnbind: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    postUnbind: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    preAttach: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    postAttach: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    preDetach: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    postDetach: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    onFailedToRecycleView: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null
): ListModelListener = ListModelListener(
    preCreateHolder, postCreateHolder,
    preCreateView, postCreateView,
    preBind, postBind,
    preUnbind, postUnbind,
    preAttach, postAttach,
    preDetach, postDetach,
    onFailedToRecycleView
).also(this::addModelListener)

fun ListModelListener(
    preCreateHolder: ((model: ListModel<*>) -> Unit)? = null,
    postCreateHolder: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    preCreateView: ((model: ListModel<*>) -> Unit)? = null,
    postCreateView: ((model: ListModel<*>, view: View) -> Unit)? = null,
    preBind: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    postBind: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    preUnbind: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    postUnbind: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    preAttach: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    postAttach: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    preDetach: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    postDetach: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    onFailedToRecycleView: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null
): ListModelListener = LambdaListModelListener(
    preCreateHolder, postCreateHolder,
    preCreateView, postCreateView,
    preBind, postBind,
    preUnbind, postUnbind,
    preAttach, postAttach,
    preDetach, postDetach,
    onFailedToRecycleView
)

class LambdaListModelListener(
    private val preCreateHolder: ((model: ListModel<*>) -> Unit)? = null,
    private val postCreateHolder: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    private val preCreateView: ((model: ListModel<*>) -> Unit)? = null,
    private val postCreateView: ((model: ListModel<*>, view: View) -> Unit)? = null,
    private val preBind: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    private val postBind: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    private val preUnbind: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    private val postUnbind: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    private val preAttach: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    private val postAttach: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    private val preDetach: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    private val postDetach: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null,
    private val onFailedToRecycleView: ((model: ListModel<*>, holder: ListHolder) -> Unit)? = null
) : ListModelListener {

    override fun preCreateHolder(model: ListModel<*>) {
        preCreateHolder?.invoke(model)
    }

    override fun postCreateHolder(model: ListModel<*>, holder: ListHolder) {
        postCreateHolder?.invoke(model, holder)
    }

    override fun preCreateView(model: ListModel<*>) {
        preCreateView?.invoke(model)
    }

    override fun postCreateView(model: ListModel<*>, view: View) {
        postCreateView?.invoke(model, view)
    }

    override fun preBind(model: ListModel<*>, holder: ListHolder) {
        preBind?.invoke(model, holder)
    }

    override fun postBind(model: ListModel<*>, holder: ListHolder) {
        postBind?.invoke(model, holder)
    }

    override fun preUnbind(model: ListModel<*>, holder: ListHolder) {
        preUnbind?.invoke(model, holder)
    }

    override fun postUnbind(model: ListModel<*>, holder: ListHolder) {
        postUnbind?.invoke(model, holder)
    }

    override fun preAttach(model: ListModel<*>, holder: ListHolder) {
        preAttach?.invoke(model, holder)
    }

    override fun postAttach(model: ListModel<*>, holder: ListHolder) {
        postAttach?.invoke(model, holder)
    }

    override fun preDetach(model: ListModel<*>, holder: ListHolder) {
        preDetach?.invoke(model, holder)
    }

    override fun postDetach(model: ListModel<*>, holder: ListHolder) {
        postDetach?.invoke(model, holder)
    }

    override fun onFailedToRecycleView(model: ListModel<*>, holder: ListHolder) {
        onFailedToRecycleView?.invoke(model, holder)
    }
}