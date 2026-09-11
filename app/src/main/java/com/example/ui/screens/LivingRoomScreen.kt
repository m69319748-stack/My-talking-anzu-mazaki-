package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.DanceRoutine
import com.example.ui.character.AnzuCharacterView
import com.example.viewmodel.GameUiState
import com.example.viewmodel.GameViewModel

@Composable
fun LivingRoomScreen(
    viewModel: GameViewModel,
    uiState: GameUiState,
    modifier: Modifier = Modifier
) {
    var showChatDialog by remember { mutableStateOf(false) }
    var customInputText by remember { mutableStateOf("") }

    val quickPhrases = listOf(
        "أهلاً أنزو! ما رأيك في يومنا اليوم؟ ✨",
        "الصداقة هي سر قوتنا الأبدية! 💖",
        "يلا نستعد لتدريب الرقص الحماسي! 💃",
        "أنتِ أفضل راقصة في المدرسة! ⭐",
        "هل أنتِ مستعدة لمبارزة قادمة؟ 🃏"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("living_room_screen")
    ) {
        // Room Background Image (Living / Dance Stage)
        val bgDrawable = if (uiState.isDancing) R.drawable.img_anzu_dance_stage else R.drawable.img_anzu_room
        Image(
            painter = painterResource(id = bgDrawable),
            contentDescription = "Room Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle gradient overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x660D041A),
                            Color.Transparent,
                            Color(0x990D041A)
                        )
                    )
                )
        )

        // Character in Center
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Interactive Anime Character
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(0.92f),
                contentAlignment = Alignment.Center
            ) {
                AnzuCharacterView(
                    outfit = uiState.currentOutfit,
                    accessory = uiState.currentAccessory,
                    emotion = uiState.emotion,
                    isListening = uiState.isListening,
                    isSpeaking = uiState.isSpeaking,
                    isDancing = uiState.isDancing,
                    danceStep = uiState.danceStep,
                    speechBubbleText = uiState.speechBubbleText,
                    onTapHead = { viewModel.onPatHead() },
                    onTapBelly = { viewModel.onTickleBelly() },
                    onTapHands = { viewModel.onHighFiveHands() },
                    onSwipeSpin = { viewModel.onAcrobaticSpin() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Controls & Dance Studio Section at bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Music Player Bar
                Surface(
                    color = Color(0xDD24113D),
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "Music",
                                tint = Color(0xFFFF4081),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "مشغل موسيقى الرقص",
                                    color = Color(0xFFFF80AB),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = uiState.currentMusicTrackName,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.toggleMusic() },
                                modifier = Modifier.testTag("btn_toggle_music")
                            ) {
                                Icon(
                                    imageVector = if (uiState.isMusicPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause Music",
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.nextMusicTrack() },
                                modifier = Modifier.testTag("btn_next_music")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "Next Track",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                // Dance Choreography Buttons Row
                Surface(
                    color = Color(0xDD1B0933),
                    shape = RoundedCornerShape(18.dp),
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💃 حركات ورقصات أنزو الاستعراضية",
                                color = Color(0xFFFFD54F),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (uiState.isDancing) {
                                Button(
                                    onClick = { viewModel.stopDancing() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("إيقاف", color = Color.White, fontSize = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(DanceRoutine.values()) { routine ->
                                val isCurrent = uiState.isDancing && uiState.currentDanceRoutine == routine
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isCurrent) Color(0xFFFF4081) else Color(0x553D1C68)
                                    ),
                                    modifier = Modifier
                                        .testTag("dance_btn_${routine.id}")
                                        .clickable { viewModel.startDanceRoutine(routine) }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = routine.emoji, fontSize = 18.sp)
                                        Text(
                                            text = routine.nameAr,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = "-${routine.energyCost}⚡",
                                            color = Color(0xFFFFD54F),
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Talk / Chat Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { showChatDialog = !showChatDialog },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E24AA)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_open_quick_chat")
                    ) {
                        Icon(Icons.Default.ChatBubble, contentDescription = "Chat", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("محادثة وتكرار الصوت 💬", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Quick Chat / Voice Input Modal Card
        AnimatedVisibility(
            visible = showChatDialog,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFA1E0B38),
                shadowElevation = 18.dp,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💬 تحدث مع أنزو مازاكي لتكرار صوتك",
                        color = Color(0xFFFF80AB),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick anime phrases list
                    Text(
                        text = "اختر عبارة جاهزة لتتحدث بها أنزو:",
                        color = Color(0xFFE1BEE7),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        quickPhrases.forEach { phrase ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0x44FFFFFF),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.speakQuickPhrase(phrase)
                                        showChatDialog = false
                                    }
                            ) {
                                Text(
                                    text = phrase,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Custom text input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customInputText,
                            onValueChange = { customInputText = it },
                            placeholder = { Text("اكتب ما تريده لتكرره أنزو...", color = Color.Gray, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFF4081),
                                unfocusedBorderColor = Color(0x66FF4081)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_custom_speech")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (customInputText.isNotBlank()) {
                                    viewModel.repeatVoiceText(customInputText)
                                    customInputText = ""
                                    showChatDialog = false
                                }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFFF4081))
                                .testTag("btn_send_custom_speech")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showChatDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إغلاق النافذة", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
