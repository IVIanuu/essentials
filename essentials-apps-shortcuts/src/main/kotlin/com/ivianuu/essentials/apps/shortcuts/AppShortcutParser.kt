/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */
package com.ivianuu.essentials.apps.shortcuts

import android.content.*
import android.content.pm.*
import android.content.res.*
import android.net.*
import org.xmlpull.v1.*

private const val NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android"
private const val TAG_SHORTCUTS = "shortcuts"
private const val TAG_SHORTCUT = "shortcut"
private const val ATTRIBUTE_SHORTCUT_ICON = "icon"
private const val ATTRIBUTE_SHORTCUT_DISABLED_MESSAGE = "shortcutDisabledMessage"
private const val ATTRIBUTE_SHORTCUT_ID = "shortcutId"
private const val ATTRIBUTE_SHORTCUT_LONG_LABEL = "shortcutLongLabel"
private const val ATTRIBUTE_SHORTCUT_SHORT_LABEL = "shortcutShortLabel"
private const val TAG_INTENT = "intent"
private const val ATTRIBUTE_ACTION = "action"
private const val ATTRIBUTE_DATA = "data"
private const val ATTRIBUTE_TARGET_CLASS = "targetClass"
private const val ATTRIBUTE_TARGET_PACKAGE = "targetPackage"

private const val ANDROID_MANIFEST_FILENAME = "AndroidManifest.xml"
private const val TAG_MANIFEST = "manifest"
private const val TAG_APPLICATION = "application"
private const val TAG_ACTIVITY = "activity"
private const val TAG_ACTIVITY_ALIAS = "activity-alias"
private const val TAG_META_DATA = "meta-data"
private const val ATTR_NAME = "name"
private const val ATTR_RESOURCE = "resource"
private const val META_APP_SHORTCUTS = "android.app.shortcuts"

internal fun parseAppShortcutMetadata(
  context: Context,
  packageName: String
): Map<ComponentName, Int> {
  val resources = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY)
    .resources
  val assets = resources.assets
  val info = context.packageManager.getApplicationInfo(
    packageName,
    PackageManager.GET_META_DATA or PackageManager.GET_SHARED_LIBRARY_FILES
  )
  val addAssetPath =
    AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
  val cookie = addAssetPath.invoke(assets, info.publicSourceDir) as Int

  check(cookie != 0) {
    "Failed adding asset path: ${info.publicSourceDir}"
  }

  return parseManifest(assets, cookie, packageName)
}

private fun parseManifest(
  assets: AssetManager,
  cookie: Int,
  packageName: String
): Map<ComponentName, Int> {
  val map = mutableMapOf<ComponentName, Int>()

  with(assets.openXmlResourceParser(cookie, ANDROID_MANIFEST_FILENAME)) {
    next()
    next()

    require(XmlPullParser.START_TAG, null, TAG_MANIFEST)

    while (next() == XmlPullParser.START_TAG) {
      if (name == TAG_APPLICATION) {
        break
      } else {
        // ignore any tag before <application />
        skip()
      }
    }

    check(name == TAG_APPLICATION) {
      "expected app but was $name for $packageName"
    }

    while (next() == XmlPullParser.START_TAG) {
      if (name == TAG_ACTIVITY || name == TAG_ACTIVITY_ALIAS) {
        parseActivity(packageName, map)
      } else {
        skip()
      }
    }
  }

  return map
}

private fun XmlPullParser.parseActivity(
  packageName: String,
  map: MutableMap<ComponentName, Int>
) {
  val activityName = getAttribute(ATTR_NAME)
  if (activityName == null) {
    skip()
    return
  }
  val componentName = ComponentName(packageName, activityName)
  var depth = 1
  while (depth != 0) {
    when (next()) {
      XmlPullParser.END_TAG -> depth--
      XmlPullParser.START_TAG -> {
        depth++
        if (name == TAG_META_DATA) {
          parseMeta(componentName, map)
          depth--
        }
      }
    }
  }
}

private fun XmlPullParser.parseMeta(
  componentName: ComponentName,
  map: MutableMap<ComponentName, Int>
) {
  require(XmlPullParser.START_TAG, null, TAG_META_DATA)
  val metaName = getAttribute(ATTR_NAME)
  if (metaName == META_APP_SHORTCUTS) {
    try {
      val resId = getAttribute(ATTR_RESOURCE)?.substring(1)?.toInt()
      if (resId != null)
        map[componentName] = resId
    } catch (ignore: NumberFormatException) {
    }
  }
  skip()
}

