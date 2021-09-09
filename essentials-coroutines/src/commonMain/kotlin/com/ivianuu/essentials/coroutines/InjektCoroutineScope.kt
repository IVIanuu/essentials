package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.coroutines.InjektCoroutineScope
import com.ivianuu.injekt.scope.Scope
import com.ivianuu.injekt.scope.ScopeElement
import kotlinx.coroutines.CoroutineScope

@Provide inline fun <S : Scope> coroutineScopeElement(
  scope: S,
  scopeKey: TypeKey<S>
): @ScopeElement<S> CoroutineScope = scope.element<InjektCoroutineScope<S>>()

inline val Scope.coroutineScope: CoroutineScope
  get() = element()
