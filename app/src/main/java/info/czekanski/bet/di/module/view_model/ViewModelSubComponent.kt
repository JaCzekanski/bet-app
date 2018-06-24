package info.czekanski.bet.di.module.view_model

import dagger.Subcomponent
import info.czekanski.bet.domain.bets.BetsViewModel
import info.czekanski.bet.domain.game.GameViewModel
import info.czekanski.bet.domain.home.HomeViewModel
import info.czekanski.bet.domain.matches.MatchesViewModel

@Subcomponent
interface ViewModelSubComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): ViewModelSubComponent
    }

    fun homeViewModel(): HomeViewModel
    fun matchesViewModel(): MatchesViewModel
    fun betsViewModel(): BetsViewModel
    fun gameViewModel(): GameViewModel
}