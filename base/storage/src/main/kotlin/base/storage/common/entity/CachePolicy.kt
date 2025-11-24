package base.storage.common.entity

import kotlin.time.Duration

interface CachePolicy {
    fun getTime(): Long
    fun isExpired(createdTime: Long): Boolean
}

internal class TimedCachePolicy(private val time: Duration) : CachePolicy {
    override fun getTime(): Long = System.currentTimeMillis()
    override fun isExpired(createdTime: Long): Boolean = getTime() - createdTime > time.inWholeMilliseconds
}

internal fun <T> createEntry(entity: T, page: Int): CachedEntry<T> {
    return CachedEntry(entry = entity, createTime = System.currentTimeMillis(), page = page)
}
