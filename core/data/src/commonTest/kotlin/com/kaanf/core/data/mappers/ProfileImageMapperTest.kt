package com.kaanf.core.data.mappers

import kotlin.test.Test
import kotlin.test.assertEquals

class ProfileImageMapperTest {
    @Test
    fun `to profile image url keeps full url unchanged`() {
        val imageUrl = "https://ads.kaanf.com/profile/profile1.png"

        val mappedUrl = imageUrl.toProfileImageUrl()

        assertEquals(imageUrl, mappedUrl)
    }

    @Test
    fun `to profile image url expands suffix to full url`() {
        val mappedUrl = "profile1".toProfileImageUrl()

        assertEquals(
            "https://ads.kaanf.com/profile/profile1.png",
            mappedUrl,
        )
    }

    @Test
    fun `to profile image suffix extracts suffix from full url`() {
        val imageUrl = "https://ads.kaanf.com/profile/profile1.png"

        val mappedSuffix = imageUrl.toProfileImageSuffix()

        assertEquals("profile1", mappedSuffix)
    }

    @Test
    fun `to profile image suffix keeps suffix unchanged`() {
        val mappedSuffix = "profile1".toProfileImageSuffix()

        assertEquals("profile1", mappedSuffix)
    }
}
