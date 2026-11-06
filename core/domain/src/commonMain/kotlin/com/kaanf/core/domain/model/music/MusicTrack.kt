package com.kaanf.core.domain.model.music

/** Spotify catalog metadata only; no playback or Spotify user authorization. */
data class MusicTrack(
    val id: String,
    val title: String,
    val artists: List<String>,
    val spotifyUrl: String,
)

data class MusicSearchPage(val tracks: List<MusicTrack>, val nextOffset: Int?)
