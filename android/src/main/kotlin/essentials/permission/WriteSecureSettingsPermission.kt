/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import android.*
import android.app.*
import android.content.*
import android.content.pm.*
import android.provider.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.unit.*
import androidx.datastore.core.*
import com.github.michaelbull.result.*
import essentials.*
import essentials.compose.*
import essentials.data.*
import essentials.shell.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import splitties.coroutines.*
import kotlin.reflect.*
import kotlin.time.Duration.Companion.seconds

abstract class WriteSecureSettingsPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: (@Composable () -> Unit)? = null
) : Permission {
  @Provide companion object {
    @Provide fun <P : WriteSecureSettingsPermission> state(
      context: Application
    ): PermissionState<P> = context
      .checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED

    @Provide suspend fun <P : WriteSecureSettingsPermission> requestHandler(
      key: KClass<P>,
      navigator: Navigator
    ): PermissionRequestResult<P> {
      navigator.push(WriteSecureSettingsScreen(key))
    }
  }
}

class WriteSecureSettingsScreen(
  val permissionClass: KClass<out WriteSecureSettingsPermission>
) : CriticalUserFlowScreen<Boolean>

@Provide @Composable fun WriteSecureSettingsUi(
  adbEnabledDataStore: DataStore<AdbEnabled>,
  appConfig: AppConfig,
  developerModeDataStore: DataStore<DeveloperMode>,
  launchUi: launchUi,
  permissions: Permissions,
  screen: WriteSecureSettingsScreen,
  shell: Shell,
  showToast: showToast,
  context: ScreenContext<WriteSecureSettingsScreen> = inject
): Ui<WriteSecureSettingsScreen> {
  var currentStep by remember { mutableIntStateOf(1) }
  var completedStep by remember { mutableIntStateOf(1) }

  val openStep = { step: Int -> currentStep = step }

  EsScaffold(
    topBar = { EsAppBar { Text("WRITE_SECURE_SETTINGS") } },
    bottomBar = {
      Button(
        modifier = Modifier
          .navigationBarsPadding()
          .padding(16.dp)
          .fillMaxWidth()
          .wrapContentSize(align = Alignment.CenterEnd),
        onClick = scopedAction {
          shell.run("pm grant ${appConfig.packageName} android.permission.WRITE_SECURE_SETTINGS")
            .onSuccess {
              if (permissions.permissionState(listOf(screen.permissionClass)).first()) {
                showToast("Permission granted!")
                popWithResult<Boolean>()
              }
            }
            .onFailure {
              it.printStackTrace()
              showToast("Your device is not rooted!")
            }
        }
      ) {
        Text("Grant using root")
      }
    }
  ) {
    EsLazyColumn {
      item {
        SectionAlert(
          sectionType = SectionType.SINGLE,
          containerColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
          text = {
            Text(
              """
                The WRITE_SECURE_SETTINGS permission can be granted directly from your PC browser!  
                You don’t need to install any drivers or programs.  
                On rooted devices, you can grant the permission with a single click.
              """.trimIndent(),
              modifier = Modifier.padding(it)
            )
          }
        )
      }

      @Composable fun SectionStep(
        step: Int,
        title: @Composable RowScope.() -> Unit,
        text: @Composable ColumnScope.() -> Unit,
        actions: @Composable RowScope.() -> Unit
      ) {
        val isCurrentStep = step == currentStep
        SectionAlert(
          modifier = Modifier.fillMaxWidth(),
          onClick = { openStep(step) },
          sectionType = sectionTypeOf(step - 1, 4),
          focused = isCurrentStep,
          title = title,
          icon = {
            val iconRotation by animateFloatAsState(
              if (isCurrentStep) 180f else 0f
            )

            Icon(
              Icons.Default.ExpandMore,
              null,
              modifier = Modifier
                .weight(1f)
                .wrapContentSize(Alignment.CenterEnd)
                .rotate(iconRotation),
              tint = MaterialTheme.colorScheme.tertiary.copy(alpha = ContentAlpha.Medium)
            )
          },
          text = { textPadding ->
            AnimatedVisibility(isCurrentStep) {
              Column(modifier = Modifier.padding(textPadding)) {
                ProvideContentColorTextStyle(
                  LocalContentColor.current.copy(alpha = ContentAlpha.Medium),
                  MaterialTheme.typography.bodyMedium
                ) {
                  text()
                }

                Spacer(Modifier.height(8.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                  verticalAlignment = Alignment.CenterVertically
                ) { actions() }
              }
            }
          }
        )
      }

      item {
        SectionHeader { Text("Grant using PC") }
      }

      @Composable fun ContinueButton(text: String = "Continue") {
        Button(
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
          ),
          onClick = action {
            if (completedStep == 4)
              popWithResult(true)
            else {
              completedStep++
              currentStep = completedStep
            }
          },
          enabled = if (currentStep > completedStep) false
          else when (completedStep) {
            1 -> developerModeDataStore.data.collectAsScopedState(null).value != 0
            2 -> adbEnabledDataStore.data.collectAsScopedState(null).value != 0
            3 -> true
            4 -> produceScopedState(false) {
              while (true) {
                value = permissions.permissionState(listOf(screen.permissionClass)).first()
                delay(1.seconds)
              }
            }.value
            else -> true
          }
        ) { Text(text) }
      }

      item {
        SectionStep(
          step = 1,
          title = { Text("1. Enable Developer options") },
          text = {
            Text(
              """
                1. Click "Open About phone".
                2. Tap "Build Number" multiple times until "Developer options" are active.
              """.trimIndent()
            )
          },
          actions = {
            OutlinedButton(
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.tertiary
              ),
              onClick = scopedAction {
                raceOf(
                  {
                    navigator().push(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS).asScreen())
                      ?.onFailure { showToast("Couldn't open phone! Please open manually") }
                  },
                  { developerModeDataStore.data.first { it != 0 } }
                )
                launchUi()
              }
            ) { Text("Open about phone") }

            ContinueButton()
          }
        )
      }

      item {
        SectionStep(
          step = 2,
          title = { Text("2. Enable USB debugging") },
          text = {
            Text(
              """
                1. Click "Open Developer options".
                2. Enable "USB debugging".
              """.trimIndent()
            )
          },
          actions = {
            OutlinedButton(
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.tertiary
              ),
              onClick = scopedAction {
                raceOf(
                  {
                    navigator().push(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).asScreen())
                      ?.onFailure { showToast("Couldn\'t open developer options! Please open manually") }
                  },
                  { adbEnabledDataStore.data.first { it != 0 } }
                )
                launchUi()
              }
            ) { Text("Open developer options") }
            ContinueButton()
          }
        )
      }
      item {
        SectionStep(
          step = 3,
          title = { Text("3. Connect your phone to WebADB") },
          text = {
            Text(
              """
                1. Open "www.webadb.com" on your PC.
                2. Connect your phone to your PC.
                3. On your PC, click "Start".
                4. Click "Add device".
                5. Select *YOUR DEVICE*.
                6. Click "Connect" in the pop-up.
                7. Click "Connect" next to the "Add device" button.
                8. On your phone, tap "Allow".
                """.trimIndent()
            )
          },
          actions = { ContinueButton() }
        )
      }

      item {
        SectionStep(
          step = 4,
          title = { Text("4. Grant permission") },
          text = {
            Text(
              """
                1. On your PC, click "Interactive Shell" in the left panel.
                2. In the terminal window, type the command below and press Enter.
              """.trimIndent()
            )

            Text(
              modifier = Modifier
                .padding(top = 8.dp)
                .background(
                  LocalContentColor.current.copy(alpha = 0.12f),
                  RoundedCornerShape(4.dp)
                )
                .padding(4.dp),
              text = "pm grant ${appConfig.packageName} android.permission.WRITE_SECURE_SETTINGS",
              style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
            )
          },
          actions = { ContinueButton(text = "Complete") }
        )
      }
    }
  }
}

@Tag typealias DeveloperMode = Int

@Provide val developerModeDataStoreProvider = AndroidSettingDataStoreProvider<DeveloperMode>(
  Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
  AndroidSettingsType.GLOBAL,
  0
)

@Tag typealias AdbEnabled = Int

@Provide val adbEnabledDataStoreProvider = AndroidSettingDataStoreProvider<AdbEnabled>(
  Settings.Global.ADB_ENABLED,
  AndroidSettingsType.GLOBAL,
  0
)
