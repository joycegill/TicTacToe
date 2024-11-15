package hu.ait.tictactoe.ui.theme

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ait.tictactoe.R
import kotlinx.coroutines.launch

@Composable
fun TicTacToeScreen(
    modifier: Modifier,
    viewModel: TicTacToeModel = viewModel()
) {
    val board = viewModel.board
    val currentPlayer = viewModel.currentPlayer
    val winner = viewModel.winner
    val winOCount = viewModel.winOCount
    val winXCount = viewModel.winXCount
    val gameOver = viewModel.gameOver
    // Animatable scale for the circle
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    var showWinner by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // currentPlayer is Player enum that has a name property that prints the enum
        Text(
            text = stringResource(R.string.current_player, currentPlayer.name),
            modifier = Modifier.padding(bottom = 16.dp))

        Text(
            text = stringResource(R.string.player_x, winXCount),
            modifier = Modifier.padding(bottom = 16.dp))

        Text(
            text = stringResource(R.string.player_o, winOCount),
            modifier = Modifier.padding(bottom = 16.dp))

        TicTacToeBoard(board,
            onCellClick = { boardCell ->
                viewModel.onCellClicked(boardCell)
            }, scale.value)

        Button(onClick = {
            viewModel.resetGame()
            coroutineScope.launch {
                scale.animateTo(1.5f,
                    animationSpec = tween(durationMillis = 300))
                scale.animateTo(1f)
            }
        }) {
            Text(stringResource(R.string.reset))
        }

        Button(onClick = {
            viewModel.finishGame()
            showWinner = true
            coroutineScope.launch {
                scale.animateTo(1.5f,
                    animationSpec = tween(durationMillis = 300))
                scale.animateTo(1f)
            }
        }) {
            Text(stringResource(R.string.finish))
        }
    }
}

@Composable
fun TicTacToeBoard(
    board: Array<Array<Player?>>,
    onCellClick: (BoardCell)-> Unit,
    scale: Float
) {
    val imageBackGround = ImageBitmap.imageResource(R.drawable.leo)
    val textMeasurer = rememberTextMeasurer()


    Canvas(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .aspectRatio(1.0f) // adjust height to match with the width
            .scale(scale)
            .pointerInput(key1 = Unit) {
                detectTapGestures { offset ->
                    Log.d(
                        "TAG_CLICK",
                        "Click: ${offset.x} ${offset.y}"
                    )

                    val row = (offset.y / (size.height / 3)).toInt()
                    val col = (offset.x / (size.width / 3)).toInt()

                    onCellClick(BoardCell(row, col))


                }
            }
    ) {

        // Draw the grid
        val gridSize = size.minDimension
        val thirdSize = gridSize / 3

        // draw background image
        drawImage(
            image = imageBackGround,
            srcOffset = IntOffset(0,0),
            srcSize = IntSize(imageBackGround.width, imageBackGround.height),
            dstOffset = IntOffset(0,0),
            dstSize = IntSize(gridSize.toInt(),gridSize.toInt())
        )

        // draw the grid
        for (i in 1..2) {
            drawLine(
                color = Color.Black,
                strokeWidth = 5f,
                start = Offset(thirdSize * i, 0f),
                end = Offset(thirdSize * i, gridSize)
            )

            drawLine(
                color = Color.Black,
                strokeWidth = 5f,
                start = Offset(0f, thirdSize * i),
                end = Offset(gridSize, thirdSize * i)
            )
        }

        // Draw the X and O
        for (row in 0..2) {
            for (col in 0..2) {
                val player = board[row][col]

                if (player != null) {
                    val centerX = col * thirdSize + thirdSize / 2
                    val centerY = row * thirdSize + thirdSize / 2

                    if (player == Player.X) {
                        drawLine(
                            color = Color(0xFFA7C7E7),
                            strokeWidth = 8f,
                            pathEffect = PathEffect.cornerPathEffect(4f),
                            start = Offset(
                                centerX - thirdSize / 4,
                                centerY - thirdSize / 4
                            ),
                            end = Offset(
                                centerX + thirdSize / 4,
                                centerY + thirdSize / 4
                            ),
                        )
                        drawLine(
                            color = Color(0xFFA7C7E7),
                            strokeWidth = 8f,
                            pathEffect = PathEffect.cornerPathEffect(4f),
                            start = Offset(
                                centerX + thirdSize / 4,
                                centerY - thirdSize / 4
                            ),
                            end = Offset(
                                centerX - thirdSize / 4,
                                centerY + thirdSize / 4
                            ),
                        )
                    } else {
                        drawCircle(
                            color = Color(0xFFFFC0CB),
                            style = Stroke(width = 8f),
                            center = Offset(centerX, centerY),
                            radius = (thirdSize / 4),
                        )
                    }
                }
            }
        }
    }
}