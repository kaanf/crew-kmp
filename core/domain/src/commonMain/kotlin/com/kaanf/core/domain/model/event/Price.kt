package com.kaanf.core.domain.model.event

data class Price(
    val amount: Long,
    val currency: String
) {
    fun format(): String {
        val major = amount / 100
        return "$major $currency"
    }
}
