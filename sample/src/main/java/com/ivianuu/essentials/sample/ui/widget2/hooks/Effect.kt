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

/**


/// Useful for side-effects and optionally canceling them.
///
/// [useEffect] is called synchronously on every [HookWidget.build], unless
typedef Dispose = void Function();

/// [keys] is specified. In which case [useEffect] is called again only if
/// any value inside [keys] as changed.
///
/// It takes an [effect] callback and calls it synchronously.
/// That [effect] may optionally return a function, which will be called when the [effect] is called again or if the widget is disposed.
///
/// By default [effect] is called on every [HookWidget.build] call, unless [keys] is specified.
/// In which case, [effect] is called once on the first [useEffect] call and whenever something within [keys] change/
///
/// The following example call [useEffect] to subscribes to a [Stream] and cancel the subscription when the widget is disposed.
/// ALso ifthe [Stream] change, it will cancel the listening on the previous [Stream] and listen to the new one.
///
/// ```dart
/// Stream stream;
/// useEffect(() {
///     final subscription = stream.listen(print);
///     // This will cancel the subscription when the widget is disposed
///     // or if the callback is called again.
///     return subscription.cancel;
///   },
///   // when the stream change, useEffect will call the callback again.
///   [stream],
/// );
/// ```
void useEffect(Dispose Function() effect, [List<Object> keys]) {
Hook.use(_EffectHook(effect, keys));
}

class _EffectHook extends Hook<void> {
final Dispose Function() effect;

const _EffectHook(this.effect, [List<Object> keys])
: assert(effect != null),
super(keys: keys);

@override
_EffectHookState createState() => _EffectHookState();
}

class _EffectHookState extends HookState<void, _EffectHook> {
Dispose disposer;

@override
void initHook() {
super.initHook();
scheduleEffect();
}

@override
void didUpdateHook(_EffectHook oldHook) {
super.didUpdateHook(oldHook);

if (hook.keys == null) {
if (disposer != null) {
disposer();
}
scheduleEffect();
}
}

@override
void build(BuildContext context) {}

@override
void dispose() {
if (disposer != null) {
disposer();
}
}

void scheduleEffect() {
disposer = hook.effect();
}
}

 */