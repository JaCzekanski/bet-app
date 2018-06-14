package info.czekanski.bet.domain.login


import android.content.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.*
import android.widget.Toast
import info.czekanski.bet.R
import info.czekanski.bet.misc.*
import info.czekanski.bet.user.UserProvider
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
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

        buttonDeleteAccount.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext(), R.style.Base_Theme_MaterialComponents_Light_Dialog)
                    .setTitle("Jesteś pewien?")
                    .setMessage("Usunięcie konta jest nieodwracalne!")
                    .setNegativeButton("Anuluj", { dialogInterface, i ->
                        dialogInterface.dismiss()
                    })
                    .setPositiveButton("Usuń", { dialogInterface, i ->
                        userProvider.setNick(null)
                                .andThen(Completable.defer { userProvider.logout() })
                                .doOnSubscribe { progress.show() }
                                .doAfterTerminate {
                                    progress.hide()
                                    dialogInterface.dismiss()
                                }
                                .subscribeBy(onComplete = {
                                    Toast.makeText(context, "Konto usunięte", Toast.LENGTH_SHORT).show()
                                    requireActivity().finish()
                                    requireActivity().recreate()
                                }, onError = {
                                    Toast.makeText(context, "Problem z usuwaniem konta, spróbuj później", Toast.LENGTH_LONG).show()
                                    Log.e("ProfileFragment", "DeleteAccount", it)
                                })
                    }).create()

            dialog.show()
        }

    }

}