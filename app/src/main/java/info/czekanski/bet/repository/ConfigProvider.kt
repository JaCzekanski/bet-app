package info.czekanski.bet.repository

import com.google.firebase.remoteconfig.*
import info.czekanski.bet.BuildConfig
import info.czekanski.bet.misc.applySchedulers
import info.czekanski.bet.user.RxHandlerCompletable
import io.reactivex.Completable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class ConfigProvider @Inject constructor(
        private val remoteConfig: FirebaseRemoteConfig
) {
    init {
        remoteConfig.setConfigSettings(FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        )
        remoteConfig.setDefaults(mapOf(
                ROUND_FLAGS to false
        ))

    }

    private fun fetch(cacheExpirationSeconds: Long): Completable {
        return Completable
                .create({ emitter -> RxHandlerCompletable.assignOnTask(emitter, remoteConfig.fetch(cacheExpirationSeconds)) })
                .timeout(3, TimeUnit.SECONDS)
                .applySchedulers()
    }

    fun loadConfig(): Completable {
        val timeout: Long = if (BuildConfig.DEBUG) 0 else 3600
        return fetch(timeout)
                .doOnComplete {
                    Timber.d("RemoteConfig fetched")
                    remoteConfig.activateFetched()
                }
                .doOnError {
                    Timber.e(it, "RemoteConfig failed")
                }
                .onErrorComplete()
    }


    val roundFlags: Boolean
        get() {
            return remoteConfig.getBoolean(ROUND_FLAGS)
        }

    companion object {
        private const val ROUND_FLAGS = "round_flags"
    }
}