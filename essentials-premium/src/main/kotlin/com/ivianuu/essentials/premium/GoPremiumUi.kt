/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.premium

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.SkuDetails
import com.ivianuu.essentials.ads.AdBannerKeyUiBlacklistEntry
import com.ivianuu.essentials.ads.FullScreenAd
import com.ivianuu.essentials.billing.Sku
import com.ivianuu.essentials.billing.toIso8601Duration
import com.ivianuu.essentials.billing.toReadableString
import com.ivianuu.essentials.billing.toSkuType
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.flowAsResource
import com.ivianuu.essentials.resource.getOrNull
import com.ivianuu.essentials.ui.core.InsetsPadding
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.material.esButtonColors
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiComponent
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.ComponentScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

data class AppFeature(
  val title: String,
  val icon: @Composable () -> Unit,
  val inPremium: Boolean,
  val inBasic: Boolean
)

@Provide data class GoPremiumKey(val showTryBasicOption: Boolean) : Key<Boolean>

@Provide val goPremiumAdBannerKeyUiBlacklistEntry: AdBannerKeyUiBlacklistEntry<GoPremiumKey>
  get() = GoPremiumKey::class

@Provide fun goPremiumUi(model: GoPremiumModel): KeyUi<GoPremiumKey> = {
  Surface {
    InsetsPadding {
      Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        PremiumUiHeader()

        PremiumUiFeatures(model.features)

        Spacer(Modifier.weight(1f))

        PremiumUiFooter(
          skuDetails = model.premiumSkuDetails.collectAsState(null).value?.getOrNull(),
          showTryBasicOption = model.showTryBasicOption,
          onGoPremiumClick = { model.goPremium() },
          onTryBasicVersionClick = { model.tryBasicVersion() }
        )
      }
    }
  }
}

@Composable fun PremiumUiHeader() {
  Icon(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
      .size(36.dp),
    painterResId = R.drawable.es_ic_medal,
    tint = MaterialTheme.colors.primary
  )

  Text(
    textResId = R.string.go_premium_title,
    style = MaterialTheme.typography.h5,
    fontWeight = FontWeight.Bold
  )

  Text(
    modifier = Modifier.padding(top = 8.dp),
    textResId = R.string.go_premium_desc,
    style = MaterialTheme.typography.body2,
    color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
  )
}

@Composable fun PremiumUiFeatures(features: List<AppFeature>) {
  Row(
    modifier = Modifier
      .padding(top = 32.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(
      modifier = Modifier.padding(top = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      features.forEach { feature ->
        Box(
          modifier = Modifier.height(48.dp),
          contentAlignment = Alignment.Center
        ) {
          CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colors.primary,
            content = feature.icon
          )
        }
      }
    }

    Column(
      modifier = Modifier
        .padding(start = 16.dp, top = 32.dp, end = 16.dp)
        .weight(1f)
    ) {
      features.forEach {
        Box(
          modifier = Modifier.height(48.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          Text(
            text = it.title,
            maxLines = 1,
            style = MaterialTheme.typography.subtitle1
          )
        }
      }
    }

    Column(
      modifier = Modifier.padding(end = 16.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        modifier = Modifier.height(32.dp),
        textResId = R.string.premium_title,
        style = MaterialTheme.typography.button,
        color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
      )

      features.forEach { feature ->
        Box(
          modifier = Modifier.size(48.dp),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            painterResId = if (feature.inPremium) R.drawable.es_ic_done
            else R.drawable.es_ic_remove,
            tint = MaterialTheme.colors.primary
          )
        }
      }
    }

    Column(
      modifier = Modifier.padding(end = 16.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        modifier = Modifier.height(32.dp),
        textResId = R.string.basic_title,
        style = MaterialTheme.typography.button,
        color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
      )

      features.forEach { feature ->
        Box(
          modifier = Modifier.size(48.dp),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            painterResId = if (feature.inPremium) R.drawable.es_ic_remove
            else R.drawable.es_ic_done,
            tint = MaterialTheme.colors.primary
          )
        }
      }
    }
  }
}

@Composable fun PremiumUiFooter(
  skuDetails: SkuDetails?,
  showTryBasicOption: Boolean,
  onGoPremiumClick: () -> Unit,
  onTryBasicVersionClick: () -> Unit
) {
  if (skuDetails != null) {
    Text(
      text = when (skuDetails.type.toSkuType()) {
        Sku.Type.IN_APP -> stringResource(R.string.one_time_purchase_desc, skuDetails.price)
        Sku.Type.SUBS -> {
          stringResource(
            R.string.subscription_pricing_model_desc,
            skuDetails.price,
            skuDetails.subscriptionPeriod.toIso8601Duration().toReadableString(),
            skuDetails.freeTrialPeriod.toIso8601Duration().toReadableString()
          )
        }
      },
      style = MaterialTheme.typography.caption,
      color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
    )
  }

  Button(
    modifier = Modifier
      .padding(top = 8.dp)
      .height(72.dp)
      .fillMaxWidth(),
    colors = ButtonDefaults.esButtonColors(backgroundColor = MaterialTheme.colors.primary),
    onClick = onGoPremiumClick
  ) {
    Text(R.string.go_premium_title)
  }

  if (showTryBasicOption) {
    TextButton(
      modifier = Modifier
        .padding(top = 8.dp)
        .height(72.dp)
        .fillMaxWidth(),
      onClick = onTryBasicVersionClick
    ) {
      Text(R.string.try_basic_version)
    }
  }
}

@Provide class GoPremiumModel(
  val features: List<AppFeature>,
  private val fullScreenAd: FullScreenAd,
  private val key: GoPremiumKey,
  private val navigator: Navigator,
  private val premiumVersionManager: PremiumVersionManager,
  private val scope: ComponentScope<KeyUiComponent>
) {
  val premiumSkuDetails: Flow<Resource<SkuDetails>> = premiumVersionManager.premiumSkuDetails
    .flowAsResource()

  val showTryBasicOption: Boolean
    get() = key.showTryBasicOption

  fun goPremium() {
    scope.launch {
      if (premiumVersionManager.purchasePremiumVersion()) {
        navigator.pop(key, true)
      }
    }
  }

  fun tryBasicVersion() {
    scope.launch {
      fullScreenAd.loadAndShow()
      navigator.pop(key, false)
    }
  }
}
