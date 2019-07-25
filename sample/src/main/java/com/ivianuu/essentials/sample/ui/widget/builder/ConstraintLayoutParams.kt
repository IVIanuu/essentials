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

package com.ivianuu.essentials.sample.ui.widget.builder

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout

fun <V : View> ViewWidgetBuilder<V>.updateConstraintLayoutParams(
    block: (ConstraintLayout.LayoutParams) -> Boolean
) {
    updateLayoutParams { block(it as ConstraintLayout.LayoutParams) }
}

fun <V : View> ViewWidgetBuilder<V>.layoutConstraintLeftToLeftOf(id: Int) {
    updateConstraintLayoutParams {
        if (it.leftToLeft != id) {
            it.leftToLeft = id
            true
        } else {
            false
        }
    }
}

fun <V : View> ViewWidgetBuilder<V>.layoutConstraintLeftToRightOf(id: Int) {
    updateConstraintLayoutParams {
        if (it.leftToRight != id) {
            it.leftToRight = id
            true
        } else {
            false
        }
    }
}

fun <V : View> ViewWidgetBuilder<V>.layoutConstraintTopToTopOf(id: Int) {
    updateConstraintLayoutParams {
        if (it.topToTop != id) {
            it.topToTop = id
            true
        } else {
            false
        }
    }
}

fun <V : View> ViewWidgetBuilder<V>.layoutConstraintTopToBottomOf(id: Int) {
    updateConstraintLayoutParams {
        if (it.topToBottom != id) {
            it.topToBottom = id
            true
        } else {
            false
        }
    }
}

fun <V : View> ViewWidgetBuilder<V>.layoutConstraintRightToLeftOf(id: Int) {
    updateConstraintLayoutParams {
        if (it.rightToLeft != id) {
            it.rightToLeft = id
            true
        } else {
            false
        }
    }
}

fun <V : View> ViewWidgetBuilder<V>.layoutConstraintRightToRightOf(id: Int) {
    updateConstraintLayoutParams {
        if (it.rightToRight != id) {
            it.rightToRight = id
            true
        } else {
            false
        }
    }
}

fun <V : View> ViewWidgetBuilder<V>.layoutConstraintBottomToTop(id: Int) {
    updateConstraintLayoutParams {
        if (it.bottomToTop != id) {
            it.bottomToTop = id
            true
        } else {
            false
        }
    }
}

fun <V : View> ViewWidgetBuilder<V>.layoutConstraintBottomToBottom(id: Int) {
    updateConstraintLayoutParams {
        if (it.bottomToBottom != id) {
            it.bottomToBottom = id
            true
        } else {
            false
        }
    }
}

fun <V : View> ViewWidgetBuilder<V>.layoutConstraintHorizontalBias(value: Float) {
    updateConstraintLayoutParams {
        if (it.horizontalBias != value) {
            it.horizontalBias = value
            true
        } else {
            false
        }
    }
}

fun <V : View> ViewWidgetBuilder<V>.layoutConstraintVerticalBias(value: Float) {
    updateConstraintLayoutParams {
        if (it.verticalBias != value) {
            it.verticalBias = value
            true
        } else {
            false
        }
    }
}

fun <V : View> ViewWidgetBuilder<V>.layoutConstraintCenterInParent(value: Float) {
    updateConstraintLayoutParams {
        if (it.verticalBias != value) {
            it.verticalBias = value
            true
        } else {
            false
        }
    }
}