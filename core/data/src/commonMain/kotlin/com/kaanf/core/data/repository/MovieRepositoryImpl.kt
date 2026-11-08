package com.kaanf.core.data.repository

import com.kaanf.core.data.dto.FavoriteMovieDto
import com.kaanf.core.data.dto.MovieSearchPageDto
import com.kaanf.core.data.dto.SaveFavoriteMovieDto
import com.kaanf.core.data.networking.delete
import com.kaanf.core.data.networking.get
import com.kaanf.core.data.networking.mapCatching
import com.kaanf.core.data.networking.put
import com.kaanf.core.domain.model.movie.MovieSearchPage
import com.kaanf.core.domain.model.movie.Movie
import com.kaanf.core.domain.repository.MovieRepository
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.Result
import io.ktor.client.HttpClient

class MovieRepositoryImpl(private val httpClient: HttpClient) : MovieRepository {
    override suspend fun search(query: String, language: String, page: Int): Result<MovieSearchPage, DataError.Remote> {
        val normalized = query.trim()
        if (normalized.length !in 2..100 || normalized.any { it.isISOControl() } || page !in 1..500 || !validLanguage(language)) {
            return Result.Failure(DataError.Remote.BAD_REQUEST)
        }
        return httpClient.get<MovieSearchPageDto>(
            route = "/movies/search",
            queryParams = mapOf("q" to normalized, "language" to language, "page" to page),
        ).mapCatching { response ->
            require(response.nextPage == null || response.nextPage in (page + 1)..500)
            MovieSearchPage(response.movies.map { it.toDomain() }, response.nextPage)
        }
    }

    override suspend fun getFavorite(language: String): Result<Movie?, DataError.Remote> {
        if (!validLanguage(language)) return Result.Failure(DataError.Remote.BAD_REQUEST)
        return httpClient.get<FavoriteMovieDto>(
            route = FAVORITE_ROUTE,
            queryParams = mapOf("language" to language),
        ).mapCatching { it.movie?.toDomain() }
    }

    override suspend fun saveFavorite(movieId: Long, language: String): Result<Movie, DataError.Remote> {
        if (movieId <= 0 || !validLanguage(language)) {
            return Result.Failure(DataError.Remote.BAD_REQUEST)
        }
        return httpClient.put<SaveFavoriteMovieDto, FavoriteMovieDto>(
            route = FAVORITE_ROUTE,
            body = SaveFavoriteMovieDto(movieId, language),
        ).mapCatching { requireNotNull(it.movie).toDomain() }
    }

    override suspend fun clearFavorite(): EmptyResult<DataError.Remote> = httpClient.delete<Unit>(FAVORITE_ROUTE)

    private fun validLanguage(language: String) = language.matches(Regex("[a-z]{2}(-[A-Z]{2})?"))

    private companion object {
        const val FAVORITE_ROUTE = "/profile/favorite-movie"
    }
}
