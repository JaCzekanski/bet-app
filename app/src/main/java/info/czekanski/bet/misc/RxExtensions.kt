package info.czekanski.bet.misc

import com.uber.autodispose.*
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers


fun <T> Flowable<T>.applySchedulers(): Flowable<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
fun <T> Maybe<T>.applySchedulers(): Maybe<T>  = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
fun <T> Single<T>.applySchedulers(): Single<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
fun Completable.applySchedulers(): Completable = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

private val onNextStub: (Any) -> Unit = {}
private val onErrorStub: (Throwable) -> Unit = { RxJavaPlugins.onError(OnErrorNotImplementedException(it)) }
private val onCompleteStub: () -> Unit = {}

/**
 * Overloaded subscribe function that allows passing named parameters
 */
fun <T : Any> ObservableSubscribeProxy<T>.subscribeBy(
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub,
        onNext: (T) -> Unit = onNextStub
): Disposable {
    return if (onError === onErrorStub && onComplete === onCompleteStub) {
        subscribe(onNext)
    } else {
        subscribe(onNext, onError, onComplete)
    }
}

/**
 * Overloaded subscribe function that allows passing named parameters
 */
fun <T : Any> FlowableSubscribeProxy<T>.subscribeBy(
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub,
        onNext: (T) -> Unit = onNextStub
): Disposable {
    return if (onError === onErrorStub && onComplete === onCompleteStub) {
        subscribe(onNext)
    } else {
        subscribe(onNext, onError, onComplete)
    }
}

/**
 * Overloaded subscribe function that allows passing named parameters
 */
fun <T : Any> SingleSubscribeProxy<T>.subscribeBy(
        onError: (Throwable) -> Unit = onErrorStub,
        onSuccess: (T) -> Unit = onNextStub
): Disposable {
    return if (onError === onErrorStub) {
        subscribe(onSuccess)
    } else {
        subscribe(onSuccess, onError)
    }
}

/**
 * Overloaded subscribe function that allows passing named parameters
 */
fun <T : Any> MaybeSubscribeProxy<T>.subscribeBy(
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub,
        onSuccess: (T) -> Unit = onNextStub
): Disposable {
    return if (onError === onErrorStub && onComplete === onCompleteStub) {
        subscribe(onSuccess)
    } else {
        subscribe(onSuccess, onError, onComplete)
    }
}


/**
 * Overloaded subscribe function that allows passing named parameters
 */
fun CompletableSubscribeProxy.subscribeBy(
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
): Disposable {
    return if (onError === onErrorStub) {
        subscribe(onComplete)
    } else {
        subscribe(onComplete, onError)
    }
}