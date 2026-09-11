package com.example.ui.character

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccessoryType
import com.example.model.CharacterEmotion
import com.example.model.OutfitType
import com.example.model.Particle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun AnzuCharacterView(
    outfit: OutfitType,
    accessory: AccessoryType,
    emotion: CharacterEmotion,
    isListening: Boolean,
    isSpeaking: Boolean,
    isDancing: Boolean,
    danceStep: Int = 0,
    speechBubbleText: String? = null,
    onTapHead: () -> Unit,
    onTapBelly: () -> Unit,
    onTapHands: () -> Unit,
    onSwipeSpin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val particles = remember { mutableStateListOf<Particle>() }

    // Breathing & Idle Physics
    val infiniteTransition = rememberInfiniteTransition(label = "idle_anim")
    val breathOffsetY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath_y"
    )
    val hairSwayAngle by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hair_sway"
    )

    // Eye blinking timer
    var isBlinking by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(2500, 4500))
            isBlinking = true
            delay(160)
            isBlinking = false
        }
    }

    // Mouth Talking lip-sync animation
    val mouthOpenRatio = remember { Animatable(0f) }
    LaunchedEffect(isSpeaking) {
        if (isSpeaking) {
            while (isSpeaking) {
                mouthOpenRatio.animateTo(1f, tween(120, easing = LinearEasing))
                mouthOpenRatio.animateTo(0.2f, tween(120, easing = LinearEasing))
                mouthOpenRatio.animateTo(0.7f, tween(100, easing = LinearEasing))
                mouthOpenRatio.animateTo(0f, tween(110, easing = LinearEasing))
            }
        } else {
            mouthOpenRatio.snapTo(0f)
        }
    }

    // Interactive Touch Spring Animations
    val rotationAngle = remember { Animatable(0f) }
    val scaleFactor = remember { Animatable(1f) }
    val jumpOffsetY = remember { Animatable(0f) }
    val wiggleAngle = remember { Animatable(0f) }

    fun emitParticles(centerX: Float, centerY: Float, count: Int, symbol: String, color: Color) {
        for (i in 0 until count) {
            val angle = Random.nextFloat() * 2f * PI.toFloat()
            val speed = Random.nextFloat() * 7f + 3f
            particles.add(
                Particle(
                    id = System.nanoTime() + i,
                    x = centerX,
                    y = centerY,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed - 4f,
                    size = Random.nextFloat() * 12f + 14f,
                    color = color,
                    symbol = symbol
                )
            )
        }
    }

    // Particle updater loop
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            if (particles.isNotEmpty()) {
                val iterator = particles.iterator()
                while (iterator.hasNext()) {
                    val p = iterator.next()
                    p.x += p.vx
                    p.y += p.vy
                    p.alpha -= 0.025f
                    if (p.alpha <= 0f) {
                        iterator.remove()
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .testTag("anzu_character_canvas_box")
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        val relativeY = offset.y / size.height
                        val relativeX = offset.x / size.width

                        when {
                            relativeY < 0.38f -> {
                                // Head pat
                                coroutineScope.launch {
                                    scaleFactor.animateTo(1.08f, tween(150))
                                    scaleFactor.animateTo(1f, tween(200))
                                }
                                emitParticles(offset.x, offset.y, 8, "💖", Color(0xFFFF4081))
                                onTapHead()
                            }
                            relativeY in 0.38f..0.72f -> {
                                // Belly tickle / Body tap
                                coroutineScope.launch {
                                    wiggleAngle.animateTo(8f, tween(80))
                                    wiggleAngle.animateTo(-8f, tween(80))
                                    wiggleAngle.animateTo(4f, tween(70))
                                    wiggleAngle.animateTo(0f, tween(70))
                                }
                                emitParticles(offset.x, offset.y, 7, "⭐", Color(0xFFFFD700))
                                onTapBelly()
                            }
                            else -> {
                                // Hands / Feet / Tap
                                coroutineScope.launch {
                                    jumpOffsetY.animateTo(-35f, tween(durationMillis = 160, easing = FastOutSlowInEasing))
                                    jumpOffsetY.animateTo(0f, tween(durationMillis = 200, easing = FastOutSlowInEasing))
                                }
                                emitParticles(offset.x, offset.y, 6, "✨", Color(0xFF00E5FF))
                                onTapHands()
                            }
                        }
                    },
                    onDoubleTap = { offset ->
                        // Double tap acrobatics spin!
                        coroutineScope.launch {
                            rotationAngle.animateTo(360f, tween(durationMillis = 550, easing = FastOutSlowInEasing))
                            rotationAngle.snapTo(0f)
                        }
                        emitParticles(offset.x, offset.y, 14, "🌟", Color(0xFFFFEB3B))
                        onSwipeSpin()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Speech Bubble
        if (!speechBubbleText.isNullOrBlank()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.96f),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-10).dp)
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = speechBubbleText,
                    color = Color(0xFF2E1A47),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }

        // Main Character Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = breathOffsetY + jumpOffsetY.value
                    rotationZ = wiggleAngle.value
                    rotationY = if (rotationAngle.value > 0f) rotationAngle.value else 0f
                    scaleX = scaleFactor.value
                    scaleY = scaleFactor.value
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height
            val centerX = canvasW / 2f
            val centerY = canvasH * 0.48f

            // Shadow on floor
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x55000000), Color.Transparent),
                    center = Offset(centerX, canvasH * 0.88f),
                    radius = canvasW * 0.32f
                ),
                topLeft = Offset(centerX - canvasW * 0.3f, canvasH * 0.85f),
                size = Size(canvasW * 0.6f, canvasH * 0.06f)
            )

            // Dance pose shifts
            val danceLegOffset = if (isDancing) sin(danceStep * 1.5).toFloat() * 18f else 0f
            val danceArmOffset = if (isDancing) cos(danceStep * 1.5).toFloat() * 25f else 0f

            // 1. Draw Legs & Shoes
            drawLegsAndShoes(
                centerX = centerX,
                bottomY = canvasH * 0.86f,
                outfit = outfit,
                legOffset = danceLegOffset
            )

            // 2. Draw Torso & Outfit
            drawTorsoAndOutfit(
                centerX = centerX,
                centerY = centerY,
                outfit = outfit,
                emotion = emotion
            )

            // 3. Draw Arms & Hands
            drawArmsAndHands(
                centerX = centerX,
                centerY = centerY,
                outfit = outfit,
                accessory = accessory,
                emotion = emotion,
                isListening = isListening,
                isDancing = isDancing,
                armOffset = danceArmOffset
            )

            // 4. Draw Neck & Head
            val headCenterY = centerY - canvasH * 0.16f
            val headRadius = canvasW * 0.22f

            drawHeadAndFace(
                centerX = centerX,
                headCenterY = headCenterY,
                headRadius = headRadius,
                emotion = emotion,
                isBlinking = isBlinking,
                mouthRatio = mouthOpenRatio.value,
                hairSway = hairSwayAngle
            )

            // 5. Draw Hair (Auburn brown anime bob with bangs)
            drawAnzuHair(
                centerX = centerX,
                headCenterY = headCenterY,
                headRadius = headRadius,
                hairSway = hairSwayAngle
            )

            // 6. Draw Accessories (Glasses, Cat ears, Headbands)
            drawAccessoryItem(
                accessory = accessory,
                centerX = centerX,
                headCenterY = headCenterY,
                headRadius = headRadius
            )

            // 7. Draw Listening Soundwave Indicator if listening
            if (isListening) {
                drawListeningAura(centerX, headCenterY, headRadius)
            }

            // 8. Draw Active Touch Particles
            for (p in particles) {
                drawCircle(
                    color = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                    radius = p.size / 2f,
                    center = Offset(p.x, p.y)
                )
            }
        }
    }
}

