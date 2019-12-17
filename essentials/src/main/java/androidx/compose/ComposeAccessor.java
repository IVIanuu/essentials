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

    public static boolean isCompositionLifecycleObserverHolder(Object object) {
        return object instanceof CompositionLifecycleObserverHolder;
    }

    public static boolean isGroupStart(Object object) {
        return object instanceof GroupStart;
    }

    public static Object[] getSlots(SlotTable table) {
        return table.getSlots$compose_runtime_release();
    }

    public static int getSlots(Object group) {
        return ((GroupStart) group).getSlots();
    }

    public static Object getKey(Object group) {
        return ((GroupStart) group).getKey();
    }

    public static CompositionLifecycleObserver getInstance(Object holder) {
        return ((CompositionLifecycleObserverHolder) holder).getInstance();
    }

}
