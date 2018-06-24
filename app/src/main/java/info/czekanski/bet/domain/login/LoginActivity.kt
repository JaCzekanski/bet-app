package info.czekanski.bet.domain.login

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.support.DaggerAppCompatActivity
import info.czekanski.bet.R
import info.czekanski.bet.misc.navigateWithTransition


class LoginActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        if (savedInstanceState == null) navigateWithTransition(LoginFragment())
    }
}