private fun DrawScope.drawLegsAndShoes(
    centerX: Float,
    bottomY: Float,
    outfit: OutfitType,
    legOffset: Float
) {
    val skinColor = Color(0xFFFFDFC4)
    val legWidth = 24f
    val legHeight = 120f
    val legSpacing = 28f

    // Left Leg
    val leftLegX = centerX - legSpacing - 12f + legOffset
    drawRoundRect(
        color = skinColor,
        topLeft = Offset(leftLegX, bottomY - legHeight),
        size = Size(legWidth, legHeight),
        cornerRadius = CornerRadius(12f, 12f)
    )

    // Right Leg
    val rightLegX = centerX + legSpacing - 12f - legOffset
    drawRoundRect(
        color = skinColor,
        topLeft = Offset(rightLegX, bottomY - legHeight),
        size = Size(legWidth, legHeight),
        cornerRadius = CornerRadius(12f, 12f)
    )

    // Socks & Shoes based on outfit
    val shoeColor = when (outfit) {
        OutfitType.SCHOOL_UNIFORM -> Color(0xFF5D4037) // Brown school loafers
        OutfitType.DANCE_TRAINING -> Color(0xFFFF3D00) // Orange sneakers
        OutfitType.DARK_MAGICIAN_GIRL -> Color(0xFFFF4081) // Magical boots
        OutfitType.IDOL_SPARKLE -> Color(0xFFFFD700) // Shiny gold idol boots
        OutfitType.SUMMER_CASUAL -> Color(0xFF29B6F6) // Teal sandals
    }

    // Left Shoe
    drawRoundRect(
        color = shoeColor,
        topLeft = Offset(leftLegX - 4f, bottomY - 22f),
        size = Size(legWidth + 8f, 26f),
        cornerRadius = CornerRadius(10f, 10f)
    )

    // Right Shoe
    drawRoundRect(
        color = shoeColor,
        topLeft = Offset(rightLegX - 4f, bottomY - 22f),
        size = Size(legWidth + 8f, 26f),
        cornerRadius = CornerRadius(10f, 10f)
    )
}

