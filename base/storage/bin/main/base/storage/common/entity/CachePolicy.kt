package base.storage.common.entity

import kotlin.time.Duration

internal data class CachePolicy(
    private val time: Duration,
) {

    fun isExpired(createdTime: Long): Boolean {
        val currentTime = getTime()
        return currentTime - createdTime > time.inWholeMilliseconds
    }

    fun getTime(): Long {
        return System.currentTimeMillis()
    }
}

internal fun <T> createEntry(entity: T, page: Int): CachedEntry<T> {
    return CachedEntry(entry = entity, createTime = System.currentTimeMillis(), page = page)
}
