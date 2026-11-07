package com.kaanf.core.data.dto

import com.kaanf.core.domain.model.music.MusicTrack
import kotlinx.serialization.Serializable

@Serializable
internal data class MusicTrackDto(
    val id: String,
    val title: String,
    val artists: List<String>,
    val spotifyUrl: String,
) {
    fun toDomain(): MusicTrack {
        require(id.matches(Regex("[A-Za-z0-9]{22}")))
        require(title.isNotBlank() && artists.isNotEmpty() && artists.all { it.isNotBlank() })
        require(spotifyUrl == "https://open.spotify.com/track/$id")
        return MusicTrack(id, title, artists, spotifyUrl)
    }
}

@Serializable
internal data class MusicSearchPageDto(val tracks: List<MusicTrackDto>, val nextOffset: Int? = null)

@Serializable
internal data class FavoriteMusicDto(val track: MusicTrackDto? = null)

@Serializable
internal data class SaveFavoriteMusicDto(val trackId: String, val market: String)
