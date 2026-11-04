package com.kaanf.core.domain.repository

import com.kaanf.core.domain.model.profileboard.BoardQrRender
import com.kaanf.core.domain.model.profileboard.ProfileBoard
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface ProfileBoardRepository {
    fun observeBoard(): Flow<ProfileBoard?>
    suspend fun refresh(): Result<ProfileBoard, DataError.Remote>
    suspend fun save(board: ProfileBoard): Result<ProfileBoard, DataError.Remote>
    fun observeQrRender(): Flow<BoardQrRender?>

    /** Caches the themed QR state, waiting while the server is still rendering it. */
    suspend fun syncQrRender(): Result<BoardQrRender, DataError.Remote>
}
