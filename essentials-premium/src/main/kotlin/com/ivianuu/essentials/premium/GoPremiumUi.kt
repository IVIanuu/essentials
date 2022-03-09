/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.premium

import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.android.billingclient.api.*
import com.ivianuu.essentials.ads.*
import com.ivianuu.essentials.billing.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.backpress.*
import com.ivianuu.essentials.ui.insets.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.material.esButtonColors
import com.ivianuu.essentials.ui.navigation.*

data class AppFeature(
  val title: String,
  val icon: @Composable () -> Unit,
  val inPremium: Boolean,
  val inBasic: Boolean
)

data class GoPremiumKey(
  val showTryBasicOption: Boolean,
  val allowBackNavigation: Boolean = true
) : Key<Boolean> {
  companion object {
    @Provide fun adFeatures() = AdFeatures<GoPremiumKey>(emptyList())
  }
}

@Provide val goPremiumUi = ModelKeyUi<GoPremiumKey, GoPremiumModel> {
  if (!model.allowBackNavigation)
    BackHandler {  }

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
          skuDetails = model.premiumSkuDetails.getOrNull(),
          showTryBasicOption = model.showTryBasicOption,
          onGoPremiumClick = model.goPremium,
          onTryBasicVersionClick = model.tryBasicVersion
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
            style = MaterialTheme.typography.subtitle2
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
            painterResId = if (feature.inBasic) R.drawable.es_ic_done
            else R.drawable.es_ic_remove,
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

data class GoPremiumModel(
  val features: List<AppFeature>,
  val premiumSkuDetails: Resource<SkuDetails>,
  val showTryBasicOption: Boolean,
  val allowBackNavigation: Boolean,
  val goPremium: () -> Unit,
  val tryBasicVersion: () -> Unit
)

@Provide @Composable fun goPremiumModel(
  features: List<AppFeature>,
  fullScreenAd: FullScreenAd,
  premiumVersionManager: PremiumVersionManager,
  ctx: KeyUiContext<GoPremiumKey>
) = GoPremiumModel(
  features = features,
  premiumSkuDetails = premiumVersionManager.premiumSkuDetails.bindResource(),
  showTryBasicOption = ctx.key.showTryBasicOption,
  allowBackNavigation = ctx.key.allowBackNavigation,
  goPremium = action {
    if (premiumVersionManager.purchasePremiumVersion())
      ctx.navigator.pop(ctx.key, true)
  },
  tryBasicVersion = action {
    fullScreenAd.loadAndShow()
    ctx.navigator.pop(ctx.key, false)
  }
)
