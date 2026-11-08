package com.kaanf.core.domain.model.movie

data class Movie(
    val id: Long,
    val title: String,
    val releaseYear: String?,
    val posterUrl: String?,
    val tmdbUrl: String,
)
data class MovieSearchPage(val movies: List<Movie>, val nextPage: Int?)
