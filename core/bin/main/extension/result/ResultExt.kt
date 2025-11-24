@file:Suppress("TooManyFunctions")

package kmp.result

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> runCatchingWithContext(
    context: CoroutineContext,
    block: suspend CoroutineScope.() -> T,
): Result<T> = runCatchingSuspended { withContext(context = context, block = block) }

@Suppress("UseRunCatchingSuspended")
inline fun <R> runCatchingSuspended(block: () -> R): Result<R> {
    return runCatching(block).exceptCancellation()
}

inline fun <reified T : Throwable, R> Result<R>.except(): Result<R> {
    return onFailure { if (it is T) throw it }
}

fun <T> Result<T>.exceptCancellation(): Result<T> {
    return except<CancellationException, T>()
}

inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> {
    return map(transform).flatten()
}

fun <T> Result<Result<T>>.flatten(): Result<T> {
    return fold(
        onSuccess = { it },
        onFailure = { Result.failure(it) },
    )
}

fun <T1, T2, R> combineResult(r1: Result<T1>, r2: Result<T2>, callback: (T1, T2) -> R): Result<R> =
    r1.flatMap { t1 ->
        r2.flatMap { t2 ->
            runCatchingSuspended {
                callback(t1, t2)
            }
        }
    }

inline fun <T1, T2, T3, R> combineResults(
    r1: Result<T1>,
    r2: Result<T2>,
    r3: Result<T3>,
    transform: (T1, T2, T3) -> R
): Result<R> {
    return r1.flatMap { v1 ->
        r2.flatMap { v2 ->
            r3.map { v3 ->
                transform(v1, v2, v3)
            }
        }
    }
}

operator fun Result<Unit>.plus(other: Result<Unit>): Result<Unit> =
    if (this.isSuccess && other.isSuccess) {
        Result.success(Unit)
    } else if (this.isFailure) {
        this
    } else {
        other
    }

@OptIn(ExperimentalContracts::class)
inline fun <T, R> Result<T>.flatMapFlow(
    onSuccess: (value: T) -> Flow<Result<R>>,
): Flow<Result<R>> {
    contract {
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
    }
    return fold(
        onSuccess = onSuccess,
        onFailure = { error ->
            flowOf(Result.failure(error))
        }
    )
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> T.success(): Result<T> = Result.success(this)

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Throwable.failure(): Result<T> = Result.failure(this)

inline fun <T, reified V : Throwable> Result<T>.mapFailure(transform: (V) -> Throwable): Result<T> {
    return when {
        isSuccess -> this
        else -> {
            val exception = exceptionOrNull()!!
            if (exception is V) {
                Result.failure(transform(exception))
            } else {
                Result.failure(exception)
            }
        }
    }
}
