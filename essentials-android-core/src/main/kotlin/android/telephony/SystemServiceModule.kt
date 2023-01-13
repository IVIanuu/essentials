/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package android.telephony

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun subscriptionManager(context: Context): SubscriptionManager =
    context.getSystemService(SubscriptionManager::class.java)

  @Provide inline fun telephonyManager(context: Context): TelephonyManager =
    context.getSystemService(TelephonyManager::class.java)
}
