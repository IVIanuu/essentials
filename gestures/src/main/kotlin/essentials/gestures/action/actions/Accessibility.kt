package essentials.gestures.action.actions

import essentials.accessibility.*
import essentials.gestures.action.*
import injekt.*

val accessibilityActionPermissions = listOf(ActionAccessibilityPermission::class)

abstract class AccessibilityActionId(
  value: String,
  val accessibilityAction: Int
) : ActionId(value) {
  @Provide companion object {
    @Provide suspend fun <@AddOn T : AccessibilityActionId> execute(
      id: T,
      performAction: performGlobalAccessibilityAction
    ): ActionExecutorResult<BackActionId> {
      performAction(id.accessibilityAction)
    }
  }
}
