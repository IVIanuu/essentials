package com.ivianuu.essentials.sample

import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide val keyUiDecoratorLogger: KeyUiDecorator = { content ->
  content()
}
