package info.czekanski.bet

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.*
import android.support.v7.app.AppCompatActivity
import android.util.Log
import info.czekanski.bet.domain.bets.BetsFragment
import info.czekanski.bet.domain.home.HomeFragment
import info.czekanski.bet.domain.login.*
import info.czekanski.bet.domain.game.GameFragment
import info.czekanski.bet.domain.matches.MatchesFragment
import info.czekanski.bet.domain.profile.ProfileFragment
import info.czekanski.bet.misc.*
import info.czekanski.bet.repository.*
import info.czekanski.bet.user.UserProvider
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    private val config by lazy { ConfigProvider.instance }
    private val userProvider by lazy { UserProvider.instance }
    private val preferencesProvider by lazy { PreferencesProvider.getInstance(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!userProvider.loggedIn) {
            startActivity(Intent(this, LoginActivity::class.java).setData(intent.data))
            finish()
            return
        }

        setContentView(R.layout.activity_main)
        initMenu()

        config.loadConfig()
                .doOnSubscribe { bottomNavigation.hide(); progress.show() }
                .doFinally { bottomNavigation.show(); progress.hide() }
                .subscribeBy(onComplete = {
                    if (parseDeeplink()) return@subscribeBy

                    if (savedInstanceState == null) {
                        preferencesProvider.runCount++
                        navigateWithTransition(HomeFragment())
                    }
                })
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
            intent.data = null

            Timber.d("Deeplink uri: $uri")

            val result = Regex("/bet/(.*)/?\$").find(uri.path)
            if (result != null && result.groupValues.size == 2) {
                val betId = result.groupValues[1]

                navigateTo(HomeFragment())
                navigateWithTransition(GameFragment().withArgument(GameFragment.Argument(betId = betId)), addToBackStack = true)
                return true
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
            return@listener when (it.itemId) {
                getSelectedItem() -> false
                R.id.action_home -> navigateTo(HomeFragment())
                R.id.action_bets -> navigateTo(BetsFragment())
                R.id.action_matches -> navigateTo(MatchesFragment())
                R.id.action_profile -> navigateTo(ProfileFragment())
                else -> false
            }
        }
    }

    private fun navigateTo(fragment: Fragment): Boolean {
        supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        navigateWithTransition(fragment)
        return true
    }
}