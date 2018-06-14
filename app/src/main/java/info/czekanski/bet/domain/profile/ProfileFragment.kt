package info.czekanski.bet.domain.login


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import info.czekanski.bet.R
import info.czekanski.bet.user.UserProvider
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    val userProvider by lazy { UserProvider.instance }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textNick.text = userProvider.nick ?: "Gość"
        buttonChangeNick.setOnClickListener {
            startActivity(
                    Intent(context, LoginActivity::class.java)
            )
        }
    }

}