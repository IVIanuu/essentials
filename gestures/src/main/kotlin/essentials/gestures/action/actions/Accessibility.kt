package essentials.gestures.action.actions

import essentials.accessibility.performGlobalAccessibilityAction
import essentials.gestures.action.ActionAccessibilityPermission
import essentials.gestures.action.ActionId
import essentials.gestures.action.ExecuteActionResult
import injekt.AddOn
import injekt.Provide

val accessibilityActionPermissions = listOf(ActionAccessibilityPermission::class)

abstract class AccessibilityActionId(
  value: String,
  val accessibilityAction: Int
) : ActionId(value) {
  @Provide companion object {
    @Provide suspend fun <@AddOn T : AccessibilityActionId> execute(
      id: T,
      performAction: performGlobalAccessibilityAction
    ): ExecuteActionResult<BackActionId> {
      performAction(id.accessibilityAction)
    }
  }
}
