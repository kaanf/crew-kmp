package com.kaanf.core.data.repository

import com.kaanf.core.data.dto.FavoriteMusicDto
import com.kaanf.core.data.dto.MusicSearchPageDto
import com.kaanf.core.data.dto.SaveFavoriteMusicDto
import com.kaanf.core.data.networking.delete
import com.kaanf.core.data.networking.get
import com.kaanf.core.data.networking.mapCatching
import com.kaanf.core.data.networking.put
import com.kaanf.core.domain.model.music.MusicSearchPage
import com.kaanf.core.domain.model.music.MusicTrack
import com.kaanf.core.domain.repository.MusicRepository
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.Result
import io.ktor.client.HttpClient

class MusicRepositoryImpl(private val httpClient: HttpClient) : MusicRepository {
    override suspend fun search(query: String, market: String, offset: Int): Result<MusicSearchPage, DataError.Remote> {
        val normalized = query.trim()
        if (normalized.length !in 2..100 || normalized.any { it.isISOControl() } || offset !in 0..1000 || !validMarket(market)) {
            return Result.Failure(DataError.Remote.BAD_REQUEST)
        }
        return httpClient.get<MusicSearchPageDto>(
            route = "/music/search",
            queryParams = mapOf("q" to normalized, "market" to market.uppercase(), "offset" to offset),
        ).mapCatching { page ->
            require(page.nextOffset == null || page.nextOffset in (offset + 1)..1000)
            MusicSearchPage(page.tracks.map { it.toDomain() }, page.nextOffset)
        }
    }

    override suspend fun getFavorite(market: String): Result<MusicTrack?, DataError.Remote> {
        if (!validMarket(market)) return Result.Failure(DataError.Remote.BAD_REQUEST)
        return httpClient.get<FavoriteMusicDto>(
            route = FAVORITE_ROUTE,
            queryParams = mapOf("market" to market.uppercase()),
        ).mapCatching { it.track?.toDomain() }
    }

    override suspend fun saveFavorite(trackId: String, market: String): Result<MusicTrack, DataError.Remote> {
        if (!trackId.matches(Regex("[A-Za-z0-9]{22}")) || !validMarket(market)) {
            return Result.Failure(DataError.Remote.BAD_REQUEST)
        }
        return httpClient.put<SaveFavoriteMusicDto, FavoriteMusicDto>(
            route = FAVORITE_ROUTE,
            body = SaveFavoriteMusicDto(trackId, market.uppercase()),
        ).mapCatching { requireNotNull(it.track).toDomain() }
    }

    override suspend fun clearFavorite(): EmptyResult<DataError.Remote> = httpClient.delete<Unit>(FAVORITE_ROUTE)

    private fun validMarket(market: String) = market.length == 2 && market.all { it in 'A'..'Z' || it in 'a'..'z' }

    private companion object {
        const val FAVORITE_ROUTE = "/profile/favorite-music"
    }
}
