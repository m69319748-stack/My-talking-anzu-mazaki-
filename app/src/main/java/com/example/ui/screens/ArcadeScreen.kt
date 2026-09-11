package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CharacterEmotion
import com.example.model.DuelCard
import com.example.ui.character.AnzuCharacterView
import com.example.viewmodel.GameUiState
import com.example.viewmodel.GameViewModel

@Composable
fun ArcadeScreen(
    viewModel: GameViewModel,
    uiState: GameUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("arcade_screen")
    ) {
        // Dance stage arcade background
        Image(
            painter = painterResource(id = R.drawable.img_anzu_dance_stage),
            contentDescription = "Arcade Stage",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x8812002B), Color(0xDD0A0017))
                    )
                )
        )

        when {
            uiState.isRhythmGameActive -> {
                RhythmGamePlayView(viewModel = viewModel, uiState = uiState)
            }
            uiState.isCardGameActive -> {
                CardGamePlayView(viewModel = viewModel, uiState = uiState)
            }
            else -> {
                ArcadeHubView(viewModel = viewModel, uiState = uiState)
            }
        }
    }
}

@Composable
private fun ArcadeHubView(
    viewModel: GameViewModel,
    uiState: GameUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Header Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x992B0854))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(Icons.Default.Gamepad, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "🎮 صالة الألعاب المصغرة وجمع الذهب",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Mini Game 1: Rhythm Beats Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xEE2A084E)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFFFF4081), RoundedCornerShape(22.dp))
                .testTag("card_game_rhythm_start")
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFFFF4081), Color(0xFF9C27B0)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "لعبة الإيقاع (Rhythm Beats) 🎵",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "انقر على النوتات المتساقطة مع رقص أنزو",
                                color = Color(0xFFCE93D8),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "أعلى نتيجة: ${uiState.rhythmHighScore}",
                            color = Color(0xFFFFE082),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = { viewModel.startRhythmGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_play_rhythm")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ابدأ الرقص والإيقاع 💃", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Mini Game 2: Card Match & Guessing Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xEE1E0940)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFF00E5FF), RoundedCornerShape(22.dp))
                .testTag("card_game_memory_start")
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFF00E5FF), Color(0xFF3F51B5)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Style, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "تخمين أوراق المبارزة والذاكرة 🃏",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "طابق بطاقات الأصدقاء والوحوش السحرية",
                                color = Color(0xFF80DEEA),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "أعلى نتيجة: ${uiState.cardHighScore}",
                            color = Color(0xFFFFE082),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = { viewModel.startCardGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_play_card_match")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ابدأ لغز البطاقات 🃏", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun RhythmGamePlayView(
    viewModel: GameViewModel,
    uiState: GameUiState
) {
    val laneColors = listOf(
        Color(0xFFFF4081),
        Color(0xFF00E5FF),
        Color(0xFFFFD700),
        Color(0xFF76FF03)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp, bottom = 10.dp, start = 12.dp, end = 12.dp)
    ) {
        // Game HUD Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close Button
            IconButton(
                onClick = { viewModel.closeRhythmGame() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0x66FFFFFF))
                    .size(36.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            // Score & Combo display
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "النتيجة: ${uiState.rhythmScore}",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
                if (uiState.rhythmCombo > 1) {
                    Text(
                        text = "COMBO x${uiState.rhythmCombo} (x${uiState.rhythmMultiplier}) 🔥",
                        color = Color(0xFFFF4081),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // High Score
            Text(
                text = "الهدف: 1000+",
                color = Color.White,
                fontSize = 12.sp
            )
        }

        // Live Hit Feedback text
        if (!uiState.rhythmFeedback.isNullOrBlank()) {
            Text(
                text = uiState.rhythmFeedback,
                color = Color(0xFF69F0AE),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }

        // Mini Anzu Dancer at Top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            contentAlignment = Alignment.Center
        ) {
            AnzuCharacterView(
                outfit = uiState.currentOutfit,
                accessory = uiState.currentAccessory,
                emotion = CharacterEmotion.DANCING,
                isListening = false,
                isSpeaking = false,
                isDancing = true,
                danceStep = uiState.rhythmScore / 10,
                onTapHead = {},
                onTapBelly = {},
                onTapHands = {},
                onSwipeSpin = {},
                modifier = Modifier.fillMaxSize()
            )
        }

        // 4 Rhythm Lanes Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xCC0E031E))
                .border(2.dp, Color(0x44FFFFFF), RoundedCornerShape(16.dp))
        ) {
            // 4 Lanes Column background separators
            Row(modifier = Modifier.fillMaxSize()) {
                for (lane in 0 until 4) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(0.5.dp, Color(0x22FFFFFF))
                    )
                }
            }

            // Target Strike Line (around 85% from top)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 70.dp)
                    .background(Color(0xFFFF4081))
            )

            // Falling Notes
            uiState.rhythmNotes.forEach { note ->
                if (!note.isHit && !note.isMissed) {
                    val laneWidthPercent = 0.25f
                    val noteXOffsetPercent = note.lane * laneWidthPercent
                    val noteYPercent = note.positionY.coerceIn(0f, 1f)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(laneWidthPercent)
                                .fillMaxHeight(0.08f)
                                .align(Alignment.TopStart)
                                .padding(
                                    start = (note.lane * 80).dp,
                                    top = (noteYPercent * 240).dp
                                )
                                .clip(CircleShape)
                                .background(laneColors[note.lane % laneColors.size]),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎵", fontSize = 16.sp)
                        }
                    }
                }
            }

            // Game Over Dialog
            if (uiState.rhythmGameOver) {
                Surface(
                    color = Color(0xF01A0636),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎉 أداء رقص رائع!", color = Color(0xFFFFD700), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("النتيجة النهائية: ${uiState.rhythmScore}", color = Color.White, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.startRhythmGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081))
                        ) {
                            Text("إعادة اللعب 🔁", color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4 Hit Buttons at Bottom
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (lane in 0 until 4) {
                Button(
                    onClick = { viewModel.onHitRhythmNote(lane) },
                    colors = ButtonDefaults.buttonColors(containerColor = laneColors[lane]),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp)
                        .testTag("btn_rhythm_lane_$lane")
                ) {
                    Text(
                        text = "HIT!",
                        color = Color(0xFF1E0A3C),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CardGamePlayView(
    viewModel: GameViewModel,
    uiState: GameUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp, bottom = 10.dp, start = 12.dp, end = 12.dp)
    ) {
        // Game HUD Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.closeCardGame() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0x66FFFFFF))
                    .size(36.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${uiState.cardTimeRemainingSec} ثانية",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Text(
                text = "المطابقات: ${uiState.cardMatches}/8 🌟",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4x4 Card Grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(uiState.cardGrid) { index, card ->
                    DuelCardItem(
                        card = card,
                        onClick = { viewModel.onFlipCard(index) },
                        modifier = Modifier.testTag("duel_card_$index")
                    )
                }
            }

            // Victory / Game Over Overlay
            if (uiState.cardGameOver) {
                Surface(
                    color = Color(0xF0150329),
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 16.dp,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (uiState.cardMatches == 8) "🏆 فوز ساحق بالمبارزة!" else "⏳ انتهى الوقت!",
                            color = Color(0xFFFFD700),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "المطابقات: ${uiState.cardMatches}/8 | الحركات: ${uiState.cardMoves}",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.startCardGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF))
                        ) {
                            Text("جولة مبارزة جديدة 🃏", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DuelCardItem(
    card: DuelCard,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                card.isMatched -> Color(0x5500E676)
                card.isFaceUp -> Color(0xFF2C1052)
                else -> Color(0xFF3F1973) // Card Back (Yu-Gi-Oh swirl style)
            }
        ),
        modifier = modifier
            .height(82.dp)
            .border(
                width = if (card.isFaceUp || card.isMatched) 2.dp else 1.dp,
                color = if (card.isMatched) Color(0xFF69F0AE) else if (card.isFaceUp) Color(0xFFFF4081) else Color(0xFFFFD700),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = !card.isFaceUp && !card.isMatched) { onClick() }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (card.isFaceUp || card.isMatched) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(text = card.iconEmoji, fontSize = 22.sp)
                    Text(
                        text = card.nameAr,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            } else {
                // Card Back Pattern (Golden Millennium Eye style)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🎴", fontSize = 22.sp)
                    Text(
                        text = "DUEL",
                        color = Color(0xFFFFD700),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
