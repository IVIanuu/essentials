/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ads

import androidx.compose.runtime.Composable

fun interface AdsEnabledProducer {
  @Composable fun adsEnabled(): Boolean
}
