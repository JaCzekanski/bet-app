package info.czekanski.bet.network

import info.czekanski.bet.network.model.*
import io.reactivex.*
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

    @POST("/api/bet/{betId}/invite/{userId}")
    fun inviteUser(@Path("betId") betId: String,
                   @Path("userId") userId: String,
                   @Header("Authorization") token: String): Completable
}