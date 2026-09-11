package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.model.AccessoryType
import com.example.model.OutfitType
import com.example.ui.character.AnzuCharacterView
import com.example.viewmodel.GameUiState
import com.example.viewmodel.GameViewModel

@Composable
fun WardrobeScreen(
    viewModel: GameViewModel,
    uiState: GameUiState,
    modifier: Modifier = Modifier
) {
    var selectedCategoryTab by remember { mutableStateOf(0) } // 0: Outfits, 1: Accessories

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("wardrobe_screen")
    ) {
        // Dressing Room Background
        Image(
            painter = painterResource(id = R.drawable.img_anzu_wardrobe),
            contentDescription = "Wardrobe Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark gradient tint for clarity
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x77120324),
                            Color(0x33120324),
                            Color(0xEE120324)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(55.dp))

            // Upper 45%: Live Character Fitting Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.48f),
                contentAlignment = Alignment.Center
            ) {
                AnzuCharacterView(
                    outfit = uiState.currentOutfit,
                    accessory = uiState.currentAccessory,
                    emotion = uiState.emotion,
                    isListening = false,
                    isSpeaking = uiState.isSpeaking,
                    isDancing = false,
                    speechBubbleText = uiState.speechBubbleText,
                    onTapHead = { viewModel.onPatHead() },
                    onTapBelly = { viewModel.onTickleBelly() },
                    onTapHands = { viewModel.onHighFiveHands() },
                    onSwipeSpin = { viewModel.onAcrobaticSpin() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Lower 52%: Wardrobe Selector Panel
            Surface(
                color = Color(0xFA1E0E38),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                shadowElevation = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.52f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    // Category Selection Tabs (الأزياء vs الإكسسوارات)
                    TabRow(
                        selectedTabIndex = selectedCategoryTab,
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFFFF4081),
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedCategoryTab]),
                                color = Color(0xFFFF4081),
                                height = 3.dp
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedCategoryTab == 0,
                            onClick = { selectedCategoryTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Checkroom, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("الأزياء والملابس 👗", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        )
                        Tab(
                            selected = selectedCategoryTab == 1,
                            onClick = { selectedCategoryTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Style, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("الإكسسوارات 🎀", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (selectedCategoryTab == 0) {
                        // Outfits List
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(OutfitType.values()) { outfit ->
                                val isUnlocked = uiState.unlockedOutfits.contains(outfit.id)
                                val isEquipped = uiState.currentOutfit == outfit

                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isEquipped) Color(0xFF381559) else Color(0x66291244)
                                    ),
                                    modifier = Modifier
                                        .testTag("outfit_card_${outfit.id}")
                                        .border(
                                            width = if (isEquipped) 2.dp else 1.dp,
                                            color = if (isEquipped) Color(0xFFFF4081) else Color(0x33FFFFFF),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable { viewModel.equipOutfit(outfit) }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // Outfit Color Palette Dot
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .clip(CircleShape)
                                                    .background(outfit.primaryColor)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .clip(CircleShape)
                                                    .background(outfit.secondaryColor)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = outfit.nameAr,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = outfit.descriptionAr,
                                            color = Color(0xFFB39DDB),
                                            fontSize = 9.sp,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2,
                                            lineHeight = 12.sp
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        when {
                                            isEquipped -> {
                                                Surface(
                                                    color = Color(0xFFFF4081),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                                        Spacer(modifier = Modifier.width(2.dp))
                                                        Text("مرتدى حالياً", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                            isUnlocked -> {
                                                Surface(
                                                    color = Color(0x33FFFFFF),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        text = "ارتداء",
                                                        color = Color(0xFFFFD54F),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }
                                            else -> {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color(0xFFFF8F00))
                                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                                ) {
                                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFF3E2723), modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text("${outfit.price}", color = Color(0xFF3E2723), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Accessories List
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(AccessoryType.values()) { acc ->
                                val isUnlocked = acc == AccessoryType.NONE || uiState.unlockedAccessories.contains(acc.id)
                                val isEquipped = uiState.currentAccessory == acc

                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isEquipped) Color(0xFF381559) else Color(0x66291244)
                                    ),
                                    modifier = Modifier
                                        .testTag("acc_card_${acc.id}")
                                        .border(
                                            width = if (isEquipped) 2.dp else 1.dp,
                                            color = if (isEquipped) Color(0xFFFF4081) else Color(0x33FFFFFF),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable { viewModel.equipAccessory(acc) }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        val iconEmoji = when (acc) {
                                            AccessoryType.NONE -> "🚫"
                                            AccessoryType.CUTE_CAT_EARS -> "🐱"
                                            AccessoryType.STAR_SUNGLASSES -> "⭐"
                                            AccessoryType.DUEL_GLOVE -> "🥊"
                                            AccessoryType.MAGIC_HEADBAND -> "🎀"
                                            AccessoryType.SMART_GLASSES -> "👓"
                                        }
                                        Text(text = iconEmoji, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = acc.nameAr,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        when {
                                            isEquipped -> {
                                                Surface(
                                                    color = Color(0xFFFF4081),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("مفعل", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                                }
                                            }
                                            isUnlocked -> {
                                                Surface(
                                                    color = Color(0x33FFFFFF),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("تفعيل", color = Color(0xFFFFD54F), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                                }
                                            }
                                            else -> {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color(0xFFFF8F00))
                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFF3E2723), modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text("${acc.price}", color = Color(0xFF3E2723), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
