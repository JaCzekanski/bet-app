package info.czekanski.bet.repository

import android.content.*
import javax.inject.Inject

class PreferencesProvider @Inject constructor(
        val sharedPreferences: SharedPreferences
) {

    var runCount: Int
        get() = sharedPreferences.getInt(RUN_COUNT, 0)
        set(value) = sharedPreferences.edit().putInt(RUN_COUNT, value).apply()

    var deviceRegistered: Boolean
        get() = sharedPreferences.getBoolean(DEVICE_REGISTERED, false)
        set(value) = sharedPreferences.edit().putBoolean(DEVICE_REGISTERED, value).apply()

    companion object {
        private const val RUN_COUNT = "runCount"
        private const val DEVICE_REGISTERED = "deviceRegistered"
    }
}
