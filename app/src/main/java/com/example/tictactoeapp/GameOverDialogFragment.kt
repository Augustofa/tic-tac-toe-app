package com.example.tictactoeapp
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

enum class DialogAction {
    PLAY_AGAIN,
    RESET_SCORES
}

class GameOverDialogFragment : DialogFragment() {

    interface GameOverListener {
        fun onDialogAction(action : DialogAction)
    }

    var listener: GameOverListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_game_over, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val winnerMessageTextView = view.findViewById<TextView>(R.id.winnerMessageTextView)
        val playAgainButton = view.findViewById<Button>(R.id.playAgainButton)
        val resetScoresButton = view.findViewById<Button>(R.id.resetScoreButton)

        val message = arguments?.getString("winner_message")
        winnerMessageTextView.text = message

        playAgainButton.setOnClickListener {
            listener?.onDialogAction(DialogAction.PLAY_AGAIN)
            dismiss()
        }

        resetScoresButton.setOnClickListener {
            listener?.onDialogAction(DialogAction.RESET_SCORES)
            dismiss()
        }
    }

    companion object {
        fun newInstance(message: String): GameOverDialogFragment {
            val args = Bundle()
            args.putString("winner_message", message)
            val fragment = GameOverDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}