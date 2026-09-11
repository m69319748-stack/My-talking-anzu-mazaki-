package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.GameAudioManager
import com.example.model.AccessoryType
import com.example.model.CharacterEmotion
import com.example.model.DanceRoutine
import com.example.model.DuelCard
import com.example.model.FoodItem
import com.example.model.GameTab
import com.example.model.OutfitType
import com.example.model.RhythmNote
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GameUiState(
    val currentTab: GameTab = GameTab.LIVING_DANCE,
    val coins: Int = 350,
    val level: Int = 1,
    val xp: Int = 20,
    val energy: Int = 85,
    val happiness: Int = 90,
    val hunger: Int = 40,
    val currentOutfit: OutfitType = OutfitType.SCHOOL_UNIFORM,
    val unlockedOutfits: Set<String> = setOf(OutfitType.SCHOOL_UNIFORM.id),
    val currentAccessory: AccessoryType = AccessoryType.NONE,
    val unlockedAccessories: Set<String> = setOf(AccessoryType.NONE.id),
    val emotion: CharacterEmotion = CharacterEmotion.HAPPY,
    val isListening: Boolean = false,
    val isSpeaking: Boolean = false,
    val speechBubbleText: String? = "مرحباً بك! أنا أنزو مازاكي ✨",
    val isDancing: Boolean = false,
    val currentDanceRoutine: DanceRoutine? = null,
    val danceStep: Int = 0,
    val isMusicPlaying: Boolean = false,
    val currentMusicTrackName: String = "Energetic Dance Beat #1",
    val rhythmHighScore: Int = 0,
    val cardHighScore: Int = 0,
    val isRhythmGameActive: Boolean = false,
    val rhythmScore: Int = 0,
    val rhythmCombo: Int = 0,
    val rhythmMultiplier: Int = 1,
    val rhythmNotes: List<RhythmNote> = emptyList(),
    val rhythmFeedback: String? = null,
    val rhythmGameOver: Boolean = false,
    val isCardGameActive: Boolean = false,
    val cardGrid: List<DuelCard> = emptyList(),
    val cardMoves: Int = 0,
    val cardMatches: Int = 0,
    val cardTimeRemainingSec: Int = 60,
    val cardGameOver: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("anzu_game_prefs", Context.MODE_PRIVATE)
    val audioManager = GameAudioManager(application)

    private val _uiState = MutableStateFlow(loadInitialState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var danceJob: Job? = null
    private var rhythmJob: Job? = null
    private var cardTimerJob: Job? = null
    private var flippedCardIndex1: Int? = null
    private var flippedCardIndex2: Int? = null
    private var isEvaluatingCards = false

    val foodMenu = listOf(
        FoodItem("bento", "علبة بينتو الصداقة", "Friendship Bento", "🍱", 35, 25, 40, "وجبة يابانية متكاملة تعيد طاقة أنزو للتدريب"),
        FoodItem("boba", "شاي البوبا والحليب", "Bubble Milk Tea", "🧋", 20, 30, 25, "مشروب منعش حلو ولذيذ بنكهة السكر البني"),
        FoodItem("cake", "كعكة الفراولة اللذيذة", "Strawberry Shortcake", "🍰", 25, 35, 35, "حلوى رقيقة غنية بالكريمة والفراولة الطازجة"),
        FoodItem("burger", "دبل تشيز برجر وبطاطس", "Cheeseburger & Fries", "🍔", 45, 20, 50, "وجبة مشبعة سريعة لزيادة الطاقة القصوى"),
        FoodItem("energy", "مشروب طاقة الرقص", "Dance Energy Drink", "⚡", 50, 15, 30, "مشروب فيتامينات فوار لإعادة النشاط الفوري"),
        FoodItem("ramen", "وعاء الرامن الساخن", "Hot Ramen Bowl", "🍜", 40, 25, 45, "حساء نودلز شهي مع البيض والأعشاب البحرية")
    )

    val musicTracks = listOf(
        "Energetic Dance Beat #1",
        "Anime Duel Pop Anthem",
        "Idol Sparkle Groove",
        "Lofi Chill Dance Lounge"
    )

    init {
        audioManager.onTtsStart = {
            _uiState.update { it.copy(isSpeaking = true, emotion = CharacterEmotion.TALKING) }
        }
        audioManager.onTtsDone = {
            _uiState.update { it.copy(isSpeaking = false, emotion = CharacterEmotion.HAPPY) }
        }
        // Speak warm initial greeting
        viewModelScope.launch {
            delay(800)
            speakAnzu("أهلاً بك! أنا أنزو مازاكي، سعيدة جداً بلقائك!")
        }
    }

    private fun loadInitialState(): GameUiState {
        val savedCoins = prefs.getInt("coins", 350)
        val savedLevel = prefs.getInt("level", 1)
        val savedXp = prefs.getInt("xp", 20)
        val savedEnergy = prefs.getInt("energy", 85)
        val savedHappiness = prefs.getInt("happiness", 90)
        val savedOutfitId = prefs.getString("outfit", OutfitType.SCHOOL_UNIFORM.id) ?: OutfitType.SCHOOL_UNIFORM.id
        val savedOutfits = prefs.getStringSet("unlocked_outfits", setOf(OutfitType.SCHOOL_UNIFORM.id)) ?: setOf(OutfitType.SCHOOL_UNIFORM.id)
        val savedAccessoryId = prefs.getString("accessory", AccessoryType.NONE.id) ?: AccessoryType.NONE.id
        val savedAccessories = prefs.getStringSet("unlocked_accessories", setOf(AccessoryType.NONE.id)) ?: setOf(AccessoryType.NONE.id)
        val savedRhythmHigh = prefs.getInt("rhythm_high_score", 0)
        val savedCardHigh = prefs.getInt("card_high_score", 0)

        val outfit = OutfitType.values().find { it.id == savedOutfitId } ?: OutfitType.SCHOOL_UNIFORM
        val accessory = AccessoryType.values().find { it.id == savedAccessoryId } ?: AccessoryType.NONE

        return GameUiState(
            coins = savedCoins,
            level = savedLevel,
            xp = savedXp,
            energy = savedEnergy,
            happiness = savedHappiness,
            currentOutfit = outfit,
            unlockedOutfits = savedOutfits,
            currentAccessory = accessory,
            unlockedAccessories = savedAccessories,
            rhythmHighScore = savedRhythmHigh,
            cardHighScore = savedCardHigh
        )
    }

    private fun saveStats() {
        val s = _uiState.value
        prefs.edit()
            .putInt("coins", s.coins)
            .putInt("level", s.level)
            .putInt("xp", s.xp)
            .putInt("energy", s.energy)
            .putInt("happiness", s.happiness)
            .putString("outfit", s.currentOutfit.id)
            .putStringSet("unlocked_outfits", s.unlockedOutfits)
            .putString("accessory", s.currentAccessory.id)
            .putStringSet("unlocked_accessories", s.unlockedAccessories)
            .putInt("rhythm_high_score", s.rhythmHighScore)
            .putInt("card_high_score", s.cardHighScore)
            .apply()
    }

    fun selectTab(tab: GameTab) {
        audioManager.playPopSound()
        // stop any active mini-games or dancing when switching tabs
        stopDancing()
        _uiState.update { it.copy(currentTab = tab, isListening = false) }
    }

    // Touch & Petting Interactions
    fun onPatHead() {
        audioManager.playPopSound()
        gainHappiness(5)
        gainXp(3)
        val phrases = listOf(
            "هيهي! هذا لطيف جداً منك!",
            "شكراً لك! أشعر بالسعادة والنشاط!",
            "أحب عندما تشجعني!"
        )
        val text = phrases.random()
        setSpeech(text)
        _uiState.update { it.copy(emotion = CharacterEmotion.HAPPY) }
    }

    fun onTickleBelly() {
        audioManager.playPopSound()
        gainHappiness(8)
        gainXp(4)
        val phrases = listOf(
            "هههه! هذا يدغدغني جداً!",
            "توقف هههه، لا أستطيع التوقف عن الضحك!",
            "أنت مرح للغاية!"
        )
        val text = phrases.random()
        setSpeech(text)
        _uiState.update { it.copy(emotion = CharacterEmotion.EXCITED) }
    }

    fun onHighFiveHands() {
        audioManager.playCoinSound()
        gainHappiness(6)
        gainXp(5)
        val phrases = listOf(
            "هاي فايف! الصداقة هي قوتنا الدائمة!",
            "معاً نستطيع تحقيق أي حلم!",
            "أنت أفضل صديق مبارز!"
        )
        val text = phrases.random()
        setSpeech(text)
        _uiState.update { it.copy(emotion = CharacterEmotion.WINKING) }
    }

    fun onAcrobaticSpin() {
        audioManager.playCoinSound()
        gainXp(8)
        addCoins(5)
        val phrases = listOf(
            "يا له من دوران رائع! ما رأيك في هذه الحركة؟",
            "استعراض بهلواني كامل بنجاح!",
            "طاقتي في القمة اليوم!"
        )
        val text = phrases.random()
        setSpeech(text)
        _uiState.update { it.copy(emotion = CharacterEmotion.EXCITED) }
    }

    // Voice & Speech repeating
    fun toggleVoiceListening() {
        if (_uiState.value.isListening) {
            audioManager.stopListening()
            _uiState.update { it.copy(isListening = false, emotion = CharacterEmotion.HAPPY) }
        } else {
            audioManager.playPopSound()
            _uiState.update {
                it.copy(
                    isListening = true,
                    emotion = CharacterEmotion.LISTENING,
                    speechBubbleText = "أنا أستمع إليك باهتمام... تحدث الآن! 👂✨"
                )
            }
            audioManager.startListening { recognizedText ->
                _uiState.update { it.copy(isListening = false) }
                repeatVoiceText(recognizedText)
            }
        }
    }

    fun repeatVoiceText(text: String) {
        if (text.isBlank()) return
        val response = "قلت: \"$text\"! نبرتك رائعة جداً! 💖"
        setSpeech(response)
        speakAnzu(text)
        gainHappiness(6)
        addCoins(5)
    }

    fun speakQuickPhrase(phrase: String) {
        setSpeech(phrase)
        speakAnzu(phrase)
    }

    private fun setSpeech(text: String) {
        _uiState.update { it.copy(speechBubbleText = text) }
    }

    private fun speakAnzu(text: String) {
        audioManager.speak(text)
    }

    // Dance & Music features
    fun startDanceRoutine(routine: DanceRoutine) {
        if (_uiState.value.energy < routine.energyCost) {
            setSpeech("طاقتي منخفضة قليلاً! أطمعني بعض الطعام من المطبخ أولاً 🥪")
            speakAnzu("طاقتي منخفضة، لنذهب للمطبخ أولاً!")
            return
        }

        stopDancing()
        audioManager.playCoinSound()
        _uiState.update {
            it.copy(
                isDancing = true,
                currentDanceRoutine = routine,
                energy = (it.energy - routine.energyCost).coerceAtLeast(0),
                emotion = CharacterEmotion.DANCING,
                speechBubbleText = "شاهد رقصة ${routine.nameAr}! 🎶💃"
            )
        }
        saveStats()

        danceJob = viewModelScope.launch {
            val totalSteps = routine.durationSec * 3
            for (step in 0 until totalSteps) {
                _uiState.update { it.copy(danceStep = step) }
                audioManager.playDanceMelodyNote(step)
                delay(330)
            }
            // Finish routine
            stopDancing()
            gainHappiness(15)
            gainXp(12)
            addCoins(20)
            setSpeech("أنهيت الرقصة بنجاح! شكراً على تشجيعك الحماسي ⭐")
            speakAnzu("أنهيت الرقصة بنجاح! شكراً لك!")
        }
    }

    fun stopDancing() {
        danceJob?.cancel()
        danceJob = null
        _uiState.update {
            it.copy(
                isDancing = false,
                currentDanceRoutine = null,
                danceStep = 0,
                emotion = CharacterEmotion.HAPPY
            )
        }
    }

    fun toggleMusic() {
        audioManager.playPopSound()
        val newState = !_uiState.value.isMusicPlaying
        _uiState.update { it.copy(isMusicPlaying = newState) }
        if (newState) {
            viewModelScope.launch {
                for (i in 0..8) {
                    if (!_uiState.value.isMusicPlaying) break
                    audioManager.playDanceMelodyNote(i)
                    delay(300)
                }
            }
        }
    }

    fun nextMusicTrack() {
        audioManager.playPopSound()
        val nextIdx = (_uiState.value.musicTracksIndex() + 1) % musicTracks.size
        _uiState.update {
            it.copy(currentMusicTrackName = musicTracks[nextIdx])
        }
    }

    private fun GameUiState.musicTracksIndex(): Int {
        val idx = musicTracks.indexOf(currentMusicTrackName)
        return if (idx >= 0) idx else 0
    }

    // Wardrobe & Cosmetics
    fun equipOutfit(outfit: OutfitType) {
        if (!_uiState.value.unlockedOutfits.contains(outfit.id)) {
            // Purchase attempt
            if (_uiState.value.coins >= outfit.price) {
                audioManager.playCardMatchSound()
                _uiState.update {
                    it.copy(
                        coins = it.coins - outfit.price,
                        unlockedOutfits = it.unlockedOutfits + outfit.id,
                        currentOutfit = outfit,
                        emotion = CharacterEmotion.WINKING,
                        speechBubbleText = "ارتديت ${outfit.nameAr}! ما رأيك في مظهري الجديد؟ ✨"
                    )
                }
                saveStats()
                speakAnzu("ما رأيك في مظهري الجديد؟ يبدو رائعاً!")
            } else {
                audioManager.playPopSound()
                setSpeech("تحتاج إلى ${outfit.price} قطعة ذهبية! العب في قسم الألعاب لجمع المزيد 🪙")
            }
        } else {
            audioManager.playPopSound()
            _uiState.update {
                it.copy(
                    currentOutfit = outfit,
                    emotion = CharacterEmotion.HAPPY,
                    speechBubbleText = "تم تغيير الزي إلى ${outfit.nameAr}!"
                )
            }
            saveStats()
        }
    }

    fun equipAccessory(accessory: AccessoryType) {
        if (accessory != AccessoryType.NONE && !_uiState.value.unlockedAccessories.contains(accessory.id)) {
            if (_uiState.value.coins >= accessory.price) {
                audioManager.playCardMatchSound()
                _uiState.update {
                    it.copy(
                        coins = it.coins - accessory.price,
                        unlockedAccessories = it.unlockedAccessories + accessory.id,
                        currentAccessory = accessory,
                        speechBubbleText = "إكسسوار رائع! ${accessory.nameAr} يناسبني تماماً 🎀"
                    )
                }
                saveStats()
            } else {
                audioManager.playPopSound()
                setSpeech("تحتاج إلى ${accessory.price} قطعة ذهبية لفتح هذا الإكسسوار 🪙")
            }
        } else {
            audioManager.playPopSound()
            _uiState.update {
                it.copy(currentAccessory = accessory)
            }
            saveStats()
        }
    }

    // Kitchen & Food
    fun feedFood(food: FoodItem) {
        if (_uiState.value.coins < food.price) {
            audioManager.playPopSound()
            setSpeech("لا تملك ذهباً كافياً لشراء ${food.nameAr}! العب لتكسب المزيد 🪙")
            return
        }

        audioManager.playMunchSound()
        _uiState.update {
            val newCoins = it.coins - food.price
            val newEnergy = (it.energy + food.energyGain).coerceAtMost(100)
            val newHappiness = (it.happiness + food.happinessGain).coerceAtMost(100)
            it.copy(
                coins = newCoins,
                energy = newEnergy,
                happiness = newHappiness,
                emotion = CharacterEmotion.EATING,
                speechBubbleText = "يممم! ${food.nameAr} لذيذ جداً! شكراً لك 😋❤️"
            )
        }
        gainXp(8)
        saveStats()

        viewModelScope.launch {
            delay(1400)
            _uiState.update { it.copy(emotion = CharacterEmotion.HAPPY) }
            speakAnzu("يممم! شكراً لك، استعدت كل طاقتي للتدريب!")
        }
    }

    // Mini-Game 1: Rhythm Beats Game
    fun startRhythmGame() {
        audioManager.playCardMatchSound()
        rhythmJob?.cancel()
        _uiState.update {
            it.copy(
                isRhythmGameActive = true,
                rhythmScore = 0,
                rhythmCombo = 0,
                rhythmMultiplier = 1,
                rhythmNotes = emptyList(),
                rhythmFeedback = "استعد! READY... GO!",
                rhythmGameOver = false,
                emotion = CharacterEmotion.DANCING
            )
        }

        rhythmJob = viewModelScope.launch {
            var noteCounter = 0L
            var gameTicks = 0
            val maxTicks = 350 // ~30 seconds of high-energy rhythm gameplay

            while (gameTicks < maxTicks && _uiState.value.isRhythmGameActive) {
                delay(80)
                gameTicks++

                // Spawn new notes periodically
                val currentNotes = _uiState.value.rhythmNotes.toMutableList()
                if (gameTicks % 5 == 0 && Random.nextFloat() > 0.25f) {
                    val lane = Random.nextInt(4)
                    currentNotes.add(
                        RhythmNote(
                            id = ++noteCounter,
                            lane = lane,
                            positionY = 0f,
                            speed = 0.045f
                        )
                    )
                }

                // Advance positions
                val updatedNotes = mutableListOf<RhythmNote>()
                for (note in currentNotes) {
                    if (!note.isHit) {
                        note.positionY += note.speed
                        if (note.positionY > 1.05f && !note.isMissed) {
                            note.isMissed = true
                            _uiState.update {
                                it.copy(
                                    rhythmCombo = 0,
                                    rhythmMultiplier = 1,
                                    rhythmFeedback = "فائت... MISS!"
                                )
                            }
                        } else if (note.positionY <= 1.05f) {
                            updatedNotes.add(note)
                        }
                    }
                }

                _uiState.update { it.copy(rhythmNotes = updatedNotes) }
            }

            // Game complete
            val finalScore = _uiState.value.rhythmScore
            val earnedCoins = (finalScore / 40).coerceAtLeast(25)
            val newHigh = maxOf(_uiState.value.rhythmHighScore, finalScore)
            addCoins(earnedCoins)
            gainXp(25)
            audioManager.playCardMatchSound()

            _uiState.update {
                it.copy(
                    rhythmGameOver = true,
                    rhythmHighScore = newHigh,
                    rhythmFeedback = "انتهت الجولة! حصلت على $finalScore نقطة و $earnedCoins قطعة ذهبية! 🏆"
                )
            }
            saveStats()
            speakAnzu("أداء إيقاعي مذهل! أحسنت صنعاً!")
        }
    }

    fun onHitRhythmNote(lane: Int) {
        val notesInLane = _uiState.value.rhythmNotes.filter { it.lane == lane && !it.isHit && !it.isMissed }
        // Target strike zone is around positionY = 0.85f (0.70f to 0.98f)
        val targetNote = notesInLane.minByOrNull { kotlin.math.abs(it.positionY - 0.85f) }

        if (targetNote != null) {
            val dist = kotlin.math.abs(targetNote.positionY - 0.85f)
            if (dist < 0.08f) {
                // PERFECT
                targetNote.isHit = true
                audioManager.playRhythmHit(perfect = true)
                _uiState.update {
                    val newCombo = it.rhythmCombo + 1
                    val newMult = (newCombo / 5 + 1).coerceAtMost(4)
                    val points = 100 * newMult
                    it.copy(
                        rhythmScore = it.rhythmScore + points,
                        rhythmCombo = newCombo,
                        rhythmMultiplier = newMult,
                        rhythmFeedback = "مثالي! PERFECT! +$points ⭐"
                    )
                }
            } else if (dist < 0.18f) {
                // GREAT
                targetNote.isHit = true
                audioManager.playRhythmHit(perfect = false)
                _uiState.update {
                    val newCombo = it.rhythmCombo + 1
                    val newMult = (newCombo / 5 + 1).coerceAtMost(4)
                    val points = 60 * newMult
                    it.copy(
                        rhythmScore = it.rhythmScore + points,
                        rhythmCombo = newCombo,
                        rhythmMultiplier = newMult,
                        rhythmFeedback = "رائع! GREAT! +$points ✨"
                    )
                }
            } else {
                audioManager.playPopSound()
            }
        } else {
            audioManager.playPopSound()
        }
    }

    fun closeRhythmGame() {
        rhythmJob?.cancel()
        rhythmJob = null
        _uiState.update { it.copy(isRhythmGameActive = false, rhythmGameOver = false) }
    }

    // Mini-Game 2: Card Match & Guessing Game
    fun startCardGame() {
        cardTimerJob?.cancel()
        audioManager.playCardFlipSound()
        val cardTemplates = listOf(
            Triple("dark_magician_girl", "فتاة الساحر", "💖"),
            Triple("blue_eyes", "التنين الأبيض", "🐉"),
            Triple("kuriboh", "كوريبو اللطيف", "🧶"),
            Triple("millennium_eye", "عين الألفية", "👁️"),
            Triple("friendship_star", "نجمة الصداقة", "⭐"),
            Triple("time_wizard", "ساحر الوقت", "⏰"),
            Triple("red_eyes", "التنين الأسود", "🔥"),
            Triple("polymerization", "بطاقة الدمج", "🌀")
        )

        val generatedCards = mutableListOf<DuelCard>()
        var id = 0
        cardTemplates.forEach { (key, nameAr, emoji) ->
            // Pair 1
            generatedCards.add(
                DuelCard(
                    id = ++id,
                    cardKey = key,
                    nameAr = nameAr,
                    nameEn = key,
                    iconEmoji = emoji,
                    color = Color(0xFF673AB7)
                )
            )
            // Pair 2
            generatedCards.add(
                DuelCard(
                    id = ++id,
                    cardKey = key,
                    nameAr = nameAr,
                    nameEn = key,
                    iconEmoji = emoji,
                    color = Color(0xFF673AB7)
                )
            )
        }
        generatedCards.shuffle()

        flippedCardIndex1 = null
        flippedCardIndex2 = null
        isEvaluatingCards = false

        _uiState.update {
            it.copy(
                isCardGameActive = true,
                cardGrid = generatedCards,
                cardMoves = 0,
                cardMatches = 0,
                cardTimeRemainingSec = 50,
                cardGameOver = false,
                emotion = CharacterEmotion.EXCITED
            )
        }

        // Start Countdown Timer
        cardTimerJob = viewModelScope.launch {
            while (_uiState.value.isCardGameActive && _uiState.value.cardTimeRemainingSec > 0 && !_uiState.value.cardGameOver) {
                delay(1000)
                _uiState.update { it.copy(cardTimeRemainingSec = it.cardTimeRemainingSec - 1) }
            }
            if (_uiState.value.isCardGameActive && !_uiState.value.cardGameOver) {
                // Time up
                _uiState.update { it.copy(cardGameOver = true) }
                speakAnzu("انتهى الوقت! كانت محاولة مبارزة رائعة!")
            }
        }
    }

    fun onFlipCard(index: Int) {
        if (isEvaluatingCards) return
        val currentCards = _uiState.value.cardGrid.toMutableList()
        val card = currentCards.getOrNull(index) ?: return
        if (card.isFaceUp || card.isMatched) return

        audioManager.playCardFlipSound()
        card.isFaceUp = true
        _uiState.update { it.copy(cardGrid = currentCards) }

        if (flippedCardIndex1 == null) {
            flippedCardIndex1 = index
        } else if (flippedCardIndex2 == null) {
            flippedCardIndex2 = index
            isEvaluatingCards = true
            _uiState.update { it.copy(cardMoves = it.cardMoves + 1) }

            val card1 = currentCards[flippedCardIndex1!!]
            val card2 = currentCards[flippedCardIndex2!!]

            viewModelScope.launch {
                delay(550)
                if (card1.cardKey == card2.cardKey) {
                    // Match found!
                    audioManager.playCardMatchSound()
                    card1.isMatched = true
                    card2.isMatched = true
                    val newMatches = _uiState.value.cardMatches + 1
                    val allMatched = newMatches == 8

                    _uiState.update {
                        it.copy(
                            cardGrid = currentCards,
                            cardMatches = newMatches,
                            cardGameOver = allMatched
                        )
                    }

                    if (allMatched) {
                        cardTimerJob?.cancel()
                        val bonusCoins = 50 + (_uiState.value.cardTimeRemainingSec * 2)
                        addCoins(bonusCoins)
                        gainXp(30)
                        val newScore = newMatches * 100 + (_uiState.value.cardTimeRemainingSec * 20)
                        val newHigh = maxOf(_uiState.value.cardHighScore, newScore)
                        _uiState.update { it.copy(cardHighScore = newHigh) }
                        saveStats()
                        speakAnzu("تهانينا! جمعت كل بطاقات المبارزة بنجاح!")
                    }
                } else {
                    // No match, flip back
                    card1.isFaceUp = false
                    card2.isFaceUp = false
                    _uiState.update { it.copy(cardGrid = currentCards) }
                }
                flippedCardIndex1 = null
                flippedCardIndex2 = null
                isEvaluatingCards = false
            }
        }
    }

    fun closeCardGame() {
        cardTimerJob?.cancel()
        cardTimerJob = null
        _uiState.update { it.copy(isCardGameActive = false, cardGameOver = false) }
    }

    // Progression & Stats
    private fun addCoins(amount: Int) {
        audioManager.playCoinSound()
        _uiState.update { it.copy(coins = it.coins + amount) }
        saveStats()
    }

    private fun gainHappiness(amount: Int) {
        _uiState.update { it.copy(happiness = (it.happiness + amount).coerceAtMost(100)) }
        saveStats()
    }

    private fun gainXp(amount: Int) {
        _uiState.update {
            val newXp = it.xp + amount
            val levelUpThreshold = it.level * 80
            if (newXp >= levelUpThreshold) {
                audioManager.playCardMatchSound()
                it.copy(level = it.level + 1, xp = newXp - levelUpThreshold, coins = it.coins + 50)
            } else {
                it.copy(xp = newXp)
            }
        }
        saveStats()
    }

    override fun onCleared() {
        super.onCleared()
        audioManager.release()
    }
}
