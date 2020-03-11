# Coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
# Keep Coroutine class names. See https://github.com/Kotlin/kotlinx.coroutines/issues/657.
# This should be removed when bug is fixed.
-keepnames class kotlinx.** { *; }

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}