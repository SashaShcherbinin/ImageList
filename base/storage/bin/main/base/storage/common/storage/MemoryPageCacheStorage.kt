@file:Suppress("MemberVisibilityCanBePrivate", "OPT_IN_USAGE")

package base.storage.common.storage

import base.storage.common.entity.CachePolicy
import base.storage.common.entity.CachedEntry
import base.storage.common.entity.createEntry
import kmp.result.runCatchingSuspended
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.Result.Companion.success
import kotlin.math.absoluteValue
import kotlin.time.Duration

private const val INCREMENT_PAGE = 1
private const val START_PAGE = 1

/**
 * Automatic caching values from network to memory and database(optional) by key ->value
 * @param maxElements max elements in the memory after adding making clean up for the first element
 * @param cacheTime time after which the element in memory will be considered expired
 * @param network network method request to get data from network
 * */
class MemoryPageCacheStorage<K, E>(
    private val maxElements: Int,
    private val cacheTime: Duration,
    private val network: (suspend (K, E?, Int) -> E),
) {

    private val cachePolicy: CachePolicy = CachePolicy(cacheTime)
    private val updateChannel = Channel<Pair<K, E>>()
    private val cache: MutableMap<K, CachedEntry<E>> = mutableMapOf()

    private val mutexPool: MutableMap<Int, Mutex> = mutableMapOf()

    private fun getMutexForKey(key: K): Mutex {
        val index = (key.hashCode().absoluteValue % maxElements)
        return mutexPool.getOrPut(index) { Mutex() }
    }

    fun get(key: K): Flow<Result<E>> {
        return channelFlow {
            launch {
                updateChannel.receiveAsFlow()
                    .filter { it.first == key }
                    .map { success(it.second) }
                    .collect { send(it) }
            }
            launch {
                withContext(NonCancellable) {
                    val mutex = getMutexForKey(key)
                    mutex.withLock {
                        val cachedEntry: CachedEntry<E>? = cache[key]
                        if (cachedEntry != null &&
                            cachePolicy.isExpired(cachedEntry.createTime).not()
                        ) {
                            send(success(cachedEntry.entry))
                        } else {
                            val result = fetchData(key, null, START_PAGE)
                            send(result)
                        }
                    }
                }
            }
        }
    }

    private fun addNewValue(key: K, entry: E, page: Int) {
        if (cache.size > maxElements) {
            val oldValueKey = cache.minBy {
                it.value.createTime
            }.key
            cache.remove(oldValueKey)
        }
        cache[key] = createEntry(entry, page)
    }

    private suspend fun fetchData(key: K, value: E?, page: Int): Result<E> = runCatchingSuspended {
        val value = network(key, value, page)
        addNewValue(key, value, page)
        updateChannel.trySend(key to value)
        value
    }

    suspend fun fetchNext(key: K): Result<Unit> {
        return withContext(NonCancellable) {
            val mutex = getMutexForKey(key)
            mutex.withLock {
                val cachedEntry: CachedEntry<E>? = cache[key]
                val nextPage = if (cachedEntry != null) {
                    cachedEntry.page + INCREMENT_PAGE
                } else {
                    START_PAGE
                }
                val result = fetchData(key, cachedEntry?.entry, nextPage)
                result.map { }
            }
        }
    }

    suspend fun refresh(key: K): Result<Unit> {
        return fetchData(key, null, START_PAGE).map { }
    }
}
