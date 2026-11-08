package com.kaanf.core.domain.repository

import com.kaanf.core.domain.model.movie.MovieSearchPage
import com.kaanf.core.domain.model.movie.Movie
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.Result

/** All calls use the Crew session. Writes require connectivity and are never queued. */
interface MovieRepository {
    suspend fun search(query: String, language: String, page: Int = 1): Result<MovieSearchPage, DataError.Remote>
    suspend fun getFavorite(language: String): Result<Movie?, DataError.Remote>
    suspend fun saveFavorite(movieId: Long, language: String): Result<Movie, DataError.Remote>
    suspend fun clearFavorite(): EmptyResult<DataError.Remote>
}
