package info.czekanski.bet.di.module.domain

import dagger.Module
import dagger.android.ContributesAndroidInjector
import info.czekanski.bet.domain.bets.BetsFragment
import info.czekanski.bet.domain.game.GameFragment
import info.czekanski.bet.domain.home.HomeFragment
import info.czekanski.bet.domain.login.LoginFragment
import info.czekanski.bet.domain.matches.MatchesFragment
import info.czekanski.bet.domain.profile.ProfileFragment

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeMatchesFragment(): MatchesFragment

    @ContributesAndroidInjector
    abstract fun contributeBetsFragment(): BetsFragment

    @ContributesAndroidInjector
    abstract fun contributeGameFragment(): GameFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment
}