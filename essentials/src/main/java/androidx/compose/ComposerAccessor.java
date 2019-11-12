/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose;

@SuppressWarnings("KotlinInternalInJava")
public class ComposerAccessor {

    public static Composer currentComposerNonNull() {
        return ViewComposerKt.getCurrentComposerNonNull();
    }

    public static boolean isComposing(Composer composer) {
        return composer.isComposing$compose_runtime_release();
    }

    public static void setComposing(Composer composer, boolean value) {
        composer.setComposing$compose_runtime_release(value);
    }

}
