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

package com.ivianuu.essentials.sample.ui.widget2.hooks

/*

/// Cache the instance of a complex object.
///
/// [useMemoized] will immediatly call [valueBuilder] on first call and store its result.
/// Later, when [HookWidget] rebuilds, the call to [useMemoized] will return the previously created instance without calling [valueBuilder].
///
/// A later call of [useMemoized] with different [keys] will call [useMemoized] again to create a new instance.
T useMemoized<T>(T Function() valueBuilder,
[List<Object> keys = const <dynamic>[]]) {
    return Hook.use(_MemoizedHook(
        valueBuilder,
        keys: keys,
        ));
}

class _MemoizedHook<T> extends Hook<T> {
    final T Function() valueBuilder;

    const _MemoizedHook(this.valueBuilder,
    {List<Object> keys = const <dynamic>[]})
    : assert(valueBuilder != null),
    assert(keys != null),
    super(keys: keys);

    @override
    _MemoizedHookState<T> createState() => _MemoizedHookState<T>();
}

class _MemoizedHookState<T> extends HookState<T, _MemoizedHook<T>> {
    T value;

    @override
    void initHook() {
        super.initHook();
        value = hook.valueBuilder();
    }

    @override
    T build(BuildContext context) {
        return value;
    }
}*/