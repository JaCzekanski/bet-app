package info.czekanski.bet.network

import info.czekanski.bet.network.model.Bet
import info.czekanski.bet.network.model.ReturnId
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface BetApi {

    @POST("/api/bet/{matchId}")
    fun createBet(@Path("matchId") matchId: String,
                  @Body bet: Bet,
                  @Header("Authorization") token: String): Single<ReturnId>

    @PUT("/api/bet/{betId}")
    fun updateBet(@Path("betId") betId: String,
                  @Body bet: Bet,
                  @Header("Authorization") token: String): Completable

    @DELETE("/api/bet/{betId}")
    fun deleteBet(@Path("betId") betId: String,
                  @Header("Authorization") token: String): Completable
}