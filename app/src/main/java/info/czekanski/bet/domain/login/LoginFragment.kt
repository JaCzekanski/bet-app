package info.czekanski.bet.domain.login


import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import info.czekanski.bet.R
import info.czekanski.bet.R.id.*
import info.czekanski.bet.domain.home.HomeFragment
import info.czekanski.bet.domain.match.hide
import info.czekanski.bet.domain.match.show
import info.czekanski.bet.domain.match.v
import info.czekanski.bet.user.UserProvider
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {
    val loading = MutableLiveData<Boolean>()
    val userProvider by lazy { UserProvider.instance }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonLogin.setOnClickListener {
            userProvider.login()
                    .andThen(Completable.defer { userProvider.setNick(textNick.text.toString().trim()) })
                    .doOnSubscribe { loading.postValue(true) }
                    .doAfterTerminate { loading.postValue(false) }
                    .subscribeBy(onComplete = {
                        goToHome()
                    }, onError = {
                        Log.e("LoginFragment", "login", it)
                        goToHome()
                    })
        }

        textNick.onTextChange { updateView() }
        loading.observe(this, Observer { if (it != null) updateView() })


        loading.postValue(false)
    }

    private fun updateView() {
        textNick.isEnabled = !loading.v
        buttonLogin.isEnabled = textNick.text?.isNotEmpty() == true && !loading.v
        progress.show(loading.v)
    }

    private fun goToHome() {
        requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment())
                .commitNowAllowingStateLoss()
    }
}


fun EditText.onTextChange(callback: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (p0 != null) {
                callback(p0.toString())
            }
        }
    })
}