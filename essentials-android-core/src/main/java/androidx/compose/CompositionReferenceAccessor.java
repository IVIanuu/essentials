package androidx.compose;

import org.jetbrains.annotations
        .NotNull;

import java.util.Map;

public final class CompositionReferenceAccessor {

    @NotNull
    public static Map<Ambient<Object>, State<Object>> getAmbientScope(CompositionReference reference) {
        //noinspection KotlinInternalInJava
        return reference.getAmbientScope$runtime_release();
    }

}
