@file:Suppress("UnusedImport")

package com.ivianuu.essentials.sample

import com.ivianuu.essentials.app.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*

class SampleApp : EsApp() {
    override fun buildAppGivenScope(): AppGivenScope = createAppGivenScope()
}