private fun DrawScope.drawTorsoAndOutfit(
    centerX: Float,
    centerY: Float,
    outfit: OutfitType,
    emotion: CharacterEmotion
) {
    val skinColor = Color(0xFFFFDFC4)
    val torsoW = 86f
    val torsoH = 95f

    // Neck
    drawRect(
        color = skinColor,
        topLeft = Offset(centerX - 14f, centerY - torsoH / 2f - 24f),
        size = Size(28f, 32f)
    )

    // Main Torso base / top
    val topColor = outfit.primaryColor
    drawRoundRect(
        color = topColor,
        topLeft = Offset(centerX - torsoW / 2f, centerY - torsoH / 2f),
        size = Size(torsoW, torsoH * 0.65f),
        cornerRadius = CornerRadius(16f, 16f)
    )

    // Skirt or Shorts (Lower outfit)
    val skirtColor = outfit.secondaryColor
    val skirtPath = Path().apply {
        moveTo(centerX - torsoW * 0.45f, centerY + torsoH * 0.12f)
        lineTo(centerX + torsoW * 0.45f, centerY + torsoH * 0.12f)
        lineTo(centerX + torsoW * 0.65f, centerY + torsoH * 0.68f)
        lineTo(centerX - torsoW * 0.65f, centerY + torsoH * 0.68f)
        close()
    }
    drawPath(skirtPath, color = skirtColor)

    // Skirt pleats lines
    for (i in -2..2) {
        val px = centerX + i * 16f
        drawLine(
            color = Color.Black.copy(alpha = 0.15f),
            start = Offset(px * 0.95f, centerY + torsoH * 0.14f),
            end = Offset(px * 1.12f, centerY + torsoH * 0.66f),
            strokeWidth = 2.5f
        )
    }

    // Special outfit embellishments
    when (outfit) {
        OutfitType.SCHOOL_UNIFORM -> {
            // Domino High sailor collar and pink ribbon
            val ribbonColor = Color(0xFFFF4081)
            // Sailor collar
            val collarPath = Path().apply {
                moveTo(centerX - 32f, centerY - torsoH / 2f)
                lineTo(centerX, centerY - 6f)
                lineTo(centerX + 32f, centerY - torsoH / 2f)
                lineTo(centerX + 24f, centerY - torsoH / 2f - 12f)
                lineTo(centerX - 24f, centerY - torsoH / 2f - 12f)
                close()
            }
            drawPath(collarPath, color = Color.White)
            // Ribbon knot & tails
            drawCircle(color = ribbonColor, radius = 7f, center = Offset(centerX, centerY - 4f))
            drawPath(
                Path().apply {
                    moveTo(centerX - 4f, centerY - 4f)
                    lineTo(centerX - 16f, centerY + 18f)
                    lineTo(centerX - 6f, centerY + 18f)
                    close()
                },
                color = ribbonColor
            )
            drawPath(
                Path().apply {
                    moveTo(centerX + 4f, centerY - 4f)
                    lineTo(centerX + 16f, centerY + 18f)
                    lineTo(centerX + 6f, centerY + 18f)
                    close()
                },
                color = ribbonColor
            )
        }
        OutfitType.DARK_MAGICIAN_GIRL -> {
            // Gold star jewel & cyan trim
            drawCircle(color = Color(0xFFFFD700), radius = 9f, center = Offset(centerX, centerY - 10f))
            drawCircle(color = Color(0xFFFF4081), radius = 5f, center = Offset(centerX, centerY - 10f))
            // Magician capelet
            drawLine(
                color = Color(0xFF00E5FF),
                start = Offset(centerX - 36f, centerY - 16f),
                end = Offset(centerX + 36f, centerY - 16f),
                strokeWidth = 4f
            )
        }
        OutfitType.IDOL_SPARKLE -> {
            // Glittering star belt
            drawRect(
                color = Color(0xFFFFD700),
                topLeft = Offset(centerX - torsoW * 0.45f, centerY + torsoH * 0.08f),
                size = Size(torsoW * 0.9f, 8f)
            )
            drawCircle(color = Color.White, radius = 6f, center = Offset(centerX, centerY + torsoH * 0.12f))
        }
        OutfitType.DANCE_TRAINING -> {
            // Sporty chest emblem
            drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 8f, center = Offset(centerX, centerY - 12f))
        }
        OutfitType.SUMMER_CASUAL -> {
            // Cute flower brooch
            drawCircle(color = Color(0xFFFFEB3B), radius = 6f, center = Offset(centerX - 18f, centerY - 14f))
        }
    }
}

