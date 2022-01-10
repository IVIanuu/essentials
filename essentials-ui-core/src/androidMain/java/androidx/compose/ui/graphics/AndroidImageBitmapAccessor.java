/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package androidx.compose.ui.graphics;

import android.graphics.Bitmap;

import org.jetbrains.annotations.NotNull;

public final class AndroidImageBitmapAccessor {
    @NotNull
    public static ImageBitmap createAndroidImageBitmap(Bitmap bitmap) {
        //noinspection KotlinInternalInJava
        return new AndroidImageBitmap(bitmap);
    }

    @NotNull
    public static Bitmap getBitmap(ImageBitmap imageBitmap) {
        //noinspection KotlinInternalInJava
        return ((AndroidImageBitmap) imageBitmap).getBitmap$ui_graphics_release();
    }
}
