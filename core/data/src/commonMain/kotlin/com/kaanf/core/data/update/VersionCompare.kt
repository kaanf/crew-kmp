package com.kaanf.core.data.update

internal fun String.isNewerVersionThan(other: String): Boolean {
    val mine = split('.').map { it.toIntOrNull() ?: 0 }
    val theirs = other.split('.').map { it.toIntOrNull() ?: 0 }
    for (i in 0 until maxOf(mine.size, theirs.size)) {
        val diff = mine.getOrElse(i) { 0 } - theirs.getOrElse(i) { 0 }
        if (diff != 0) return diff > 0
    }
    return false
}
