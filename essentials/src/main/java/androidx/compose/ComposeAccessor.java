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
public final class ComposeAccessor {

    public static Ambient getAmbientFromHolder(Object holder) {
        return ((Ambient.Holder) holder).getAmbient();
    }

    public static boolean isAmbientHolder(Object object) {
        return object instanceof Ambient.Holder;
    }

    public static Object getProviderKey() {
        return ViewComposerCommonKt.getProvider();
    }

    public static int intStackSize(Object intStack) {
        return ((IntStack) intStack).getSize();
    }

    public static int intStackPeek(Object intStack, int index) {
        return ((IntStack) intStack).peek(index);
    }
}
