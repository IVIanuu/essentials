-keep class com.ivianuu.essentials.xposed.XposedRunningKt {
  <methods>;
}

-keep class * implements de.robv.android.xposed.IXposedHookZygoteInit {
  *;
}

-keep class * implements de.robv.android.xposed.IXposedHookLoadPackage {
  *;
}
