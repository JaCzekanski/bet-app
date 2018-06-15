package info.czekanski.bet

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.*
import android.support.v7.app.AppCompatActivity
import android.util.Log
import info.czekanski.bet.domain.calendar.CalendarFragment
import info.czekanski.bet.domain.home.HomeFragment
import info.czekanski.bet.domain.login.*
import info.czekanski.bet.domain.match.BetFragment
import info.czekanski.bet.domain.profile.ProfileFragment
import info.czekanski.bet.misc.*
import info.czekanski.bet.repository.PreferencesProvider
import info.czekanski.bet.user.UserProvider
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val userProvider by lazy { UserProvider.instance }
    private val preferencesProvider by lazy { PreferencesProvider.getInstance(applicationContext)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!userProvider.loggedIn) {
            startActivity(
                    Intent(this, LoginActivity::class.java)
                            .setData(intent.data)
            )
            finish()
            return
        }

        setContentView(R.layout.activity_main)
        initMenu()

        if (parseDeeplink()) return

        if (savedInstanceState == null) {
            preferencesProvider.runCount++
            navigateWithTransition(HomeFragment())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            parseDeeplink()
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if (fragment is OnBackPressedInterface) {
            if (!fragment.onBackPressed()) super.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    private fun parseDeeplink(): Boolean {
        if (intent.data != null) {
            val uri = intent.data

            Log.d("MainActivity", "Deeplink uri: $uri")

            val result = Regex("/bet/(.*)/?\$").find(uri.path)
            if (result != null && result.groupValues.size == 2) {
                val betId = result.groupValues[1]

                navigateTo(HomeFragment())
                navigateWithTransition(BetFragment().withArgument(BetFragment.Argument(betId = betId)), addToBackStack = true)
                return true
                // Home
                // Bet
                // Login

                // Home
                // Bet
            }
        }
        return false
    }

    private fun getSelectedItem(): Int {
        val menu = bottomNavigation.menu
        for (i in 0 until bottomNavigation.menu.size()) {
            val menuItem = menu.getItem(i)
            if (menuItem.isChecked) {
                return menuItem.itemId
            }
        }
        return 0
    }

    private fun initMenu() {
        bottomNavigation.setOnNavigationItemSelectedListener listener@{
            if (getSelectedItem() == it.itemId) return@listener false
            when (it.itemId) {
                R.id.action_home -> {
                    navigateTo(HomeFragment())
                    return@listener true
                }
                R.id.action_matches -> {
                    navigateTo(CalendarFragment())
                    return@listener true
                }
                R.id.action_profile -> {
                    navigateTo(ProfileFragment())
                    return@listener true
                }
                else -> {
                    return@listener false
                }
            }
        }
    }

    private fun navigateTo(fragment: Fragment) {
        supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        navigateWithTransition(fragment)
    }
}