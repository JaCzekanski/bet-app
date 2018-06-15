package info.czekanski.bet.repository

import android.content.*

class PreferencesProvider(val sharedPreferences: SharedPreferences) {

    var runCount: Int
        get() = sharedPreferences.getInt("runCount", 0)
        set(value) = sharedPreferences.edit().putInt("runCount", value).apply()

    companion object {
        var _instance: PreferencesProvider? = null

        fun getInstance(context: Context): PreferencesProvider {
            if (_instance == null) {
                _instance = PreferencesProvider(context.getSharedPreferences("preferences", Context.MODE_PRIVATE))
            }

            return _instance!!
        }

    }
}