private fun DrawScope.drawArmsAndHands(
    centerX: Float,
    centerY: Float,
    outfit: OutfitType,
    accessory: AccessoryType,
    emotion: CharacterEmotion,
    isListening: Boolean,
    isDancing: Boolean,
    armOffset: Float
) {
    val skinColor = Color(0xFFFFDFC4)
    val armWidth = 16f
    val armLength = 70f
    val shoulderY = centerY - 32f

    // Glove color if equipped
    val handColor = if (accessory == AccessoryType.DUEL_GLOVE) Color(0xFF7C4DFF) else skinColor

    if (isListening) {
        // Hand to Ear pose (listening gesture)
        // Right Arm goes up to ear
        val armPath = Path().apply {
            moveTo(centerX + 40f, shoulderY)
            quadraticTo(centerX + 75f, shoulderY - 30f, centerX + 52f, shoulderY - 65f)
        }
        drawPath(armPath, color = skinColor, style = Stroke(width = armWidth, cap = StrokeCap.Round))
        drawCircle(color = handColor, radius = 11f, center = Offset(centerX + 52f, shoulderY - 65f))

        // Left Arm relaxed
        drawLine(
            color = skinColor,
            start = Offset(centerX - 40f, shoulderY),
            end = Offset(centerX - 55f, shoulderY + armLength * 0.8f),
            strokeWidth = armWidth,
            cap = StrokeCap.Round
        )
        drawCircle(color = handColor, radius = 10f, center = Offset(centerX - 55f, shoulderY + armLength * 0.8f))
    } else if (isDancing) {
        // Dance poses: Dynamic swinging arms
        val leftArmEndY = shoulderY - 20f + armOffset
        val rightArmEndY = shoulderY + 40f - armOffset

        drawLine(
            color = skinColor,
            start = Offset(centerX - 38f, shoulderY),
            end = Offset(centerX - 65f, leftArmEndY),
            strokeWidth = armWidth,
            cap = StrokeCap.Round
        )
        drawCircle(color = handColor, radius = 10f, center = Offset(centerX - 65f, leftArmEndY))

        drawLine(
            color = skinColor,
            start = Offset(centerX + 38f, shoulderY),
            end = Offset(centerX + 65f, rightArmEndY),
            strokeWidth = armWidth,
            cap = StrokeCap.Round
        )
        drawCircle(color = handColor, radius = 10f, center = Offset(centerX + 65f, rightArmEndY))
    } else if (emotion == CharacterEmotion.EXCITED || emotion == CharacterEmotion.HAPPY) {
        // Waving / Cheerful hands up
        drawLine(
            color = skinColor,
            start = Offset(centerX - 38f, shoulderY),
            end = Offset(centerX - 60f, shoulderY - 25f),
            strokeWidth = armWidth,
            cap = StrokeCap.Round
        )
        drawCircle(color = handColor, radius = 10f, center = Offset(centerX - 60f, shoulderY - 25f))

        drawLine(
            color = skinColor,
            start = Offset(centerX + 38f, shoulderY),
            end = Offset(centerX + 60f, shoulderY - 25f),
            strokeWidth = armWidth,
            cap = StrokeCap.Round
        )
        drawCircle(color = handColor, radius = 10f, center = Offset(centerX + 60f, shoulderY - 25f))
    } else {
        // Idle Arms by side
        drawLine(
            color = skinColor,
            start = Offset(centerX - 38f, shoulderY),
            end = Offset(centerX - 52f, shoulderY + armLength),
            strokeWidth = armWidth,
            cap = StrokeCap.Round
        )
        drawCircle(color = handColor, radius = 10f, center = Offset(centerX - 52f, shoulderY + armLength))

        drawLine(
            color = skinColor,
            start = Offset(centerX + 38f, shoulderY),
            end = Offset(centerX + 52f, shoulderY + armLength),
            strokeWidth = armWidth,
            cap = StrokeCap.Round
        )
        drawCircle(color = handColor, radius = 10f, center = Offset(centerX + 52f, shoulderY + armLength))
    }
}

