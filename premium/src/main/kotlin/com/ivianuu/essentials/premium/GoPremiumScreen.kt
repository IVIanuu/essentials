/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.premium

import android.content.res.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.android.billingclient.api.*
import com.ivianuu.essentials.ads.*
import com.ivianuu.essentials.billing.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.insets.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.slack.circuit.foundation.internal.*

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

    @Provide fun ui(
      features: List<AppFeature>,
      fullScreenAdManager: FullScreenAdManager,
      navigator: Navigator,
      premiumVersionManager: PremiumVersionManager,
      screen: GoPremiumScreen,
      toaster: Toaster
    ) = Ui<GoPremiumScreen> {
      val premiumSkuDetails = premiumVersionManager.premiumSkuDetails.resourceState()
      val goPremium = action {
        if (premiumVersionManager.purchasePremiumVersion()) {
          navigator.pop(screen, true)
          toaster("Premium version is now active!")
        }
      }
      val tryBasicVersion = action {
        fullScreenAdManager.loadAndShowAdWithTimeout()
        navigator.pop(screen, false)
      }

      BackHandler(onBack = action {
        if (screen.allowBackNavigation) {
          fullScreenAdManager.loadAndShowAdWithTimeout()
          navigator.pop(screen, false)
        }
      })

      Surface {
        InsetsPadding {
          if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Column(
              modifier = Modifier.padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              PremiumUiHeader()

              Spacer(Modifier.height(32.dp))

              PremiumUiFeatures(Modifier.weight(1f), features)

              Spacer(Modifier.height(8.dp))

              PremiumUiFooter(
                skuDetails = premiumSkuDetails.getOrNull(),
                showTryBasicOption = screen.showTryBasicOption,
                onGoPremiumClick = goPremium,
                onTryBasicVersionClick = tryBasicVersion
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
                  skuDetails = premiumSkuDetails.getOrNull(),
                  showTryBasicOption = screen.showTryBasicOption,
                  onGoPremiumClick = goPremium,
                  onTryBasicVersionClick = tryBasicVersion
                )
              }

              Spacer(Modifier.width(16.dp))

              Column(modifier = Modifier.weight(1f)) {
                PremiumUiFeatures(Modifier.weight(1f), features)
              }
            }
          }
        }
      }
    }

    @Composable private fun PremiumUiHeader() {
      Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Icon(
          painter = painterResource(R.drawable.ic_medal),
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .size(36.dp),
          tint = MaterialTheme.colors.primary,
          contentDescription = null
        )

        Text(
          text = "Go premium",
          style = MaterialTheme.typography.h5,
          fontWeight = FontWeight.Bold
        )

        Text(
          modifier = Modifier.padding(top = 8.dp),
          text = "Unlock all features",
          style = MaterialTheme.typography.body2,
          color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
        )
      }
    }

    @Composable private fun PremiumUiFeatures(modifier: Modifier, features: List<AppFeature>) {
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
            text = "Premium",
            style = MaterialTheme.typography.button,
            color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
          )

          features.forEach { feature ->
            Icon(
              if (feature.inPremium) Icons.Default.Done
              else Icons.Default.Remove,
              modifier = Modifier
                .size(48.dp)
                .center(),
              tint = MaterialTheme.colors.primary,
              contentDescription = null
            )
          }
        }

        Column(
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            modifier = Modifier.height(32.dp),
            text = "Basic",
            style = MaterialTheme.typography.button,
            color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
          )

          features.forEach { feature ->
            Icon(
              if (feature.inPremium) Icons.Default.Remove
              else Icons.Default.Done,
              modifier = Modifier
                .size(48.dp)
                .center(),
              tint = MaterialTheme.colors.primary,
              contentDescription = null
            )
          }
        }
      }
    }

    @Composable private fun PremiumUiFooter(
      skuDetails: SkuDetails?,
      showTryBasicOption: Boolean,
      onGoPremiumClick: () -> Unit,
      onTryBasicVersionClick: () -> Unit
    ) {
      Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        if (skuDetails != null)
          Text(
          text = when (skuDetails.type.toSkuType()) {
            Sku.Type.IN_APP -> "One time fee of ${skuDetails.price}"
            Sku.Type.SUBS -> {
              if (skuDetails.freeTrialPeriod.toIso8601Duration().amount == 0) {
                "${skuDetails.price}/${skuDetails.subscriptionPeriod.toIso8601Duration().toReadableString()}"
              } else {
                "${skuDetails.price}/${skuDetails.subscriptionPeriod.toIso8601Duration().toReadableString()} " +
                    "after ${skuDetails.freeTrialPeriod.toIso8601Duration().toReadableString()} free trial"
              }
            }
          },
          style = MaterialTheme.typography.caption,
          color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
        )

        Button(
          modifier = Modifier
            .padding(top = 8.dp)
            .height(72.dp)
            .fillMaxWidth(),
          colors = ButtonDefaults.esButtonColors(backgroundColor = MaterialTheme.colors.primary),
          onClick = onGoPremiumClick
        ) {
          Text("Go premium")
        }

        if (showTryBasicOption) {
          TextButton(
            modifier = Modifier
              .padding(top = 8.dp)
              .height(72.dp)
              .fillMaxWidth(),
            onClick = onTryBasicVersionClick
          ) {
            Text("Try limited version")
          }
        }
      }
    }
  }
}
