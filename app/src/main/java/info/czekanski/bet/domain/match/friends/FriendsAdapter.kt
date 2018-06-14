package info.czekanski.bet.domain.match.friends

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import info.czekanski.bet.R
import info.czekanski.bet.misc.inflate
import info.czekanski.bet.repository.Friend

class FriendsAdapter(
        private var friends: List<Friend> = listOf(),
        private val callback: Callback = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            FriendViewHolder(parent.inflate(R.layout.holder_summary_friend), callback)

    override fun getItemCount() = friends.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            (holder as FriendViewHolder).bind(friends[position])
}

typealias Callback = (Friend) -> Unit