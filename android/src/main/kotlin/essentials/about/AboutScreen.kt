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
  context: ScreenContext<AboutScreen> = inject
): Ui<AboutScreen> {
  EsScaffold(topBar = { EsAppBar { Text("About") } }) {
    EsLazyColumn {
      item {
        SectionListItem(
          sectionType = SectionType.FIRST,
          headlineContent = { Text("Version") },
          supportingContent = { Text(appConfig.versionName) },
          trailingContent = { Icon(Icons.Default.Info, null) }
        )
      }

      item {
        SectionListItem(
          onClick = scopedAction {
            navigator().push(PlayStoreAppDetailsKey(appConfig.packageName))
          },
          headlineContent = { Text("Rate") },
          supportingContent = { Text("I'll be happy if you give me 5 stars") },
          trailingContent = { Icon(Icons.Default.Star, null) }
        )
      }

      if (context.screen.donationScreen != null)
        item {
          SectionListItem(
            onClick = scopedAction {
              navigator().push(context.screen.donationScreen!!)
            },
            headlineContent = { Text("Donate") },
            trailingContent = { Icon(Icons.Default.Favorite, null) }
          )
        }

      item {
        SectionListItem(
          onClick = scopedAction {
            navigator().push(UrlScreen("https://play.google.com/store/apps/developer?id=Manuel+Wrage"))
          },
          headlineContent = { Text("More apps") },
          supportingContent = { Text("Check out my other apps on Google Play") },
          trailingContent = { Icon(Icons.Default.ShoppingBag, null) }
        )
      }

      item {
        SectionListItem(
          onClick = scopedAction {
            navigator().push(UrlScreen("https://www.reddit.com/r/manuelwrageapps"))
          },
          headlineContent = { Text("Reddit") },
          supportingContent = {
            Text("If you need help or have questions, my subreddit is a good place to go")
          },
          trailingContent = {
            Icon(
              imageVector = FontAwesomeIcons.Brands.RedditAlien,
              modifier = Modifier.size(24.dp),
              contentDescription = null
            )
          }
        )
      }

      item {
        SectionListItem(
          onClick = scopedAction {
            navigator().push(UrlScreen("https://github.com/IVIanuu"))
          },
          headlineContent = { Text("Github") },
          supportingContent = { Text("Check out my work on Github") },
          trailingContent = {
            Icon(
              imageVector = FontAwesomeIcons.Brands.Github,
              modifier = Modifier.size(24.dp),
              contentDescription = null
            )
          }
        )
      }

      item {
        SectionListItem(
          onClick = scopedAction {
            navigator().push(UrlScreen("https://twitter.com/IVIanuu"))
          },
          headlineContent = { Text("Twitter") },
          supportingContent = { Text("Follow me on Twitter") },
          trailingContent = {
            Icon(
              imageVector = FontAwesomeIcons.Brands.Twitter,
              modifier = Modifier.size(24.dp),
              contentDescription = null
            )
          }
        )
      }

      item {
        val email = "ivianuu@gmail.com"
        SectionListItem(
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
          headlineContent = { Text("Send feedback") },
          supportingContent = { Text(email) },
          trailingContent = { Icon(Icons.Default.Email, null) }
        )
      }

      if (context.screen.privacyPolicyUrl != null)
        item {
          SectionListItem(
            sectionType = SectionType.LAST,
            onClick = scopedAction {
              navigator().push(UrlScreen(context.screen.privacyPolicyUrl!!))
            },
            headlineContent = { Text("Privacy policy") },
            trailingContent = { Icon(Icons.Default.Policy, null) }
          )
        }
    }
  }
}
