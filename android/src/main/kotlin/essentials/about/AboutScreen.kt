package essentials.about

import android.content.*
import android.net.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import compose.icons.*
import compose.icons.fontawesomeicons.*
import compose.icons.fontawesomeicons.brands.*
import essentials.*
import essentials.compose.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*

class AboutScreen(
  val donationScreen: Screen<*>?,
  val privacyPolicyUrl: String?
) : Screen<Unit>

@Provide @Composable fun AboutUi(
  appConfig: AppConfig,
  screen: AboutScreen,
  scope: Scope<ScreenScope> = inject
): Ui<AboutScreen> {
  EsScaffold(topBar = { EsAppBar { Text("About") } }) {
    EsLazyColumn {
      item {
        EsListItem(
          leadingContent = { Icon(Icons.Default.Info, null) },
          headlineContent = { Text("Version") },
          supportingContent = { Text(appConfig.versionName) }
        )
      }

      item {
        EsListItem(
          onClick = scopedAction {
            navigator().push(PlayStoreAppDetailsKey(appConfig.packageName))
          },
          leadingContent = { Icon(Icons.Default.Star, null) },
          headlineContent = { Text("Rate") },
          supportingContent = { Text("I'll be happy if you give me 5 stars") }
        )
      }

      if (screen.donationScreen != null)
        item {
          EsListItem(
            onClick = scopedAction {
              navigator().push(screen.donationScreen)
            },
            leadingContent = { Icon(Icons.Default.Favorite, null) },
            headlineContent = { Text("Donate") }
          )
        }

      item {
        EsListItem(
          onClick = scopedAction {
            navigator().push(UrlScreen("https://play.google.com/store/apps/developer?id=Manuel+Wrage"))
          },
          leadingContent = { Icon(Icons.Default.ShoppingBag, null) },
          headlineContent = { Text("More apps") },
          supportingContent = { Text("Check out my other apps on Google Play") }
        )
      }

      item {
        EsListItem(
          onClick = scopedAction {
            navigator().push(UrlScreen("https://www.reddit.com/r/manuelwrageapps"))
          },
          leadingContent = {
            Icon(
              imageVector = FontAwesomeIcons.Brands.RedditAlien,
              modifier = Modifier.size(24.dp),
              contentDescription = null
            )
          },
          headlineContent = { Text("Reddit") },
          supportingContent = {
            Text("If you need help or have questions, my subreddit is a good place to go")
          }
        )
      }

      item {
        EsListItem(
          onClick = scopedAction {
            navigator().push(UrlScreen("https://github.com/IVIanuu"))
          },
          leadingContent = {
            Icon(
              imageVector = FontAwesomeIcons.Brands.Github,
              modifier = Modifier.size(24.dp),
              contentDescription = null
            )
          },
          headlineContent = { Text("Github") },
          supportingContent = { Text("Check out my work on Github") }
        )
      }

      item {
        EsListItem(
          onClick = scopedAction {
            navigator().push(UrlScreen("https://twitter.com/IVIanuu"))
          },
          leadingContent = {
            Icon(
              imageVector = FontAwesomeIcons.Brands.Twitter,
              modifier = Modifier.size(24.dp),
              contentDescription = null
            )
          },
          headlineContent = { Text("Twitter") },
          supportingContent = { Text("Follow me on Twitter") }
        )
      }

      item {
        val email = "ivianuu@gmail.com"
        EsListItem(
          onClick = scopedAction {
            navigator().push(
              Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(
                  Intent.EXTRA_SUBJECT,
                  "Feedback for ${appConfig.appName} ${appConfig.versionName}"
                )
              }
                .asScreen()
            )
          },
          leadingContent = { Icon(Icons.Default.Email, null) },
          headlineContent = { Text("Send feedback") },
          supportingContent = { Text(email) }
        )
      }

      if (screen.privacyPolicyUrl != null)
        item {
          EsListItem(
            onClick = scopedAction {
              navigator().push(UrlScreen(screen.privacyPolicyUrl))
            },
            leadingContent = { Icon(Icons.Default.Policy, null) },
            headlineContent = { Text("Privacy policy") }
          )
        }
    }
  }
}
