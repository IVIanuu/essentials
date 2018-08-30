package com.ivianuu.essentials.ui.state

import com.ivianuu.essentials.ui.base.BaseFragment

/**
 * State fragment
 */
abstract class StateFragment : BaseFragment(), StateView {

    override fun onStart() {
        super.onStart()
        postInvalidate()
    }

}