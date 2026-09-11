package com.example.model

import androidx.compose.ui.graphics.Color

enum class GameTab(val titleAr: String, val titleEn: String) {
    LIVING_DANCE("الغرفة والرقص", "Dance Room"),
    WARDROBE("خزانة الملابس", "Wardrobe"),
    KITCHEN("المطبخ والطاقة", "Kitchen"),
    ARCADE("الألعاب المصغرة", "Arcade")
}

enum class CharacterEmotion {
    HAPPY,
    EXCITED,
    LISTENING,
    TALKING,
    EATING,
    DANCING,
    WINKING,
    SURPRISED,
    SLEEPY
}

enum class OutfitType(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val price: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
    val descriptionAr: String
) {
    SCHOOL_UNIFORM(
        id = "school_uniform",
        nameAr = "الزي المدرسي (دومينو)",
        nameEn = "Domino High Uniform",
        price = 0,
        primaryColor = Color(0xFF2962FF),
        secondaryColor = Color(0xFFFF4081),
        descriptionAr = "الزي الكلاسيكي لثانوية دومينو مع الوشاح الوردي الأنيق"
    ),
    DANCE_TRAINING(
        id = "dance_training",
        nameAr = "ملابس تدريب الرقص",
        nameEn = "Street Dance Gear",
        price = 150,
        primaryColor = Color(0xFFFF5722),
        secondaryColor = Color(0xFF212121),
        descriptionAr = "زي رياضي مريح وخفيف لتدريبات الرقص الحماسية"
    ),
    DARK_MAGICIAN_GIRL(
        id = "dark_magician_girl",
        nameAr = "زي فتاة الساحر الأسود",
        nameEn = "Magician Girl Cosplay",
        price = 350,
        primaryColor = Color(0xFFFF69B4),
        secondaryColor = Color(0xFF00E5FF),
        descriptionAr = "زي سحري مستوحى من بطاقة فتاة الساحر مع القبعة والعباءة"
    ),
    IDOL_SPARKLE(
        id = "idol_sparkle",
        nameAr = "فستان نجمة الاستعراض",
        nameEn = "Idol Sparkle Dress",
        price = 500,
        primaryColor = Color(0xFFAB47BC),
        secondaryColor = Color(0xFFFFD700),
        descriptionAr = "فستان براق للعروض المسرحية الكبرى مع أشرطة لامعة"
    ),
    SUMMER_CASUAL(
        id = "summer_casual",
        nameAr = "ملابس الصيف الكاجوال",
        nameEn = "Casual Summer Wear",
        price = 100,
        primaryColor = Color(0xFFFFB74D),
        secondaryColor = Color(0xFF4FC3F7),
        descriptionAr = "إطلالة صيفية مرحة للتنزه مع الأصدقاء"
    )
}

enum class AccessoryType(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val category: String,
    val price: Int,
    val color: Color
) {
    NONE("none", "بدون إكسسوار", "None", "All", 0, Color.Transparent),
    CUTE_CAT_EARS("cat_ears", "أذنا قطة لطيفة", "Cat Ears", "Head", 80, Color(0xFFFF4081)),
    STAR_SUNGLASSES("star_glasses", "نظارة النجوم العصرية", "Star Glasses", "Face", 120, Color(0xFFFFD700)),
    DUEL_GLOVE("duel_glove", "قفاز المبارزة الأسطوري", "Duelist Glove", "Hands", 180, Color(0xFF7C4DFF)),
    MAGIC_HEADBAND("magic_headband", "طوق الرأس السحري", "Magic Ribbon", "Head", 60, Color(0xFF00E5FF)),
    SMART_GLASSES("smart_glasses", "نظارة ذكية كلاسيكية", "Smart Glasses", "Face", 90, Color(0xFF424242))
}

data class FoodItem(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val emoji: String,
    val energyGain: Int,
    val happinessGain: Int,
    val price: Int,
    val descriptionAr: String
)

enum class DanceRoutine(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val emoji: String,
    val durationSec: Int,
    val energyCost: Int,
    val styleDescAr: String
) {
    POP_SHUFFLE("pop_shuffle", "رقصة الشفل الحماسية", "Pop Shuffle", "👟", 6, 8, "خطوات سريعة وحركات إيقاعية مبهجة"),
    IDOL_CHOREO("idol_choreo", "استعراض الآيدول المتألق", "Idol Choreo", "✨", 8, 12, "حركات رقيقة مع تلويح اليدين وإشارات القلب"),
    ACROBATIC_SPIN("acrobatic_spin", "الدوران البهلواني السريع", "Acrobatic Spin", "💫", 5, 15, "دوران 360 درجة مع قفزة استعراضية عالية"),
    HIPHOP_GROOVE("hiphop_groove", "هيب هوب ستريت دانس", "HipHop Groove", "🔥", 7, 10, "إيقاعات قوية وحركات أذرع عصرية"),
    MAGIC_VICTORY("magic_victory", "وقفة النصر السحرية", "Magic Victory", "⭐", 6, 5, "حركات استعراض البطاقات مع وقفة فخر")
}

data class RhythmNote(
    val id: Long,
    val lane: Int, // 0 to 3
    var positionY: Float = 0f, // 0.0 to 1.0 (top to bottom)
    val speed: Float = 0.015f,
    var isHit: Boolean = false,
    var isMissed: Boolean = false
)

data class DuelCard(
    val id: Int,
    val cardKey: String,
    val nameAr: String,
    val nameEn: String,
    val iconEmoji: String,
    val color: Color,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)

data class Particle(
    val id: Long,
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float,
    var alpha: Float = 1f,
    val size: Float,
    val color: Color,
    val symbol: String = "✨"
)
