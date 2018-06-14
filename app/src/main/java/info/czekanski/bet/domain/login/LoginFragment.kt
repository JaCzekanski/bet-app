package info.czekanski.bet.domain.login


import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.*
import android.support.v4.app.*
import android.text.*
import android.util.*
import android.view.*
import android.widget.*
import info.czekanski.bet.*
import info.czekanski.bet.R
import info.czekanski.bet.domain.home.*
import info.czekanski.bet.misc.*
import info.czekanski.bet.user.*
import io.reactivex.*
import io.reactivex.rxkotlin.*
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
        startActivity(
                Intent(context, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .setData(requireActivity().intent.data)
        )
        requireActivity().finish()
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