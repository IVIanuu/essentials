/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tschuchort.compiletesting;

import java.io.File;

public class SourceFileAccessor {
    public static File writeIfNeeded(SourceFile file, File dir) {
        //noinspection KotlinInternalInJava
        return file.writeIfNeeded$kotlin_compile_testing(dir);
    }
}
