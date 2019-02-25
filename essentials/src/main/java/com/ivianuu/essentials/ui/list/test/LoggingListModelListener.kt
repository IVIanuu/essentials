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

package com.ivianuu.essentials.ui.list.test

import android.view.View
import com.ivianuu.essentials.ui.list.ListHolder
import com.ivianuu.essentials.ui.list.ListModel
import com.ivianuu.essentials.ui.list.ListModelListener
import com.ivianuu.timberktx.d

/**
 * @author Manuel Wrage (IVIanuu)
 */
class LoggingListModelListener : ListModelListener {

    override fun preCreateHolder(model: ListModel<*>) {
        super.preCreateHolder(model)
        d { "model pre create holder $model" }
    }

    override fun postCreateHolder(model: ListModel<*>, holder: ListHolder) {
        super.postCreateHolder(model, holder)
        d { "model post create holder $model $holder" }
    }

    override fun preCreateView(model: ListModel<*>) {
        super.preCreateView(model)
        d { "model pre create view $model" }
    }

    override fun postCreateView(model: ListModel<*>, view: View) {
        super.postCreateView(model, view)
        d { "model post create view $model $view" }
    }

    override fun preBind(model: ListModel<*>, holder: ListHolder) {
        super.preBind(model, holder)
        d { "model pre bind $model $holder" }
    }

    override fun postBind(model: ListModel<*>, holder: ListHolder) {
        super.postBind(model, holder)
        d { "model post bind $model $holder" }
    }

    override fun preUnbind(model: ListModel<*>, holder: ListHolder) {
        super.preUnbind(model, holder)
        d { "model pre unbind $model $holder" }
    }

    override fun postUnbind(model: ListModel<*>, holder: ListHolder) {
        super.postUnbind(model, holder)
        d { "model post unbind $model $holder" }
    }

    override fun preAttach(model: ListModel<*>, holder: ListHolder) {
        super.preAttach(model, holder)
        d { "model pre attach $model $holder" }
    }

    override fun postAttach(model: ListModel<*>, holder: ListHolder) {
        super.postAttach(model, holder)
        d { "model post attach $model $holder" }
    }

    override fun preDetach(model: ListModel<*>, holder: ListHolder) {
        super.preDetach(model, holder)
        d { "model pre detach $model $holder" }
    }

    override fun postDetach(model: ListModel<*>, holder: ListHolder) {
        super.postDetach(model, holder)
        d { "model post detach $model $holder" }
    }

    override fun onFailedToRecycleView(model: ListModel<*>, holder: ListHolder) {
        super.onFailedToRecycleView(model, holder)
        d { "model on failed to recycle view $model, $holder" }
    }

}