private fun DrawScope.drawHeadAndFace(
    centerX: Float,
    headCenterY: Float,
    headRadius: Float,
    emotion: CharacterEmotion,
    isBlinking: Boolean,
    mouthRatio: Float,
    hairSway: Float
) {
    val skinColor = Color(0xFFFFDFC4)
    val blushColor = Color(0xFFFF80AB).copy(alpha = 0.55f)

    // Base Head Shape
    val headPath = Path().apply {
        moveTo(centerX - headRadius * 0.85f, headCenterY - headRadius * 0.2f)
        cubicTo(
            centerX - headRadius * 0.85f, headCenterY + headRadius * 0.6f,
            centerX - headRadius * 0.4f, headCenterY + headRadius * 0.95f,
            centerX, headCenterY + headRadius * 1.02f // Soft anime chin
        )
        cubicTo(
            centerX + headRadius * 0.4f, headCenterY + headRadius * 0.95f,
            centerX + headRadius * 0.85f, headCenterY + headRadius * 0.6f,
            centerX + headRadius * 0.85f, headCenterY - headRadius * 0.2f
        )
        close()
    }
    drawPath(headPath, color = skinColor)
    drawCircle(color = skinColor, radius = headRadius * 0.85f, center = Offset(centerX, headCenterY - headRadius * 0.15f))

    // Cute Blushing Cheeks
    drawOval(
        color = blushColor,
        topLeft = Offset(centerX - headRadius * 0.72f, headCenterY + headRadius * 0.2f),
        size = Size(headRadius * 0.32f, headRadius * 0.16f)
    )
    drawOval(
        color = blushColor,
        topLeft = Offset(centerX + headRadius * 0.40f, headCenterY + headRadius * 0.2f),
        size = Size(headRadius * 0.32f, headRadius * 0.16f)
    )

    // Eyes: Téa Gardner / Anzu Mazaki's big radiant anime blue eyes
    val eyeSpacing = headRadius * 0.42f
    val eyeY = headCenterY + headRadius * 0.05f
    val eyeWidth = headRadius * 0.36f
    val eyeHeight = headRadius * 0.42f

    // Draw Left Eye
    drawAnimeEye(
        eyeCenter = Offset(centerX - eyeSpacing, eyeY),
        width = eyeWidth,
        height = eyeHeight,
        isBlinking = isBlinking || emotion == CharacterEmotion.SLEEPY,
        isWink = emotion == CharacterEmotion.WINKING,
        isHappyCurve = emotion == CharacterEmotion.HAPPY || emotion == CharacterEmotion.EXCITED
    )

    // Draw Right Eye
    drawAnimeEye(
        eyeCenter = Offset(centerX + eyeSpacing, eyeY),
        width = eyeWidth,
        height = eyeHeight,
        isBlinking = isBlinking || emotion == CharacterEmotion.SLEEPY,
        isWink = false,
        isHappyCurve = emotion == CharacterEmotion.HAPPY || emotion == CharacterEmotion.EXCITED
    )

    // Cute Little Nose
    drawCircle(
        color = Color(0xFFE5A88B),
        radius = 2.5f,
        center = Offset(centerX, headCenterY + headRadius * 0.36f)
    )

    // Mouth
    val mouthY = headCenterY + headRadius * 0.56f
    drawAnimeMouth(
        centerX = centerX,
        mouthY = mouthY,
        emotion = emotion,
        mouthRatio = mouthRatio
    )
}

