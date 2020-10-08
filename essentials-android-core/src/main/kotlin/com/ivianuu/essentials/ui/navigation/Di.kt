package com.ivianuu.essentials.ui.navigation

import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.merge.ApplicationComponent

@Binding(ApplicationComponent::class)
fun navigator() = Navigator()
