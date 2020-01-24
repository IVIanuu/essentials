package androidx.compose;

@SuppressWarnings("KotlinInternalInJava")
public class RecomposerAccessor {

    public static void scheduleRecompose(Composer composer) {
        Recomposer.Companion.current().scheduleRecompose$compose_runtime_release(composer);
    }

}