internal fun parseAppShortcuts(
  context: Context,
  resources: Resources,
  packageName: String,
  componentName: ComponentName,
  resId: Int
): List<AppShortcut> {
  val packageInfo = context.packageManager.getPackageInfo(
    packageName, PackageManager.GET_ACTIVITIES
  )
  val appShortcuts = mutableListOf<AppShortcut>()
  val parser = resources.getXml(resId)
  parser.next()
  parser.next()
  parser.require(XmlPullParser.START_TAG, null, TAG_SHORTCUTS)
  while (parser.next() != XmlPullParser.END_TAG) {
    parser.require(XmlPullParser.START_TAG, null, TAG_SHORTCUT)
    var id = parser.getAttribute(ATTRIBUTE_SHORTCUT_ID)
    val shortLabel = parser.getString(resources, ATTRIBUTE_SHORTCUT_SHORT_LABEL) ?: ""
    val longLabel = parser.getString(resources, ATTRIBUTE_SHORTCUT_LONG_LABEL)
    val disabledMessage = parser.getString(resources, ATTRIBUTE_SHORTCUT_DISABLED_MESSAGE)
    val icon = resources.getDrawable(parser.getResourceAttribute(ATTRIBUTE_SHORTCUT_ICON))
    var activity: Intent? = null
    var depth = 1
    while (depth != 0) {
      when (parser.next()) {
        XmlPullParser.END_TAG -> depth--
        XmlPullParser.START_TAG -> {
          depth++
          if (parser.name == TAG_INTENT) {
            val intent = parser.parseIntent(componentName)
            intent.addFlags(
              Intent.FLAG_ACTIVITY_NEW_TASK or
                  Intent.FLAG_ACTIVITY_CLEAR_TASK or
                  Intent.FLAG_ACTIVITY_TASK_ON_HOME
            )
            if (activity == null) {
              activity = intent
            }
            depth--
          }
        }
      }
    }
    if (activity == null) continue
    if (id == null) {
      id = activity.component.toString() + "_shortcut" + appShortcuts.size
    }

    if (isComponentExported(componentName, packageInfo)
      && isComponentExported(activity.component, packageInfo)
    ) {
      appShortcuts.add(
        AppShortcut(
          id,
          activity,
          packageName,
          componentName,
          shortLabel,
          longLabel,
          disabledMessage,
          icon
        )
      )
    }
  }
  return appShortcuts
}

private fun isComponentExported(
  componentName: ComponentName?,
  packageInfo: PackageInfo
): Boolean {
  val activities = packageInfo.activities
  for (activity in activities) {
    if (componentName?.className == activity.name) {
      return activity.exported
    }
  }
  return false
}

private fun XmlPullParser.parseIntent(defaultComponent: ComponentName): Intent {
  require(XmlPullParser.START_TAG, null, TAG_INTENT)
  val action = getAttribute(ATTRIBUTE_ACTION)
  val data = getAttribute(ATTRIBUTE_DATA)
  val targetClass = getAttribute(ATTRIBUTE_TARGET_CLASS)
  val targetPackage = getAttribute(ATTRIBUTE_TARGET_PACKAGE)
  val component = if (targetClass == null || targetPackage == null) {
    defaultComponent
  } else {
    ComponentName(targetPackage, targetClass)
  }
  val intent = Intent()
  intent.component = component
  if (action != null) {
    intent.action = action
  } else {
    intent.action = Intent.ACTION_MAIN
  }
  if (data != null) {
    intent.data = Uri.parse(data)
  }
  skip()
  return intent
}

private fun XmlPullParser.getString(
  resources: Resources,
  attr: String
): String? {
  val resId = getResourceAttribute(attr)
  if (resId == 0) return null
  return if (resId == -1) getAttribute(attr) else resources.getString(resId)
}

private fun XmlPullParser.getResourceAttribute(attr: String): Int {
  val value = getAttribute(attr) ?: return 0
  return if (!value.startsWith("@")) -1 else value.substring(1).toInt()
}

private fun XmlPullParser.getAttribute(attr: String): String? =
  getAttributeValue(NAMESPACE_ANDROID, attr)

private fun XmlPullParser.skip() {
  check(eventType == XmlPullParser.START_TAG)
  var depth = 1
  while (depth != 0) {
    when (next()) {
      XmlPullParser.END_TAG -> depth--
      XmlPullParser.START_TAG -> depth++
    }
  }
}
