package com.kaanf.core.data.networking

import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import kotlinx.coroutines.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MapCatchingTest {
    @Test
    fun malformedDomainDataBecomesSerializationFailure() {
        val result: Result<String, DataError.Remote> = Result.Success("invalid")
        assertEquals(Result.Failure(DataError.Remote.SERIALIZATION), result.mapCatching { kotlin.time.Instant.parse(it) })
    }

    @Test
    fun coroutineCancellationIsNotConvertedToDataFailure() {
        val result: Result<Int, DataError.Remote> = Result.Success(1)
        assertFailsWith<CancellationException> {
            result.mapCatching { throw CancellationException("fixture cancellation") }
        }
    }
}
