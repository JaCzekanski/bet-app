package info.czekanski.bet.domain.match

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.*
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Toast
import info.czekanski.bet.R
import info.czekanski.bet.domain.home.utils.ItemDecorator
import info.czekanski.bet.domain.match.BetViewModel.Action
import info.czekanski.bet.domain.match.BetViewState.Step.*
import info.czekanski.bet.domain.match.summary.SummaryAdapter
import info.czekanski.bet.domain.match.summary.cells.*
import info.czekanski.bet.misc.*
import info.czekanski.bet.model.MatchState
import info.czekanski.bet.network.scoreToPair
import info.czekanski.bet.user.UserProvider
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_bet.*
import kotlinx.android.synthetic.main.layout_match_bid.*
import kotlinx.android.synthetic.main.layout_match_score.*

class BetFragment : Fragment() {
    private val userProvider by lazy { UserProvider.instance }
    private val arg by lazy { getArgument<Argument>() }

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
        viewModel.getShareLink().safeObserve(this, { openShareWindow(it.shortLink) })
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

        buttonEdit.setOnClickListener { viewModel.buttonClicked(Action.EditBet) }
    }

    private fun updateView(state: BetViewState) {
        // Misc
        if (state.match == null) {
            viewMatch.invisible()
        } else {
            viewMatch.show()
            viewMatch.bindMatch(state.match)
        }
        imageBall.show(state.step != LIST)
        buttonEdit.show(state.step == LIST && state.bet != null && state.match?.state == MatchState.BEFORE)
        progress.show(state.showLoader)

        // Steps
        layoutBid.show(state.step == BID)
        layoutScore.show(state.step == SCORE)
        recyclerView.show(state.step == LIST)

        when (state.step) {
            BID -> {
                textBid.text = "${state.bid} zł"
            }
            SCORE -> {
                textScore1.text = "${state.score.first}"
                textScore2.text = "${state.score.second}"
            }
            LIST -> {
                val cells: MutableList<Cell>
                if (state.bet == null) {
                    cells = mutableListOf()
                } else {
                    cells = mutableListOf(
                            HeaderCell(),
                            SeparatorCell()
                    )

                    // Get user id and find his bet
                    val stake = state.bet.bets[userProvider.userId]?.bid ?: 0

                    val jackpot = state.bet.bets.values
                            .mapNotNull { it.bid }
                            .reduce { acc, i -> acc + i }

                    val isAfterMatch = state.match?.state == MatchState.AFTER

                    val winnerCount = state.bet.bets.values.map { it.score }
                            .filter { it == state.match?.score }
                            .count()

                    val matchScore = state.match?.score?.scoreToPair()

                    if (isAfterMatch && winnerCount == 0) {
                        cells += NoteCell()
                    }

                    state.bet.bets.forEach {
                        val userId = it.key
                        val betEntry = it.value
                        val score = betEntry.score.scoreToPair() ?: return@forEach

                        var won: Int? = null
                        if (isAfterMatch && score == matchScore) {
                            won = jackpot/winnerCount
                        }

                        cells += EntryCell(state.nicknames.getOrDefault(userId, ". . ."), score, won)
                    }

                    cells += SeparatorCell()
                    cells += SummaryCell(stake, jackpot)
                    if (state.match?.state == MatchState.BEFORE) {
                        cells += InviteCell(showText = state.bet.bets.size < 2)
                    }
                }
                recyclerView.adapter = SummaryAdapter(cells, {
                    when (it) {
                        is InviteCell -> {
                            viewModel.buttonClicked(Action.Share)
                        }
                    }
                })
            }
        }
    }

    private fun openShareWindow(link: Uri) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, link.toString())
        intent.type = "text/plain"
        activity?.startActivity(Intent.createChooser(intent, "Udostępnij"))
    }

    @Parcelize
    data class Argument(val matchId: String? = null, val betId: String? = null) : Parcelable
}