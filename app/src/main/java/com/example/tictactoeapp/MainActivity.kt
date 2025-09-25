package com.example.tictactoeapp

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), GameOverDialogFragment.GameOverListener {
    var gridSize = 3
    var grid = Array(gridSize) { CharArray(gridSize) {' '} }
    var currentPlayer = 0
    var symbols = arrayOf('X', 'O')
    var colors = arrayOf(R.color.blue, R.color.red)
    var gameOver = false

    var originalBackground : Drawable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val cellClickListener = View.OnClickListener { view ->
            val tag = view.tag.toString()
            val parts = tag.split(",")
            val row = parts[0].toInt()
            val col = parts[1].toInt()
            cellClicked(row, col, view as TextView)
        }

        findViewById<TextView>(R.id.cell00).setOnClickListener(cellClickListener)
        findViewById<TextView>(R.id.cell01).setOnClickListener(cellClickListener)
        findViewById<TextView>(R.id.cell02).setOnClickListener(cellClickListener)
        findViewById<TextView>(R.id.cell10).setOnClickListener(cellClickListener)
        findViewById<TextView>(R.id.cell11).setOnClickListener(cellClickListener)
        findViewById<TextView>(R.id.cell12).setOnClickListener(cellClickListener)
        findViewById<TextView>(R.id.cell20).setOnClickListener(cellClickListener)
        findViewById<TextView>(R.id.cell21).setOnClickListener(cellClickListener)
        findViewById<TextView>(R.id.cell22).setOnClickListener(cellClickListener)
        findViewById<Button>(R.id.resetBtn).setOnClickListener { resetGame() }

        originalBackground = findViewById<TextView>(R.id.cell00).background.mutate()
    }

    fun cellClicked(row : Int, col : Int, cell : TextView) {
        if(gameOver){
            return
        }
        if(grid[row][col] != ' '){
            showText("Movimento Inválido")
            return
        }
        cell.text = symbols[currentPlayer].toString()
        cell.setTextColor(ContextCompat.getColor(this, colors[currentPlayer]))
        grid[row][col] = symbols[currentPlayer]
        val result = checkWinner()

        when(result.winner) {
            'N' -> {

            }
            'T' -> {
                showText("Empate!")
                gameOverScreen(result)
                gameOver = true
            }
            else -> {
                var name = getPlayerName(currentPlayer)
                showText("Vencendor: $name")
                highlightWinLine(result)
                Handler(Looper.getMainLooper()).postDelayed({
                    awardPoints()
                    gameOverScreen(result)
                }, 500)

                gameOver = true
            }
        }

        swapPlayer()
    }

    data class WinResult(
        val winner: Char, // 'X', 'O', 'T' (tie), 'N' (none)
        val winningLine: List<Pair<Int, Int>> = emptyList()
    )

    fun checkWinner(): WinResult {
        var isWin = true
        // Rows
        for (row in 0 until gridSize) {
            isWin = true
            for (col in 0 until gridSize) {
                if (grid[row][col] != symbols[currentPlayer]) {
                    isWin = false
                    break
                }
            }

            if (isWin) {
                val line = (0 until gridSize).map { col -> Pair(row, col) }
                return WinResult(symbols[currentPlayer], line)
            }
        }

        // Columns
        for (col in 0 until gridSize) {
            isWin = true
            for (row in 0 until gridSize) {
                if (grid[row][col] != symbols[currentPlayer]) {
                    isWin = false
                    break
                }
            }
            if (isWin) {
                val line = (0 until gridSize).map { row -> Pair(row, col) }
                return WinResult(symbols[currentPlayer], line)
            }
        }

        // Main diagonal
        isWin = true
        for (i in 0 until gridSize) {
            if (grid[i][i] != symbols[currentPlayer]) {
                isWin = false
                break
            }
        }
        if (isWin) {
            val line = (0 until gridSize).map { i -> Pair(i, i) }
            return WinResult(symbols[currentPlayer], line)
        }

        // Anti-diagonal
        isWin = true
        for (i in 0 until gridSize) {
            if (grid[i][gridSize - 1 - i] != symbols[currentPlayer]) {
                isWin = false
                break
            }
        }
        if (isWin) {
            val line = (0 until gridSize).map { i -> Pair(i, gridSize - 1 - i) }
            return WinResult(symbols[currentPlayer], line)
        }

        // Tie
        var isTie = true
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                if (grid[row][col] == ' ') {
                    isTie = false
                    break
                }
            }
            if (!isTie) break
        }
        if (isTie) {
            return WinResult('T')
        }

        return WinResult('N')
    }

    fun highlightWinLine(result : WinResult) {
        for(pos in result.winningLine){
            val view = getGridView(pos.first, pos.second)
            highlightCell(view)
        }
    }

    fun highlightCell(cell : TextView) {
        val startColor = (cell.background as GradientDrawable).color?.defaultColor ?: Color.TRANSPARENT
//        val endColor = ContextCompat.getColor(this, R.color.your_highlight_color)
        val endColor = Color.GREEN

        val colorAnimator = ValueAnimator.ofArgb(startColor, endColor)
        colorAnimator.duration = 500

        colorAnimator.addUpdateListener { animator ->
            val animatedColor = animator.animatedValue as Int
            val drawable = cell.background.mutate() as GradientDrawable
            drawable.setColor(animatedColor)
        }
        colorAnimator.start()


        cell.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .alpha(0.5f)
                .setDuration(500)
                .start()
    }

    fun swapPlayer() {
        currentPlayer = (currentPlayer + 1) % 2
    }

    fun resetGame() {
        swapPlayer()
        gameOver = false

        for(i in 0 until gridSize) {
            for(j in 0 until gridSize) {
                grid[i][j] = ' '
                val view = getGridView(i, j)
                view.text = ""
                val originalColor = ContextCompat.getColor(this, R.color.light_blue)
                val drawable = view.background.mutate() as GradientDrawable
                drawable.setColor(originalColor)
                view.setBackgroundResource(R.drawable.cell_box)
                view.scaleX = 1f
                view.scaleY = 1f
                view.alpha = 1f
            }
        }
    }

    fun awardPoints() {
        val id = if(currentPlayer == 0) R.id.score_player1 else R.id.score_player2
        val scoreView = findViewById<TextView>(id)
        scoreView.text = (scoreView.text.toString().toInt() + 1).toString()
    }

    fun getPlayerName(player : Int) : String {
        val id = if(player == 0) R.id.name_player1 else R.id.name_player2
        return findViewById<EditText>(id).text.toString()
    }

    fun showText(text : String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    fun getGridView(row : Int, col : Int) : TextView {
        val id = resources.getIdentifier("cell$row$col", "id", packageName)
        return findViewById<TextView>(id)
    }

    override fun onDialogAction(action: DialogAction) {
        when (action) {
            DialogAction.PLAY_AGAIN -> {
                resetGame()
            }
            DialogAction.RESET_SCORES -> {
                recreate()
            }
        }
    }

    private fun gameOverScreen(result: WinResult) {
        val winnerName = getPlayerName(symbols.indexOf(result.winner))
        val message = when (result.winner) {
            'T' -> "Empate!"
            else -> "${winnerName} Ganhou!"
        }

        val dialog = GameOverDialogFragment.newInstance(message)
        dialog.listener = this
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, "GameOverDialog")
    }
}