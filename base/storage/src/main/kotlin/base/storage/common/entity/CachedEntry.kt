package base.storage.common.entity

internal data class CachedEntry<E>(
    val entry: E,
    val createTime: Long,
    val page: Int,
)
