package info.czekanski.bet.di.module

import android.app.Application
import android.content.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.*
import info.czekanski.bet.MyApplication
import info.czekanski.bet.network.BetApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
abstract class ContextModule {

    @Binds
    @Singleton
    abstract fun context(app: MyApplication): Context

}