package androidx.compose;

@SuppressWarnings("KotlinInternalInJava")
public class ComposerAccessor {

    public static Composer getCurrentComposer() {
        return ViewComposerKt.getCurrentComposer();
    }

    public static Object getInvocation() {
        return ViewComposerCommonKt.getInvocation();
    }

}
