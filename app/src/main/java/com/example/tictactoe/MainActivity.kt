package com.example.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.example.tictactoe.Data.ControllerState
import com.example.tictactoe.Data.TileAndGameState
import com.example.tictactoe.Data.T3ViewModel
import com.example.tictactoe.Data.listOfState
import com.example.tictactoe.ui.*
import com.example.tictactoe.ui.theme.*
import kotlinx.coroutines.launch

const val TAG = "main"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm = ViewModelProvider(this)[T3ViewModel::class.java]
            val scaffoldState = rememberScaffoldState()
            val scope = rememberCoroutineScope()
            Scaffold(scaffoldState = scaffoldState,
                topBar = { AppBar { scope.launch { scaffoldState.drawerState.apply { if (isClosed) open() else close() } } } },
                drawerContent = { DrawerContent() }) {
                MainScreen(
                    viewModel = vm,
                    liveDataListOfTileAndGameStates = vm.tileAndGameState,
                    controllerState = vm.controllerState,
                )
            }
        }
    }

    @Composable
    fun DrawerContent() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            retroGrey, Color(139, 137, 137, 255)
                        )
                    )
                ),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("RULES:",
                fontSize = 26.sp,
                fontFamily = playerTextFont4,
                textAlign = TextAlign.Start,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 40.dp, top = 20.dp,)
            )
            Text(
                text = "\u2022 Complete a row diagonal or column.\n\n\u2022 You can transpose to win.\n\n\u2022 Lock cannot be destroyed/transposed & lasts 1 enemy turn.",
                fontSize = 21.sp,
                fontFamily = playerTextFont4,
                textAlign = TextAlign.Start,
                color = Color.White,
                modifier = Modifier.padding(start = 40.dp, end = 30.dp)
            )
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
               Text("\u2022 Actions have cooldowns like ",
                   fontSize = 21.sp,
                   fontFamily = playerTextFont4,
                   textAlign = TextAlign.Start,
                   color = Color.White,
                   modifier = Modifier.padding(start = 40.dp).weight(1.8f)
                   )
                Box(modifier = Modifier.padding(start = 40.dp).weight(1f)) {
                    DeadButton(onCooldown = 2)
                }
            }
            Text("ACTIONS:",
                fontSize = 26.sp,
                fontFamily = playerTextFont4,
                textAlign = TextAlign.Start,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 40.dp)
                )
            Column(modifier = Modifier.padding(end = 30.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.padding(start = 40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.AdsClick,
                        contentDescription = "",
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                    Text(
                        "Place an X or O",
                        modifier = Modifier.padding(start = 20.dp),
                        fontSize = 21.sp,
                        color = Color.White,
                        fontFamily = playerTextFont4
                    )
                }

                Row(
                    modifier = Modifier.padding(start = 40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = "",
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                    Text(
                        "Destroy tiles\n(random pattern)",
                        modifier = Modifier.padding(start = 20.dp),
                        fontSize = 21.sp,
                        color = Color.White,
                        fontFamily = playerTextFont4
                    )
                }

                Row(
                    modifier = Modifier.padding(start = 40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "",
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                    Text(
                        "Lock a tile",
                        modifier = Modifier.padding(start = 20.dp),
                        fontSize = 21.sp,
                        color = Color.White,
                        fontFamily = playerTextFont4
                    )
                }

                Row(
                    modifier = Modifier.padding(start = 40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shuffle,
                        contentDescription = "",
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                    Text(
                        "Transpose cross, diagonal or mid\n(mid is random)",
                        modifier = Modifier.padding(start = 20.dp),
                        fontSize = 21.sp,
                        color = Color.White,
                        fontFamily = playerTextFont4
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: T3ViewModel,
    liveDataListOfTileAndGameStates: LiveData<List<TileAndGameState>?>,
    controllerState: LiveData<ControllerState>,

    ) {

    val liveBoardState = liveDataListOfTileAndGameStates.observeAsState()
    val controllerState = controllerState.observeAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(196, 196, 196))
            .drawBehind {
                drawCableUI()
            }, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(0.55f)
        ) {
            TicTacToeBoard(
                listOfTileAndGameStates = liveBoardState.value ?: listOfState,
                viewModel = viewModel,
                arrowState = controllerState,
            turnOver = {viewModel.outOfTime()})
        }
        Column(
            modifier = Modifier
                .weight(0.45f)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (liveBoardState.value?.get(0)?.isPlayer1Turn == true) {

                FullController(
                    arrowOnClick = { viewModel.updateArrowButtonState(direction = it) },
                    actionOnClick = { viewModel.updateActionButtonState(action = it) },
                    destroyButtonOnCooldown = { controllerState.value?.destroyButtonIsOnCooldownP1!! },
                    destroyCooldownLeft = { controllerState.value?.destroyCooldownLeftP1 ?: 0 },
                    lockButtonOnCooldown = { controllerState.value?.lockButtonIsOnCooldownP1!! },
                    lockCooldownLeft = { controllerState.value?.lockButtonCooldownLeftP1 ?: 0 },
                    transposeButtonOnCooldown = { controllerState.value?.transposeButtonIsOnCooldownP1!! },
                    transposeCooldownLeft = { controllerState.value?.transposeCooldownLeftP1 ?: 0 },
                    buttonBorderColor = retroPurple,
                    modifier = Modifier.padding(bottom = 40.dp)
                )
            } else {

                FullController(
                    arrowOnClick = { viewModel.updateArrowButtonState(direction = it) },
                    actionOnClick = { viewModel.updateActionButtonState(action = it) },
                    destroyButtonOnCooldown = { controllerState.value?.destroyButtonIsOnCooldownP2!! },
                    destroyCooldownLeft = { controllerState.value?.destroyCooldownLeftP2 ?: 0 },
                    lockButtonOnCooldown = { controllerState.value?.lockButtonIsOnCooldownP2!! },
                    lockCooldownLeft = { controllerState.value?.lockButtonCooldownLeftP2 ?: 0 },
                    transposeButtonOnCooldown = { controllerState.value?.transposeButtonIsOnCooldownP2!! },
                    transposeCooldownLeft = { controllerState.value?.transposeCooldownLeftP2 ?: 0 },
                    buttonBorderColor = retroGreen,
                    modifier = Modifier.padding(bottom = 40.dp)
                )
            }
            OutlinedButton(
                onClick = { viewModel.resetBoard() },
                shape = CutCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    retroPurple
                ),
                elevation = ButtonDefaults.elevation(defaultElevation = 5.dp),
                border = BorderStroke(5.dp, color = Color.Black),
                modifier = Modifier.size(140.dp, 60.dp)
            ) {
                Text(
                    "Reset",
                    modifier = Modifier.padding(top = 8.dp, start = 5.dp),
                    fontSize = 16.sp,
                    color = Color.White,
                    fontFamily = playerTextFont5
                )
            }
        }
    }
}


@Composable
fun AppBar(scaffoldState: () -> Unit) {

    var expandedMenu by remember { mutableStateOf(false) }
    var switchState by remember { mutableStateOf(false) }

    TopAppBar(title = {
        Text("Tic Tac No", color = Color.White)
    }, actions = {
        IconButton(onClick = scaffoldState) {
            Icon(
                imageVector = Icons.Filled.Help,
                contentDescription = "game rules",
                tint = Color.White
            )
        }
        IconButton(
            onClick = { expandedMenu = !expandedMenu }, Modifier.padding(end = 10.dp, start = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "drop down menu",
                tint = Color.White
            )
        }
    }, backgroundColor = retroAppBarColor, elevation = 2.dp)

    MaterialTheme(colors = MaterialTheme.colors.copy(surface = retroNearWhite)) {

        DropdownMenu(
            expanded = expandedMenu,
            onDismissRequest = { expandedMenu = false },
            offset = DpOffset(x = (-50).dp, y = (-118).dp),
        ) {
            DropdownMenuItem(onClick = { }) {
                Text("Board Touch", color = Color.Black)
                Switch(
                    checked = switchState,
                    onCheckedChange = { switchState = it },
                    modifier = Modifier.padding(start = 20.dp),
                    colors = SwitchDefaults.colors(uncheckedThumbColor = retroGrey)
                )
            }
        }
    }
}











