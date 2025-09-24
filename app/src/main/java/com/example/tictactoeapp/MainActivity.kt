package com.example.tictactoeapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    var gridSize = 3
    var grid = Array(gridSize) { CharArray(gridSize) {' '} }
    var currentPlayer = 'X'
    var gameOver = false


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

    }

    fun cellClicked(row : Int, col : Int, cell : TextView) {
        if(gameOver){
            return
        }
        if(grid[row][col] != ' '){
            showText("Movimento Inválido")
            return
        }
        cell.text = currentPlayer.toString()
        grid[row][col] = currentPlayer
        val result = checkWinner()

        when(result.winner) {
            'N' -> {

            }
            'T' -> {
                showText("Empate!")
            }
            else -> {
                showText("Vencendor: $currentPlayer")
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
                if (grid[row][col] != currentPlayer) {
                    isWin = false
                    break
                }
            }

            if (isWin) {
                val line = (0 until gridSize).map { col -> Pair(row, col) }
                return WinResult(currentPlayer, line)
            }
        }

        // Columns
        for (col in 0 until gridSize) {
            isWin = true
            for (row in 0 until gridSize) {
                if (grid[row][col] != currentPlayer) {
                    isWin = false
                    break
                }
            }
            if (isWin) {
                val line = (0 until gridSize).map { row -> Pair(row, col) }
                return WinResult(currentPlayer, line)
            }
        }

        // Main diagonal
        isWin = true
        for (i in 0 until gridSize) {
            if (grid[i][i] != currentPlayer) {
                isWin = false
                break
            }
        }
        if (isWin) {
            val line = (0 until gridSize).map { i -> Pair(i, i) }
            return WinResult(currentPlayer, line)
        }

        // Anti-diagonal
        isWin = true
        for (i in 0 until gridSize) {
            if (grid[i][gridSize - 1 - i] != currentPlayer) {
                isWin = false
                break
            }
        }
        if (isWin) {
            val line = (0 until gridSize).map { i -> Pair(i, gridSize - 1 - i) }
            return WinResult(currentPlayer, line)
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

    fun swapPlayer() {
        currentPlayer = if(currentPlayer == 'X') 'O' else 'X'
    }

    fun resetGame() {
        swapPlayer()
        gameOver = false

        for(i in 0 until gridSize) {
            for(j in 0 until gridSize) {
                grid[i][j] = ' '
                val view = getGridView(i, j)
                view.text = ""
            }
        }
    }

    fun showText(text : String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    fun getGridView(row : Int, col : Int) : TextView {
        val id = resources.getIdentifier("cell$row$col", "id", packageName)
        return findViewById<TextView>(id)
    }
}