private fun DrawScope.drawAnimeEye(
    eyeCenter: Offset,
    width: Float,
    height: Float,
    isBlinking: Boolean,
    isWink: Boolean,
    isHappyCurve: Boolean
) {
    val eyeBlue = Color(0xFF00A2FF)
    val eyeDeepBlue = Color(0xFF0D47A1)
    val lashColor = Color(0xFF2B170B)

    if (isBlinking || isWink) {
        // Closed wink eye curve
        val winkPath = Path().apply {
            moveTo(eyeCenter.x - width * 0.5f, eyeCenter.y)
            quadraticTo(eyeCenter.x, eyeCenter.y + height * 0.35f, eyeCenter.x + width * 0.5f, eyeCenter.y)
        }
        drawPath(winkPath, color = lashColor, style = Stroke(width = 4.5f, cap = StrokeCap.Round))
    } else if (isHappyCurve) {
        // Happy `^` arch eye
        val happyPath = Path().apply {
            moveTo(eyeCenter.x - width * 0.5f, eyeCenter.y + height * 0.1f)
            quadraticTo(eyeCenter.x, eyeCenter.y - height * 0.35f, eyeCenter.x + width * 0.5f, eyeCenter.y + height * 0.1f)
        }
        drawPath(happyPath, color = lashColor, style = Stroke(width = 5f, cap = StrokeCap.Round))
    } else {
        // Open anime eye (White sclera, Blue iris, Pupil, Specular highlight)
        // White base
        drawOval(
            color = Color.White,
            topLeft = Offset(eyeCenter.x - width * 0.5f, eyeCenter.y - height * 0.5f),
            size = Size(width, height)
        )

        // Blue Iris gradient
        drawOval(
            brush = Brush.verticalGradient(
                colors = listOf(eyeDeepBlue, eyeBlue, Color(0xFF80D8FF)),
                startY = eyeCenter.y - height * 0.5f,
                endY = eyeCenter.y + height * 0.5f
            ),
            topLeft = Offset(eyeCenter.x - width * 0.38f, eyeCenter.y - height * 0.48f),
            size = Size(width * 0.76f, height * 0.96f)
        )

        // Dark Pupil
        drawCircle(
            color = Color(0xFF102027),
            radius = width * 0.18f,
            center = eyeCenter
        )

        // Shiny Specular Highlights (Sparkle in eye)
        drawCircle(
            color = Color.White,
            radius = width * 0.12f,
            center = Offset(eyeCenter.x - width * 0.14f, eyeCenter.y - height * 0.2f)
        )
        drawCircle(
            color = Color.White,
            radius = width * 0.06f,
            center = Offset(eyeCenter.x + width * 0.14f, eyeCenter.y + height * 0.16f)
        )

        // Top Eyelash curve
        val lashPath = Path().apply {
            moveTo(eyeCenter.x - width * 0.55f, eyeCenter.y - height * 0.2f)
            quadraticTo(eyeCenter.x, eyeCenter.y - height * 0.62f, eyeCenter.x + width * 0.58f, eyeCenter.y - height * 0.25f)
        }
        drawPath(lashPath, color = lashColor, style = Stroke(width = 4.5f, cap = StrokeCap.Round))
    }
}

