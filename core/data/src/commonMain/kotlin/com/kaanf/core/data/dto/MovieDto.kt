package com.kaanf.core.data.dto

import com.kaanf.core.domain.model.movie.Movie
import kotlinx.serialization.Serializable

@Serializable
internal data class MovieDto(val id: Long, val title: String, val releaseYear: String? = null,
    val posterUrl: String? = null, val tmdbUrl: String) {
    fun toDomain(): Movie {
        require(id > 0 && title.isNotBlank())
        require(tmdbUrl == "https://www.themoviedb.org/movie/$id")
        require(releaseYear == null || releaseYear.matches(Regex("[0-9]{4}")))
        require(posterUrl == null || posterUrl.matches(Regex("https://image\\.tmdb\\.org/t/p/w342/[A-Za-z0-9]+\\.[A-Za-z0-9]+")))
        return Movie(id, title, releaseYear, posterUrl, tmdbUrl)
    }
}
@Serializable
internal data class MovieSearchPageDto(val movies: List<MovieDto>, val nextPage: Int? = null)
@Serializable
internal data class FavoriteMovieDto(val movie: MovieDto? = null)
@Serializable
internal data class SaveFavoriteMovieDto(val movieId: Long, val language: String)
