/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import android.*
import android.content.*
import android.content.pm.*
import android.provider.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.datastore.core.DataStore
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
    @Provide fun <P : WriteSecureSettingsPermission> stateProvider(
      appContext: AppContext
    ) = PermissionStateProvider<P> {
      appContext
        .checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
    }

    @Provide fun <P : WriteSecureSettingsPermission> requestHandler(
      navigator: Navigator,
      key: KClass<P>
    ) = PermissionRequestHandler<P> {
      navigator.push(WriteSecureSettingsScreen(key))
    }
  }
}

class WriteSecureSettingsScreen(
  val permissionClass: KClass<out WriteSecureSettingsPermission>
) : CriticalUserFlowScreen<Boolean> {
  @Provide companion object {
    @Provide fun ui(
      adbEnabledDataStore: DataStore<AdbEnabled>,
      appUiStarter: AppUiStarter,
      appConfig: AppConfig,
      navigator: Navigator,
      developerModeDataStore: DataStore<DeveloperMode>,
      permissionManager: PermissionManager,
      screen: WriteSecureSettingsScreen,
      shell: Shell,
      toaster: Toaster
    ) = Ui<WriteSecureSettingsScreen> {
      var currentStep by remember { mutableIntStateOf(1) }
      var completedStep by remember { mutableIntStateOf(1) }

      @Composable fun ContinueButton(text: String = "Continue") {
        Button(
          onClick = action {
            if (completedStep == 4)
              navigator.pop(screen, true)
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
                value = permissionManager.permissionState(listOf(screen.permissionClass)).first()
                delay(1.seconds)
              }
            }.value
            else -> true
          }
        ) { Text(text) }
      }

      val openStep = { step: Int -> currentStep = step }

      EsScaffold(
        topBar = { EsAppBar { Text("PC instructions") } },
        bottomBar = {
          Snackbar(
            modifier = Modifier
              .navigationBarsPadding()
              .padding(16.dp),
            action = {
              TextButton(onClick = scopedAction {
                shell.run("pm grant ${appConfig.packageName} android.permission.WRITE_SECURE_SETTINGS")
                  .onRight {
                    if (permissionManager.permissionState(listOf(screen.permissionClass)).first()) {
                      toaster.toast("Permission granted!")
                      navigator.pop(screen)
                    }
                  }
                  .onLeft {
                    it.printStackTrace()
                    toaster.toast("Your device is not rooted!")
                  }
              }) {
                Text("Grant")
              }
            }
          ) {
            Text("Grant permission using root")
          }
        }
      ) {
        EsLazyColumn {
          item {
            Text(
              text = "The WRITE_SECURE_SETTINGS permission can be granted from the browser on your PC! " +
                  "You don\'t have to install any drivers or programs.\n" +
                  "You can grant the permission with a single click on rooted devices.",
              modifier = Modifier.padding(all = 16.dp),
              style = MaterialTheme.typography.bodyMedium
            )
          }

          item {
            Step(
              step = 1,
              isCompleted = completedStep > 1,
              isCurrent = currentStep == 1,
              onClick = { openStep(1) },
              title = { Text("Enable Developer options") },
              content = {
                Text(
                  text = ") Click \"Open about phone\"\n" +
                      "2) Click \"Build Number\" multiple times until the \"Developer options\" are active",
                  style = MaterialTheme.typography.bodyMedium
                )
              },
              actions = {
                ContinueButton()
                OutlinedButton(onClick = scopedAction {
                  raceOf(
                    {
                      navigator.push(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS).asScreen())
                        ?.onLeft { toaster.toast("Couldn't open phone! Please open manually") }
                    },
                    { developerModeDataStore.data.first { it != 0 } }
                  )
                  appUiStarter.startAppUi()
                }) { Text("Open about phone") }
              }
            )
          }

          item {
            Step(
              step = 2,
              isCompleted = completedStep > 2,
              isCurrent = currentStep == 2,
              onClick = { openStep(2) },
              title = { Text("Enable USB debugging") },
              content = {
                Text(
                  text = "1) Click \"Open developer options\"\n" +
                      "2) Enable \"USB debugging\"",
                  style = MaterialTheme.typography.bodyMedium
                )
              },
              actions = {
                ContinueButton()
                OutlinedButton(onClick = scopedAction {
                  raceOf(
                    {
                      navigator.push(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).asScreen())
                        ?.onLeft { toaster.toast("Couldn\'t open developer options! Please open manually") }
                    },
                    { adbEnabledDataStore.data.first { it != 0 } }
                  )
                  appUiStarter.startAppUi()
                }) {
                  Text("Open developer options")
                }
              }
            )
          }

          item {
            Step(
              step = 3,
              isCompleted = completedStep > 3,
              isCurrent = currentStep == 3,
              onClick = { openStep(3) },
              title = { Text("Connect your phone to WebADB") },
              content = {
                Text(
                  "1) Open \"www.webadb.com\" on your PC\n" +
                      "2) Connect your phone with your PC\n" +
                      "3) On your PC click \"Start\"\n" +
                      "4) Click \"Add device\"\n" +
                      "5) Click *YOUR DEVICE*\\n" +
                      "6) Click \"Connect\" on the pop up\n" +
                      "7) Click \"Connect\" next to the \"Add device\" button\n" +
                      "8) On your phone click \"Allow\""
                )
              },
              actions = { ContinueButton() }
            )
          }

          item {
            Step(
              step = 4,
              isCompleted = completedStep > 4,
              isCurrent = currentStep == 4,
              onClick = { openStep(4) },
              title = { Text("Grant permission") },
              content = {
                Text("1) On your PC click \"Interactive shell\" in the left panel" +
                    "\n2) In the terminal window type the command below and hit the enter button")

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
  }
}

@Tag typealias DeveloperMode = Int

@Provide val developerModeSettingModule: AndroidSettingModule<DeveloperMode> = AndroidSettingModule(
  Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
  AndroidSettingsType.GLOBAL,
  0
)

@Tag typealias AdbEnabled = Int

@Provide val adbEnabledSettingModule: AndroidSettingModule<AdbEnabled> = AndroidSettingModule(
  Settings.Global.ADB_ENABLED,
  AndroidSettingsType.GLOBAL,
  0
)
