package info.czekanski.bet.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class BetService(val api: BetApi) {

    companion object {
        val instance by lazy {
            val retrofit = Retrofit.Builder()
                    .baseUrl("https://bet.czekanski.info/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()

            val api = retrofit.create(BetApi::class.java)

            return@lazy BetService(api)
        }
    }
}