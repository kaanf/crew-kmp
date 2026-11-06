package com.kaanf.core.domain.repository

import com.kaanf.core.domain.model.music.MusicSearchPage
import com.kaanf.core.domain.model.music.MusicTrack
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.Result

/** All calls use the Crew session. Writes require connectivity and are never queued. */
interface MusicRepository {
    suspend fun search(query: String, market: String, offset: Int = 0): Result<MusicSearchPage, DataError.Remote>
    suspend fun getFavorite(market: String): Result<MusicTrack?, DataError.Remote>
    suspend fun saveFavorite(trackId: String, market: String): Result<MusicTrack, DataError.Remote>
    suspend fun clearFavorite(): EmptyResult<DataError.Remote>
}
