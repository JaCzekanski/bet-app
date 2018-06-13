package info.czekanski.bet

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import info.czekanski.bet.domain.home.HomeFragment
import info.czekanski.bet.domain.login.LoginFragment
import info.czekanski.bet.domain.match.MatchFragment
import info.czekanski.bet.domain.match.withArgument
import info.czekanski.bet.model.Match
import info.czekanski.bet.user.UserProvider


class MainActivity : AppCompatActivity() {
    val userProvider by lazy { UserProvider.instance }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (intent.data != null) {
            val uri = intent.data

            Log.d("MainActivity", "Deeplink uri: $uri")

            val result = Regex("/bet/(.*)/?\$").find(uri.path)
            if (result != null && result.groupValues.size == 2) {
                val betId = result.groupValues[1]

                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, MatchFragment().withArgument(MatchFragment.Argument(betId = betId)))
                        .commitAllowingStateLoss()

                return
                // Home
                // Bet
                // Login

                // Home
                // Bet
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, getStartingFragment())
                    .commitAllowingStateLoss()
        }

//        bottomNavigation.setupWithNavController(findNavController(R.id.navHostFragment))
    }

    fun getStartingFragment(): Fragment {
        if (!userProvider.loggedIn) return LoginFragment()
        return HomeFragment()
    }

//    override fun onSupportNavigateUp() = findNavController(R.id.navHostFragment).navigateUp()
}
