package com.kaanf.core.data.mappers

import com.kaanf.core.data.dto.ProfilePictureUploadUrlsResponse
import com.kaanf.core.domain.model.ProfilePictureUploadUrls

fun ProfilePictureUploadUrlsResponse.toDomain(): ProfilePictureUploadUrls {
    return ProfilePictureUploadUrls(
        uploadUrl = uploadUrl,
        publicUrl = publicUrl,
        headers = headers
    )
}
