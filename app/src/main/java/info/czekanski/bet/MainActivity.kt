package info.czekanski.bet

import android.content.Intent
import android.os.*
import android.support.v4.app.*
import android.support.v7.app.*
import android.util.*
import info.czekanski.bet.domain.home.*
import info.czekanski.bet.domain.login.*
import info.czekanski.bet.domain.match.*
import info.czekanski.bet.misc.*
import info.czekanski.bet.user.*
import kotlinx.android.synthetic.main.activity_main.*
import android.support.design.widget.BottomNavigationView
import info.czekanski.bet.domain.calendar.CalendarFragment


class MainActivity : AppCompatActivity() {
    private val userProvider by lazy { UserProvider.instance }

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
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, HomeFragment())
                    .commitAllowingStateLoss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            parseDeeplink()
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

                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, BetFragment().withArgument(BetFragment.Argument(betId = betId)))
                        .addToBackStack(null)
                        .commitAllowingStateLoss()
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
        supportFragmentManager. popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitAllowingStateLoss()
    }

//    override fun onSupportNavigateUp() = findNavController(R.id.navHostFragment).navigateUp()
}
