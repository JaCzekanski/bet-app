package info.czekanski.bet.domain.match.friends

import android.support.v7.widget.RecyclerView
import android.view.View
import info.czekanski.bet.R
import info.czekanski.bet.repository.Friend
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_summary_friend.*


class FriendViewHolder(override val containerView: View, val callback: Callback) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(friend: Friend) {
        imageAvatar.setImageResource(if (friend.id.isEmpty()) R.drawable.ic_share else R.drawable.ic_account_circle)
        textNick.text = friend.name

        containerView.setOnClickListener {
            callback(friend)
        }
    }
}