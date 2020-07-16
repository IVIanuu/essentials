package com.ivianuu.essentials.gestures.action.actions

/**
@Module
fun ShortcutModule() {
installIn<ApplicationComponent>()
    actionFactory<ShortcutActionFactory>()
    actionPickerDelegate<ShortcutActionPickerDelegate>()
}

@Given
internal class ShortcutActionFactory(
private val intentActionExecutorProvider: (Intent) -> IntentActionExecutor,
private val logger: Logger
) : ActionFactory {
    override fun handles(key: String): Boolean = key.startsWith(ACTION_KEY_PREFIX)
    override suspend fun createAction(key: String): Action {
logger.d { "create action from $key" }
val tmp = key.split(DELIMITER)
        val label = tmp[1]
        val intent = Intent.getIntent(tmp[2])
        val iconBytes = Base64.decode(tmp[3], 0)
        val icon = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size).toImageAsset()
        return Action(
            key = key,
            title = label,
            unlockScreen = true,
            iconProvider = SingleActionIconProvider { Icon(ImagePainter(icon)) },
            executor = intentActionExecutorProvider(intent),
            enabled = true
        )
    }
}


@Given
internal class ShortcutActionPickerDelegate(
    private val resourceProvider: ResourceProvider,
    private val shortcutPickerPage: ShortcutPickerPage
) : ActionPickerDelegate {
override val title: String
get() = getString(R.string.es_action_shortcut)
override val icon: @Composable () -> Unit = {
        Icon(vectorResource(R.drawable.es_ic_content_cut))
    }

    override suspend fun getResult(navigator: Navigator): ActionPickerResult? {
        val shortcut = navigator.push<Shortcut> { shortcutPickerPage() }
            ?: return null

        val label = shortcut.name
        val icon = shortcut.icon.toBitmap()
        val stream = ByteArrayOutputStream()
        icon.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val iconBytes = stream.toByteArray()
        val key =
            "$ACTION_KEY_PREFIX$DELIMITER$label$DELIMITER${shortcut.intent.toUri(0)}$DELIMITER${Base64.encodeToString(
                iconBytes,
                0
            )}"
        return ActionPickerResult.Action(key)
    }
}

private const val ACTION_KEY_PREFIX = "shortcut"
private const val DELIMITER = "=:="
 */