private fun DrawScope.drawAnimeMouth(
    centerX: Float,
    mouthY: Float,
    emotion: CharacterEmotion,
    mouthRatio: Float
) {
    val lipColor = Color(0xFFFF5252)

    if (mouthRatio > 0.05f) {
        // Talking open mouth
        val mouthHeight = 16f * mouthRatio + 4f
        val mouthWidth = 24f + 8f * mouthRatio
        drawOval(
            color = lipColor,
            topLeft = Offset(centerX - mouthWidth / 2f, mouthY - mouthHeight / 2f),
            size = Size(mouthWidth, mouthHeight)
        )
        // Inside tongue / teeth
        drawCircle(
            color = Color(0xFFFF8A80),
            radius = mouthHeight * 0.35f,
            center = Offset(centerX, mouthY + mouthHeight * 0.15f)
        )
    } else if (emotion == CharacterEmotion.EATING) {
        // Chewing puffed cheeks mouth
        drawCircle(
            color = lipColor,
            radius = 6f,
            center = Offset(centerX, mouthY)
        )
    } else {
        // Sweet anime smile
        val smilePath = Path().apply {
            moveTo(centerX - 14f, mouthY - 2f)
            quadraticTo(centerX, mouthY + 8f, centerX + 14f, mouthY - 2f)
        }
        drawPath(smilePath, color = Color(0xFF6D214F), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
    }
}

private fun DrawScope.drawAnzuHair(
    centerX: Float,
    headCenterY: Float,
    headRadius: Float,
    hairSway: Float
) {
    // Auburn / Chestnut Brown Anime Hair
    val hairBase = Color(0xFF5D2E16)
    val hairHighlight = Color(0xFF8D4B27)
    val hairShine = Color(0xFFB57048)

    // Back hair bob
    drawCircle(
        color = hairBase,
        radius = headRadius * 1.05f,
        center = Offset(centerX, headCenterY - headRadius * 0.1f)
    )

    // Side hair locks (Anzu's characteristic side bangs that frame her cheeks)
    // Left side lock
    val leftLock = Path().apply {
        moveTo(centerX - headRadius * 0.9f, headCenterY - headRadius * 0.4f)
        quadraticTo(
            centerX - headRadius * 1.1f + hairSway, headCenterY + headRadius * 0.4f,
            centerX - headRadius * 0.72f, headCenterY + headRadius * 0.85f
        )
        quadraticTo(
            centerX - headRadius * 0.65f, headCenterY + headRadius * 0.4f,
            centerX - headRadius * 0.6f, headCenterY - headRadius * 0.2f
        )
        close()
    }
    drawPath(leftLock, color = hairBase)

    // Right side lock
    val rightLock = Path().apply {
        moveTo(centerX + headRadius * 0.9f, headCenterY - headRadius * 0.4f)
        quadraticTo(
            centerX + headRadius * 1.1f - hairSway, headCenterY + headRadius * 0.4f,
            centerX + headRadius * 0.72f, headCenterY + headRadius * 0.85f
        )
        quadraticTo(
            centerX + headRadius * 0.65f, headCenterY + headRadius * 0.4f,
            centerX + headRadius * 0.6f, headCenterY - headRadius * 0.2f
        )
        close()
    }
    drawPath(rightLock, color = hairBase)

    // Front Bangs (layered anime fringe across forehead)
    val bangsPath = Path().apply {
        moveTo(centerX - headRadius * 0.95f, headCenterY - headRadius * 0.5f)
        // Bang Strand 1
        lineTo(centerX - headRadius * 0.55f, headCenterY - headRadius * 0.05f)
        lineTo(centerX - headRadius * 0.4f, headCenterY - headRadius * 0.35f)
        // Bang Strand 2 (Center fringe)
        lineTo(centerX - headRadius * 0.1f, headCenterY - headRadius * 0.02f)
        lineTo(centerX + headRadius * 0.08f, headCenterY - headRadius * 0.35f)
        // Bang Strand 3
        lineTo(centerX + headRadius * 0.45f, headCenterY - headRadius * 0.06f)
        lineTo(centerX + headRadius * 0.6f, headCenterY - headRadius * 0.32f)
        // Bang Strand 4
        lineTo(centerX + headRadius * 0.95f, headCenterY - headRadius * 0.5f)
        // Top skull arc
        cubicTo(
            centerX + headRadius * 0.8f, headCenterY - headRadius * 1.15f,
            centerX - headRadius * 0.8f, headCenterY - headRadius * 1.15f,
            centerX - headRadius * 0.95f, headCenterY - headRadius * 0.5f
        )
        close()
    }
    drawPath(bangsPath, color = hairHighlight)

    // Glossy Anime Hair Shine Arc
    drawArc(
        color = hairShine.copy(alpha = 0.75f),
        startAngle = 200f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(centerX - headRadius * 0.7f, headCenterY - headRadius * 0.95f),
        size = Size(headRadius * 1.4f, headRadius * 0.45f),
        style = Stroke(width = 8f, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawAccessoryItem(
    accessory: AccessoryType,
    centerX: Float,
    headCenterY: Float,
    headRadius: Float
) {
    when (accessory) {
        AccessoryType.CUTE_CAT_EARS -> {
            val earColor = accessory.color
            // Left Cat Ear
            val leftEar = Path().apply {
                moveTo(centerX - headRadius * 0.7f, headCenterY - headRadius * 0.75f)
                lineTo(centerX - headRadius * 0.88f, headCenterY - headRadius * 1.35f)
                lineTo(centerX - headRadius * 0.32f, headCenterY - headRadius * 0.95f)
                close()
            }
            drawPath(leftEar, color = earColor)
            // Inner pink ear
            val leftInner = Path().apply {
                moveTo(centerX - headRadius * 0.68f, headCenterY - headRadius * 0.8f)
                lineTo(centerX - headRadius * 0.82f, headCenterY - headRadius * 1.22f)
                lineTo(centerX - headRadius * 0.42f, headCenterY - headRadius * 0.95f)
                close()
            }
            drawPath(leftInner, color = Color(0xFFFF80AB))

            // Right Cat Ear
            val rightEar = Path().apply {
                moveTo(centerX + headRadius * 0.7f, headCenterY - headRadius * 0.75f)
                lineTo(centerX + headRadius * 0.88f, headCenterY - headRadius * 1.35f)
                lineTo(centerX + headRadius * 0.32f, headCenterY - headRadius * 0.95f)
                close()
            }
            drawPath(rightEar, color = earColor)
            val rightInner = Path().apply {
                moveTo(centerX + headRadius * 0.68f, headCenterY - headRadius * 0.8f)
                lineTo(centerX + headRadius * 0.82f, headCenterY - headRadius * 1.22f)
                lineTo(centerX + headRadius * 0.42f, headCenterY - headRadius * 0.95f)
                close()
            }
            drawPath(rightInner, color = Color(0xFFFF80AB))
        }
        AccessoryType.STAR_SUNGLASSES -> {
            val starColor = accessory.color
            val glassY = headCenterY + headRadius * 0.05f
            // Left star lens
            drawStarLens(centerX - headRadius * 0.42f, glassY, headRadius * 0.26f, starColor)
            // Right star lens
            drawStarLens(centerX + headRadius * 0.42f, glassY, headRadius * 0.26f, starColor)
            // Bridge
            drawLine(
                color = starColor,
                start = Offset(centerX - headRadius * 0.2f, glassY),
                end = Offset(centerX + headRadius * 0.2f, glassY),
                strokeWidth = 4f
            )
        }
        AccessoryType.SMART_GLASSES -> {
            val frameColor = accessory.color
            val glassY = headCenterY + headRadius * 0.05f
            // Left frame
            drawRoundRect(
                color = frameColor,
                topLeft = Offset(centerX - headRadius * 0.62f, glassY - headRadius * 0.16f),
                size = Size(headRadius * 0.45f, headRadius * 0.32f),
                cornerRadius = CornerRadius(6f, 6f),
                style = Stroke(width = 3.5f)
            )
            // Right frame
            drawRoundRect(
                color = frameColor,
                topLeft = Offset(centerX + headRadius * 0.17f, glassY - headRadius * 0.16f),
                size = Size(headRadius * 0.45f, headRadius * 0.32f),
                cornerRadius = CornerRadius(6f, 6f),
                style = Stroke(width = 3.5f)
            )
            // Bridge
            drawLine(
                color = frameColor,
                start = Offset(centerX - headRadius * 0.17f, glassY),
                end = Offset(centerX + headRadius * 0.17f, glassY),
                strokeWidth = 3f
            )
        }
        AccessoryType.MAGIC_HEADBAND -> {
            // Glowing cyan headband
            val ribbonColor = accessory.color
            drawArc(
                color = ribbonColor,
                startAngle = 190f,
                sweepAngle = 160f,
                useCenter = false,
                topLeft = Offset(centerX - headRadius * 0.85f, headCenterY - headRadius * 1.05f),
                size = Size(headRadius * 1.7f, headRadius * 0.85f),
                style = Stroke(width = 9f, cap = StrokeCap.Round)
            )
            // Star jewel on side
            drawCircle(
                color = Color(0xFFFFD700),
                radius = 10f,
                center = Offset(centerX - headRadius * 0.7f, headCenterY - headRadius * 0.65f)
            )
        }
        else -> {}
    }
}

private fun DrawScope.drawStarLens(cx: Float, cy: Float, radius: Float, color: Color) {
    val path = Path()
    val points = 5
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) radius else radius * 0.5f
        val angle = i * PI / points - PI / 2
        val x = cx + (r * cos(angle)).toFloat()
        val y = cy + (r * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color = color.copy(alpha = 0.88f))
}

private fun DrawScope.drawListeningAura(centerX: Float, headCenterY: Float, headRadius: Float) {
    // Sound wave pulse rings around Anzu's ear
    val waveColor = Color(0xFF00E5FF)
    drawCircle(
        color = waveColor.copy(alpha = 0.35f),
        radius = headRadius * 1.3f,
        center = Offset(centerX, headCenterY),
        style = Stroke(width = 3.5f)
    )
    drawCircle(
        color = waveColor.copy(alpha = 0.2f),
        radius = headRadius * 1.55f,
        center = Offset(centerX, headCenterY),
        style = Stroke(width = 2.5f)
    )
}
