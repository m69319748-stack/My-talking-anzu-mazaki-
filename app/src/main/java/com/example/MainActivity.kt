package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GameTab
import com.example.ui.components.BottomNavigationBar
import com.example.ui.components.TopBarStats
import com.example.ui.screens.ArcadeScreen
import com.example.ui.screens.KitchenScreen
import com.example.ui.screens.LivingRoomScreen
import com.example.ui.screens.WardrobeScreen
import com.example.ui.theme.AnimeBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MyTalkingAnzuApp()
            }
        }
    }
}

@Composable
fun MyTalkingAnzuApp(
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleVoiceListening()
        }
    }

    Scaffold(
        containerColor = AnimeBackground,
        topBar = {
            TopBarStats(
                uiState = uiState,
                onToggleMic = {
                    viewModel.toggleVoiceListening()
                }
            )
        },
        bottomBar = {
            // Hide bottom bar during active full-screen mini-games for maximum gameplay focus
            if (!uiState.isRhythmGameActive && !uiState.isCardGameActive) {
                BottomNavigationBar(
                    currentTab = uiState.currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = uiState.currentTab,
                animationSpec = tween(280),
                label = "screen_transition"
            ) { tab ->
                when (tab) {
                    GameTab.LIVING_DANCE -> LivingRoomScreen(viewModel = viewModel, uiState = uiState)
                    GameTab.WARDROBE -> WardrobeScreen(viewModel = viewModel, uiState = uiState)
                    GameTab.KITCHEN -> KitchenScreen(viewModel = viewModel, uiState = uiState)
                    GameTab.ARCADE -> ArcadeScreen(viewModel = viewModel, uiState = uiState)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    MyTalkingAnzuApp()
}
