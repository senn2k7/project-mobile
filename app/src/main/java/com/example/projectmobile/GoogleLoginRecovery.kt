package com.example.projectmobile

import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import java.io.IOException

object GoogleLoginRecovery {

    private const val MAX_RETRIES = 2
    private const val INITIAL_DELAY_MS = 1_000L

    suspend fun <T> execute(
        operation: suspend () -> T,
        shouldRetry: (Throwable) -> Boolean = ::isRetryableNetworkError
    ): T {
        var retryCount = 0

        while (true) {
            try {
                return operation()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                if (retryCount >= MAX_RETRIES || !shouldRetry(error)) {
                    throw error
                }

                delay(INITIAL_DELAY_MS * (1L shl retryCount))
                retryCount++
            }
        }
    }

    fun isRetryableNetworkError(error: Throwable): Boolean {
        var current: Throwable? = error

        while (current != null) {
            if (current is FirebaseNetworkException || current is IOException) {
                return true
            }

            current = current.cause
        }

        return false
    }
}
