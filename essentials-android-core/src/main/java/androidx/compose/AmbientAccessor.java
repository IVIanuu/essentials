package androidx.compose;

import org.jetbrains.annotations.Nullable;

public final class AmbientAccessor {

    @Nullable
    public static <T> T getDefaultValue(Ambient<T> key) {
        //noinspection KotlinInternalInJava
        return key.getDefaultValueHolder$compose_runtime_release().getValue();
    }

}
