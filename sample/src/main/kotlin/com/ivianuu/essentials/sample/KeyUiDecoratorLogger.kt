package com.ivianuu.essentials.sample

import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

typealias KeyUiDecoratorLogger = KeyUiDecorator

@Provide val keyUiDecoratorLogger: KeyUiDecoratorLogger = { content ->
  content()
}
