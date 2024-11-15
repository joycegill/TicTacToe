package hu.ait.tictactoe.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class Player{
    X, O
}

data class BoardCell(val row: Int, val col: Int)

class TicTacToeModel : ViewModel(){
    var board by mutableStateOf(
        Array(3) {Array(3) {null as Player?} })

    var currentPlayer by mutableStateOf(Player.X)
    // The winner
    var winner by mutableStateOf<Player?>(null)
    var gameOver by mutableStateOf(false)
    var winOCount by mutableStateOf(0)
    var winXCount by mutableStateOf(0)

    fun onCellClicked(cell: BoardCell) {
        if (board[cell.row][cell.col] == null && !gameOver) {
            board[cell.row][cell.col] = currentPlayer

            if (findWinner()) { // win
                winner = currentPlayer
                if (winner == Player.X) {
                    winXCount++
                }
                else if (winner == Player.O) {
                    winOCount++
                }
                gameOver = true
            } else if (tieGame()) { // tie
                gameOver = true
            } else { // next
                // Switch players if the game is not over
                currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X
            }
        }
    }

    fun resetGame() {
        board=Array(3){Array(3){null} }
        currentPlayer = Player.X
        winner = null
        gameOver = false
    }

    fun finishGame() {
        board=Array(3){Array(3){null} }
        winner = null
        gameOver = true
        winXCount = 0
        winOCount = 0
    }

    fun findWinner(): Boolean {
        // Diagonals
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != null) {
            return true
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != null) {
            return true
        }
        for (i in 0..2) {
            // Rows
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != null) {
                return true
            }
            // Column
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != null) {
                return true
            }
        }
        return false
    }

    fun tieGame(): Boolean {
        for (row in board) {
            for (cell in row) {
                if (cell == null) {
                    return false
                }
            }
        }
        return true
    }
}