package info.czekanski.bet.domain.login

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
import info.czekanski.bet.R
import info.czekanski.bet.domain.calendar.CalendarFragment


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, LoginFragment())
                    .commitAllowingStateLoss()
        }

    }
}
