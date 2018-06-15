package info.czekanski.bet.domain.match

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.*
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.*
import android.view.*
import android.widget.Toast
import info.czekanski.bet.R
import info.czekanski.bet.domain.home.cells.BetCell
import info.czekanski.bet.domain.match.BetViewModel.Action
import info.czekanski.bet.domain.match.BetViewState.Step.*
import info.czekanski.bet.domain.match.friends.FriendsAdapter
import info.czekanski.bet.domain.match.summary.SummaryAdapter
import info.czekanski.bet.domain.match.summary.cells.*
import info.czekanski.bet.misc.*
import info.czekanski.bet.model.MatchState
import info.czekanski.bet.network.scoreToPair
import info.czekanski.bet.repository.Friend
import info.czekanski.bet.user.UserProvider
import info.czekanski.bet.views.MatchView
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_bet.*
import kotlinx.android.synthetic.main.layout_match_bid.*
import kotlinx.android.synthetic.main.layout_match_score.*

class BetFragment : Fragment(), OnBackPressedInterface {
    private val userProvider by lazy { UserProvider.instance }
    private val arg by lazy { getArgument<Argument>() }
    private val summaryAdapter = SummaryAdapter(this::listCallback)

    private lateinit var viewModel: BetViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BetViewModel::class.java)

        initToolbar()
        initViews()

        viewModel.getState(arg).safeObserve(this, { updateView(it) })
        viewModel.getToast().safeObserve(this, { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() })
    }

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
    }

    private fun initViews() {
        buttonMinus.setOnClickListener { viewModel.buttonClicked(Action.BidMinus) }
        buttonPlus.setOnClickListener { viewModel.buttonClicked(Action.BidPlus) }
        buttonAccept.setOnClickListener { viewModel.buttonClicked(Action.BidAccept) }

        buttonMinus1.setOnClickListener { viewModel.buttonClicked(Action.Team1ScoreMinus) }
        buttonPlus1.setOnClickListener { viewModel.buttonClicked(Action.Team1ScorePlus) }

        buttonMinus2.setOnClickListener { viewModel.buttonClicked(Action.Team2ScoreMinus) }
        buttonPlus2.setOnClickListener { viewModel.buttonClicked(Action.Team2ScorePlus) }
        buttonAccept2.setOnClickListener { viewModel.buttonClicked(Action.ScoreAccept) }

        buttonDelete.setOnClickListener { showDeleteWarningDialog() }
        buttonEdit.setOnClickListener { viewModel.buttonClicked(Action.EditBet) }

        recyclerView.adapter = summaryAdapter
    }


    override fun onBackPressed(): Boolean {
        return viewModel.onBackPressed()
    }


    @SuppressLint("PrivateResource")
    private fun showDeleteWarningDialog() {
        val dialog = AlertDialog.Builder(requireContext(), R.style.Base_Theme_MaterialComponents_Light_Dialog)
                .setTitle("Jesteś pewien?")
                .setMessage("Zostaniesz usunięty z zakładu.")
                .setNegativeButton("Anuluj", { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                .setPositiveButton("Usuń", { dialogInterface, i ->
                    viewModel.buttonClicked(Action.DeleteBet)
                }).create()

        dialog.show()
    }

    private fun updateView(state: BetViewState) {
        if (state.closeView) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }
        // Misc
        if (state.match == null) {
            viewMatch.invisible()
        } else {
            viewMatch.show()
            viewMatch.bindMatch(state.match)
        }
        imageBall.show(state.step != LIST)
        buttonDelete.show(state.bet?.users?.contains(state.userId) == true && state.match?.state == MatchState.BEFORE)
        buttonEdit.show(state.bet?.bets?.contains(state.userId) == true && state.step == LIST && state.match?.state == MatchState.BEFORE)
        progress.show(state.showLoader)

        // Steps
        layoutBid.delayedShow(state.step == BID)
        layoutScore.delayedShow(state.step == SCORE)
        recyclerView.delayedShow(state.step == LIST)
        friendsRecyclerView.delayedShow(state.step == FRIENDS)
        commitDelayedShow()

        when (state.step) {
            BID -> {
                textBid.text = requireContext().getString(R.string.zl, state.bid)
            }
            SCORE -> {
                textScore1.text = "${state.score.first}"
                textScore2.text = "${state.score.second}"
            }
            LIST -> {
                summaryAdapter.setCells(createSummaryList(state))
            }
            FRIENDS -> {
                friendsRecyclerView.adapter = FriendsAdapter(
                        listOf(Friend.SHARE) + state.friends,
                        callback = { friend -> onFriendClicked(friend, state.shareLink) }
                )
                friendsRecyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            }
        }
    }

    private fun onFriendClicked(friend: Friend, shareLink: Uri?) {
        if (shareLink != null) {
            if (friend == Friend.SHARE) {
                openShareWindow(shareLink)
            } else {
                viewModel.shareLinkTo(friend.id)
            }
        }
        viewModel.sharedLink()
    }

    private fun listCallback(cell: Cell) {
        when (cell) {
            is InviteCell -> viewModel.buttonClicked(Action.Share)
            is BidCell -> viewModel.buttonClicked(Action.GotoBet)
        }
    }

    private fun createSummaryList(state: BetViewState): List<Cell> {
        if (state.bet == null) {
            return listOf()
        }

        val cells = mutableListOf(
                HeaderCell(),
                SeparatorCell()
        )

        val bet = state.bet

        // Get user id and find his bet
        val stake = bet.bets[userProvider.userId]?.bid ?: 0

        // How much was there to win
        val jackpot = bet.bets.values
                .mapNotNull { it.bid }
                .reduce { acc, i -> acc + i }

        // is match finished?
        val isAfterMatch = state.match?.state == MatchState.AFTER

        // How many people has hit the bet
        val winnerCount = bet.bets.values.map { it.score }
                .filter { it == state.match?.score }
                .count()

        val matchScore = state.match?.score?.scoreToPair()

        // How big was jackpot of people who win
        val winnerJackpot = bet.bets.values
                .filter { it.score.scoreToPair() == matchScore }
                .mapNotNull { it.bid }
                .sum()

        if (isAfterMatch && winnerCount == 0) {
            cells += NoteCell()
        }


        bet.bets.forEach {
            val userId = it.key
            val betEntry = it.value
            val bid = betEntry.bid
            val score = betEntry.score.scoreToPair()

            var won: Int? = null
            if (isAfterMatch && score == matchScore && bid != null) {
                val percentage = bid.toFloat() / winnerJackpot
                won = (percentage * jackpot).toInt()
            }

            cells += EntryCell(state.nicknames[userId] ?: ". . .", score, won, bid)
        }

        cells += SeparatorCell()
        cells += SummaryCell(stake, jackpot)

        val userAlreadyBid = !bet.bets[state.userId]?.score.isNullOrEmpty()
        if (state.match?.state == MatchState.BEFORE) {
            if (userAlreadyBid) cells += InviteCell(showText = bet.bets.size < 2)
            else cells += BidCell()
        }
        return cells
    }

    private fun openShareWindow(link: Uri) {
        val state = viewModel.getState().value
        var whoPlays = ""
        if (state?.match != null) {
            val t1 = MatchView.getCountryName(state.match.team1)
            val t2 = MatchView.getCountryName(state.match.team2)
            whoPlays = "($t1 : $t2)"
        }

        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, "Obstaw kto wygra $whoPlays $link")
        intent.type = "text/plain"
        activity?.startActivity(Intent.createChooser(intent, "Udostępnij"))
    }

    @Parcelize
    data class Argument(val matchId: String? = null, val betId: String? = null) : Parcelable
}