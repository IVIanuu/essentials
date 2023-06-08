/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.ui.dialog.ConfirmationKey
import com.ivianuu.injekt.Provide

@Provide val confirmationHomeItem = HomeItem("Confirmation") {
  ConfirmationKey(
    title = "Would you like to share your private data?",
    items = listOf("YES", "NO")
  )
}
