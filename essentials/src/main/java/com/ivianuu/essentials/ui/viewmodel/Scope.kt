package com.ivianuu.essentials.ui.viewmodel

import com.ivianuu.scopes.BaseScope
import com.ivianuu.scopes.cache.ScopeStore

val ViewModel.scope get() = scopeCache.get(this)

private val scopeCache = ScopeStore<ViewModel> { ViewModelScope(it) }

private class ViewModelScope(viewModel: ViewModel) : BaseScope() {

    init {
        viewModel.addListener(object : ViewModelListener {
            override fun preCleared(viewModel: ViewModel) {
                super.preCleared(viewModel)
                close()
            }
        })
    }

}