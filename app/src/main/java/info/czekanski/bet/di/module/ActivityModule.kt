package info.czekanski.bet.di.module

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.*
import dagger.android.ContributesAndroidInjector
import info.czekanski.bet.MainActivity
import info.czekanski.bet.domain.login.LoginActivity
import info.czekanski.bet.network.BetApi
import info.czekanski.bet.receiver.UpdateReceiver
import info.czekanski.bet.service.FirebaseTokenRegistrationService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeLoginActivity(): LoginActivity

    @ContributesAndroidInjector
    abstract fun contributeUpdateReceiver(): UpdateReceiver

    @ContributesAndroidInjector
    abstract fun contributeFirebaseTokenRegistrationService(): FirebaseTokenRegistrationService
}