package androidx.ui.graphics;

import android.graphics.Bitmap;

public class AndroidImageAccessor {

    public static Image createAndroidImage(Bitmap bitmap) {
        //noinspection KotlinInternalInJava
        return new AndroidImage(bitmap);
    }

}
