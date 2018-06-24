package info.czekanski.bet.di.module.view_model

import android.arch.lifecycle.*
import dagger.*
import dagger.multibindings.IntoMap
import info.czekanski.bet.domain.bets.BetsViewModel
import info.czekanski.bet.domain.game.GameViewModel
import info.czekanski.bet.domain.home.HomeViewModel
import info.czekanski.bet.domain.matches.MatchesViewModel


@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MatchesViewModel::class)
    abstract fun bindMathesViewModel(matchesViewModel: MatchesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GameViewModel::class)
    abstract fun bindGameViewModel(gameViewModel: GameViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BetsViewModel::class)
    abstract fun bindBetsViewModel(betsViewModel: BetsViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: DiViewModelFactory): ViewModelProvider.Factory
}