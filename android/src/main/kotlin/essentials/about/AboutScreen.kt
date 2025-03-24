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
      section {
        item {
          SectionListItem(
            title = { Text("Version") },
            description = { Text(appConfig.versionName) },
            trailing = { Icon(Icons.Default.Info, null) }
          )
        }

        item {
          SectionListItem(
            onClick = scopedAction {
              navigator().push(PlayStoreAppDetailsKey(appConfig.packageName))
            },
            title = { Text("Rate") },
            description = { Text("I'll be happy if you give me 5 stars") },
            trailing = { Icon(Icons.Default.Star, null) }
          )
        }

        if (context.screen.donationScreen != null)
          item {
            SectionListItem(
              onClick = scopedAction {
                navigator().push(context.screen.donationScreen!!)
              },
              title = { Text("Donate") },
              trailing = { Icon(Icons.Default.Favorite, null) }
            )
          }

        item {
          SectionListItem(
            onClick = scopedAction {
              navigator().push(UrlScreen("https://play.google.com/store/apps/developer?id=Manuel+Wrage"))
            },
            title = { Text("More apps") },
            description = { Text("Check out my other apps on Google Play") },
            trailing = { Icon(Icons.Default.ShoppingBag, null) }
          )
        }

        item {
          SectionListItem(
            onClick = scopedAction {
              navigator().push(UrlScreen("https://www.reddit.com/r/manuelwrageapps"))
            },
            title = { Text("Reddit") },
            description = {
              Text("If you need help or have questions, my subreddit is a good place to go")
            },
            trailing = {
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
            title = { Text("Github") },
            description = { Text("Check out my work on Github") },
            trailing = {
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
            title = { Text("Twitter") },
            description = { Text("Follow me on Twitter") },
            trailing = {
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
            title = { Text("Send feedback") },
            description = { Text(email) },
            trailing = { Icon(Icons.Default.Email, null) }
          )
        }

        if (context.screen.privacyPolicyUrl != null)
          item {
            SectionListItem(
              onClick = scopedAction {
                navigator().push(UrlScreen(context.screen.privacyPolicyUrl!!))
              },
              title = { Text("Privacy policy") },
              trailing = { Icon(Icons.Default.Policy, null) }
            )
          }
      }
    }
  }
}
