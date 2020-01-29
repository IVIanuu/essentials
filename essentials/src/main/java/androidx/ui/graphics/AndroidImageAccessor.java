package androidx.ui.graphics;

import android.graphics.Bitmap;

import org.jetbrains.annotations.NotNull;

public final class AndroidImageAccessor {

    @NotNull
    public static Image createAndroidImage(Bitmap bitmap) {
        //noinspection KotlinInternalInJava
        return new AndroidImage(bitmap);
    }

}
