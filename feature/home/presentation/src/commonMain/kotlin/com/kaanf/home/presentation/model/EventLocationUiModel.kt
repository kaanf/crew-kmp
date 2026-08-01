package com.kaanf.home.presentation.model

/** Yalnız koordinatı girilmiş mekânlar için üretilir; yol tarifi kartı bu model null değilse çizilir. */
data class EventLocationUiModel(
    val name: String,
    val address: String,
    val district: String,
    val latitude: Double,
    val longitude: Double,
)

// Android'de Google Maps, iOS'ta yüklü harita uygulaması açılır; koordinat tam nokta verir.
fun EventLocationUiModel.toDirectionsUri(): String =
    "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
