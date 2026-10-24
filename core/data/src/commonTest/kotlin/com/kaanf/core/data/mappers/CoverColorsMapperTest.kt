package com.kaanf.core.data.mappers

import kotlin.test.Test
import kotlin.test.assertEquals

class CoverColorsMapperTest {

    @Test
    fun hexColoursBecomeOpaqueArgbInTheSameOrder() {
        assertEquals(
            listOf(0xFFEDACCA.toInt(), 0xFF312724.toInt(), 0xFFFFFFFF.toInt()),
            listOf("#EDACCA", "#312724", "#ffffff").toArgbColors(),
        )
    }

    @Test
    fun anyMalformedEntryDropsTheWholeListSoRolesNeverShift() {
        assertEquals(emptyList(), listOf("#EDACCA", "#12345", "#312724").toArgbColors())
        assertEquals(emptyList(), listOf("EDACCAFF").toArgbColors())
        assertEquals(emptyList(), listOf("#GGGGGG").toArgbColors())
        assertEquals(emptyList(), listOf("#-12345").toArgbColors())
        assertEquals(emptyList(), emptyList<String>().toArgbColors())
    }
}
