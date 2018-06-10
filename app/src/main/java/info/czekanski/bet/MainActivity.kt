package info.czekanski.bet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import info.czekanski.bet.domain.matches.MatchesFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MatchesFragment())
                    .commitAllowingStateLoss()
        }
    }
}
