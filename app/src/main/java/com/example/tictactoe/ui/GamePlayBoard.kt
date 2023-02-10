package com.example.tictactoe.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.Data.ControllerState
import com.example.tictactoe.Data.T3ViewModel
import com.example.tictactoe.Data.TileState
import com.example.tictactoe.Data.TileValue
import com.example.tictactoe.ui.theme.*

@Composable
fun TicTacToeBoard(
    modifier: Modifier = Modifier,
    listOfTileStates: List<TileState>?,
    viewModel: T3ViewModel,
    arrowState: State<ControllerState?>
) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(10.dp)
            .fillMaxSize(),
    ) {
        Text(
            "Complete a row, diagonal or column",
            fontFamily = playerTextFont4,
            color = Color.DarkGray
        )
        for (i in 1..3) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(10.dp)
            ) {
                for (j in 1..3) {
                    val currentIndex = (i - 1) * 3 + (j - 1)
                    listOfTileStates?.getOrNull(currentIndex).let { tileState ->
                        Tile(
                            // UserEvent -> if tile is chosen and not occupied update the state
                            // given a bool based on players turn
                            // and a current index 0 through 8
                            onChooseTile = { bool ->
                                if (!tileState?.tileIsOccupied!!) {
                                    viewModel.updatePlayerState(
                                        listOfStateIndex = currentIndex, bool = bool
                                    )
                                }
                            }, state = tileState, currentIndex = currentIndex, viewModel = viewModel
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
        ) {
            Text(
                "${if (listOfTileStates?.first()?.isPlayer1Turn == true) "Player 1" else "Player 2"}",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = playerTextFont3,
                color = Color.Blue,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(start = 20.dp, 5.dp,)
                    .alpha(alpha = 0.40f)
                    .weight(1f)
            )
            CountdownTimer(modifier.weight(1f))
        }
    }
}




@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Tile(
    modifier: Modifier = Modifier,
    onChooseTile: (Boolean) -> Unit,
    state: TileState?,
    currentIndex: Int,
    viewModel: T3ViewModel,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(modifier = Modifier
            .border(4.dp, Color.Black, shape = RoundedCornerShape(8.dp))
            .size(80.dp)
            .clickable(onClick = {
                when (state?.isPlayer1Turn) {
                    true -> onChooseTile(true)
                    false -> onChooseTile(false)
                }
            })
            .drawBehind {
                drawRoundRect(
                    //if selectedState draw pink else black
                    color = Color.Black,
                    size = Size(width = 84.dp.toPx(), height = 84.dp.toPx()),
                    cornerRadius = CornerRadius(x = 30f, y = 30f)
                )
            },
            elevation = 5.dp,
            shape = RoundedCornerShape(8.dp),
            backgroundColor = retroNearWhite
        ) {
            AnimatedVisibility(
                visible = viewModel.tileState.value?.get(currentIndex)?.currentTileSymbolState != TileValue.NONE,
                enter = scaleIn(tween(150))
            ) {
                when (state?.currentTileSymbolState?.ordinal) {
                    TileValue.NONE.ordinal -> {}
                    TileValue.CROSS.ordinal -> DrawCross()
                    TileValue.CIRCLE.ordinal -> CircleOfSquares()
                }
            }
        }
    }
}




