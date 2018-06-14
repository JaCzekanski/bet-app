package info.czekanski.bet.user

import com.google.android.gms.tasks.*
import io.reactivex.CompletableEmitter

class RxHandlerCompletable<T> private constructor(private val emitter: CompletableEmitter) : OnSuccessListener<T>, OnFailureListener, OnCompleteListener<T> {
    override fun onSuccess(p0: T) {
        this.emitter.onComplete()
    }

    override fun onComplete(task: Task<T>) {
        this.emitter.onComplete()
    }

    override fun onFailure(e: Exception) {
        if (!this.emitter.isDisposed) {
            this.emitter.onError(e)
        }

    }

    companion object {
        fun <T> assignOnTask(emitter: CompletableEmitter, task: Task<T>) {
            val handler = RxHandlerCompletable<T>(emitter)
            task.addOnSuccessListener(handler)
            task.addOnFailureListener(handler)

            try {
                task.addOnCompleteListener(handler)
            } catch (var4: Throwable) {
            }
        }
    }
}
