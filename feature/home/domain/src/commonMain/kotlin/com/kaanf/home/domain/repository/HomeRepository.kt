package com.kaanf.home.domain.repository

import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult

interface HomeRepository {
    suspend fun uploadProfilePicture(
        imageBytes: ByteArray,
        mimeType: String,
    ): EmptyResult<DataError.Remote>
}
