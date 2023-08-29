/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.premium

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.SkuDetails
import com.ivianuu.essentials.ads.AdFeatures
import com.ivianuu.essentials.ads.FullScreenAdManager
import com.ivianuu.essentials.billing.Sku
import com.ivianuu.essentials.billing.toIso8601Duration
import com.ivianuu.essentials.billing.toReadableString
import com.ivianuu.essentials.billing.toSkuType
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.collectAsResourceState
import com.ivianuu.essentials.resource.getOrNull
import com.ivianuu.essentials.ui.backpress.BackHandler
import com.ivianuu.essentials.ui.insets.InsetsPadding
import com.ivianuu.essentials.ui.layout.align
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.material.esButtonColors
import com.ivianuu.essentials.ui.navigation.CriticalUserFlowScreen
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

data class AppFeature(
  val title: String,
  val icon: @Composable () -> Unit,
  val inPremium: Boolean,
  val inBasic: Boolean
)

class GoPremiumScreen(
  val showTryBasicOption: Boolean,
  val allowBackNavigation: Boolean = true
) : CriticalUserFlowScreen<Boolean> {
  @Provide companion object {
    @Provide fun adFeatures() = AdFeatures<GoPremiumScreen>(emptyList())
  }
}

@Provide val goPremiumUi = Ui<GoPremiumScreen, GoPremiumModel> { model ->
  BackHandler(onBackPress = model.goBack)

  Surface {
    InsetsPadding {
      if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          PremiumUiHeader()

          Spacer(Modifier.height(32.dp))

          PremiumUiFeatures(Modifier.weight(1f), model.features)

          Spacer(Modifier.height(8.dp))

          PremiumUiFooter(
            skuDetails = model.premiumSkuDetails.getOrNull(),
            showTryBasicOption = model.showTryBasicOption,
            onGoPremiumClick = model.goPremium,
            onTryBasicVersionClick = model.tryBasicVersion
          )
        }
      } else {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            PremiumUiHeader()

            Spacer(Modifier.height(8.dp))

            PremiumUiFooter(
              skuDetails = model.premiumSkuDetails.getOrNull(),
              showTryBasicOption = model.showTryBasicOption,
              onGoPremiumClick = model.goPremium,
              onTryBasicVersionClick = model.tryBasicVersion
            )
          }

          Spacer(Modifier.width(16.dp))

          Column(modifier = Modifier.weight(1f)) {
            PremiumUiFeatures(Modifier.weight(1f), model.features)
          }
        }
      }
    }
  }
}

@Composable fun PremiumUiHeader() {
  Column(
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Icon(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .size(36.dp),
      painterResId = R.drawable.es_ic_medal,
      tint = MaterialTheme.colors.primary
    )

    Text(
      textResId = R.string.es_go_premium_title,
      style = MaterialTheme.typography.h5,
      fontWeight = FontWeight.Bold
    )

    Text(
      modifier = Modifier.padding(top = 8.dp),
      textResId = R.string.es_go_premium_desc,
      style = MaterialTheme.typography.body2,
      color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
    )
  }
}

@Composable fun PremiumUiFeatures(modifier: Modifier, features: List<AppFeature>) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState())
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
        Text(
          modifier = Modifier
            .height(48.dp)
            .align(Alignment.CenterStart),
          text = it.title,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.subtitle2
        )
      }
    }

    Column(
      modifier = Modifier.padding(end = 16.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        modifier = Modifier.height(32.dp),
        textResId = R.string.es_premium_title,
        style = MaterialTheme.typography.button,
        color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
      )

      features.forEach { feature ->
        Icon(
          modifier = Modifier
            .size(48.dp)
            .center(),
          painterResId = if (feature.inPremium) R.drawable.es_ic_done
          else R.drawable.es_ic_remove,
          tint = MaterialTheme.colors.primary
        )
      }
    }

    Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        modifier = Modifier.height(32.dp),
        textResId = R.string.es_basic_title,
        style = MaterialTheme.typography.button,
        color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
      )

      features.forEach { feature ->
        Icon(
          modifier = Modifier
            .size(48.dp)
            .center(),
          painterResId = if (feature.inBasic) R.drawable.es_ic_done
          else R.drawable.es_ic_remove,
          tint = MaterialTheme.colors.primary
        )
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
  Column(
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    if (skuDetails != null) {
      Text(
        text = when (skuDetails.type.toSkuType()) {
          Sku.Type.IN_APP -> stringResource(R.string.es_one_time_purchase_desc, skuDetails.price)
          Sku.Type.SUBS -> {
            if (skuDetails.freeTrialPeriod.toIso8601Duration().amount == 0) {
              stringResource(
                R.string.es_subscription_pricing_model_desc,
                skuDetails.price,
                skuDetails.subscriptionPeriod.toIso8601Duration().toReadableString()
              )
            } else {
              stringResource(
                R.string.es_subscription_pricing_model_with_trial_desc,
                skuDetails.price,
                skuDetails.subscriptionPeriod.toIso8601Duration().toReadableString(),
                skuDetails.freeTrialPeriod.toIso8601Duration().toReadableString()
              )
            }
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
      Text(R.string.es_go_premium_title)
    }

    if (showTryBasicOption) {
      TextButton(
        modifier = Modifier
          .padding(top = 8.dp)
          .height(72.dp)
          .fillMaxWidth(),
        onClick = onTryBasicVersionClick
      ) {
        Text(R.string.es_try_basic_version)
      }
    }
  }
}

data class GoPremiumModel(
  val features: List<AppFeature>,
  val premiumSkuDetails: Resource<SkuDetails>,
  val showTryBasicOption: Boolean,
  val goPremium: () -> Unit,
  val tryBasicVersion: () -> Unit,
  val goBack: () -> Unit
)

@Provide fun goPremiumModel(
  features: List<AppFeature>,
  fullScreenAdManager: FullScreenAdManager,
  navigator: Navigator,
  premiumVersionManager: PremiumVersionManager,
  screen: GoPremiumScreen,
  toaster: Toaster
) = Model {
  GoPremiumModel(
    features = features,
    premiumSkuDetails = premiumVersionManager.premiumSkuDetails.collectAsResourceState().value,
    showTryBasicOption = screen.showTryBasicOption,
    goPremium = action {
      if (premiumVersionManager.purchasePremiumVersion()) {
        navigator.pop(screen, true)
        toaster(R.string.es_premium_activated)
      }
    },
    tryBasicVersion = action {
      navigator.pop(screen, false)
      withContext(NonCancellable) {
        fullScreenAdManager.loadAndShowAd()
      }
    },
    goBack = action {
      if (screen.allowBackNavigation) {
        navigator.pop(screen, false)
        withContext(NonCancellable) {
          fullScreenAdManager.loadAndShowAd()
        }
      }
    }
  )
}