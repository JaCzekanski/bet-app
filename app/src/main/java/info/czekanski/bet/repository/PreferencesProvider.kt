package info.czekanski.bet.repository

import android.content.*

class PreferencesProvider(val sharedPreferences: SharedPreferences) {

    var runCount: Int
        get() = sharedPreferences.getInt(RUN_COUNT, 0)
        set(value) = sharedPreferences.edit().putInt(RUN_COUNT, value).apply()

    var deviceRegistered: Boolean
        get() = sharedPreferences.getBoolean(DEVICE_REGISTERED, false)
        set(value) = sharedPreferences.edit().putBoolean(DEVICE_REGISTERED, value).apply()

    companion object {
        private const val RUN_COUNT = "runCount"
        private const val DEVICE_REGISTERED = "deviceRegistered"
        var _instance: PreferencesProvider? = null

        fun getInstance(context: Context): PreferencesProvider {
            if (_instance == null) {
                _instance = PreferencesProvider(context.getSharedPreferences("preferences", Context.MODE_PRIVATE))
            }

            return _instance!!
        }

    }
}
