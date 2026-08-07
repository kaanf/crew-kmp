package com.kaanf.game.domain.model

/**
 * Görev kategorisi. Sunucu kataloğundaki değerlerle eşleşir; tanınmayan bir değer
 * gelirse [Unknown]'a düşülür (ileri uyumluluk).
 */
enum class TaskCategory {
    Icebreaker,
    Team,
    Storytime,
    Challenge,
    Photo,
    Bold,
    Confession,
    Flirty,
    Unknown,
}
