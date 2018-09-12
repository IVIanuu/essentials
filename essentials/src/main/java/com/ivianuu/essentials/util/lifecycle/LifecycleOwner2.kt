package com.ivianuu.essentials.util.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.ivianuu.essentials.util.ext.canceledWith
import com.ivianuu.essentials.util.ext.correspondingEvent
import com.ivianuu.essentials.util.ext.disposedWith
import com.ivianuu.essentials.util.ext.observeK
import com.ivianuu.essentials.util.ext.onCompleteStub
import com.ivianuu.essentials.util.ext.onErrorStub
import com.ivianuu.essentials.util.ext.onNextStub
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Extensions for lifecycle owner
 */
interface LifecycleOwner2 : LifecycleOwner {

    fun <T> LiveData<T>.observe(onChanged: (T) -> Unit) {
        observeK(this@LifecycleOwner2, onChanged)
    }

    fun <T> LiveEvent<T>.consume(consumer: (T) -> Unit) {
        consume(this@LifecycleOwner2, consumer)
    }

    fun Completable.subscribeUi(
        event: Lifecycle.Event = lifecycle.correspondingEvent(),
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
    ) = observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onError, onComplete).disposedWith(this@LifecycleOwner2, event)

    fun <T : Any> Flowable<T>.subscribeUi(
        event: Lifecycle.Event = lifecycle.correspondingEvent(),
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub,
        onNext: (T) -> Unit = onNextStub
    ) = observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onError, onComplete, onNext).disposedWith(this@LifecycleOwner2, event)

    fun <T : Any> Maybe<T>.subscribeUi(
        event: Lifecycle.Event = lifecycle.correspondingEvent(),
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub,
        onSuccess: (T) -> Unit = onNextStub
    ) = observeOn(AndroidSchedulers.mainThread()).subscribeBy(
        onError,
        onComplete,
        onSuccess
    ).disposedWith(this@LifecycleOwner2, event)

    fun <T : Any> Observable<T>.subscribeUi(
        event: Lifecycle.Event = lifecycle.correspondingEvent(),
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub,
        onNext: (T) -> Unit = onNextStub
    ) = observeOn(AndroidSchedulers.mainThread()).subscribeBy(
        onError,
        onComplete,
        onNext
    ).disposedWith(
        this@LifecycleOwner2,
        event
    )

    fun <T : Any> Single<T>.subscribeUi(
        event: Lifecycle.Event = lifecycle.correspondingEvent(),
        onError: (Throwable) -> Unit = onErrorStub,
        onSuccess: (T) -> Unit = onNextStub
    ) = observeOn(AndroidSchedulers.mainThread()).subscribeBy(
        onError,
        onSuccess
    ).disposedWith(this@LifecycleOwner2, event)

    fun launchUi(
        event: Lifecycle.Event = lifecycle.correspondingEvent(),
        block: suspend CoroutineScope.() -> Unit
    ) = launch(context = UI, block = block).canceledWith(this, event)
}