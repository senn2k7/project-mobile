package com.example.projectmobile

import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import java.io.IOException

/**
 * Pengelola retry untuk proses Google Login.
 *
 * Maksimal 2 kali retry setelah percobaan pertama.
 * Hanya error yang dinyatakan aman oleh pemanggil
 * yang boleh dicoba kembali.
 */
object GoogleLoginRecovery {

    private const val DEFAULT_MAX_RETRIES = 2
    private const val INITIAL_DELAY_MS = 1_000L
    private const val BACKOFF_MULTIPLIER = 2L

    suspend fun <T> execute(
        maxRetries: Int = DEFAULT_MAX_RETRIES,
        shouldRetry: (Exception) -> Boolean,
        operation: suspend () -> T
    ): T {
        require(maxRetries >= 0) {
            "Jumlah retry tidak boleh negatif."
        }

        var retryCount = 0
        var currentDelay = INITIAL_DELAY_MS

        while (true) {
            try {
                return operation()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                val canRetry =
                    retryCount < maxRetries &&
                    shouldRetry(error)

                if (!canRetry) {
                    throw error
                }

                delay(currentDelay)
                retryCount++
                currentDelay *= BACKOFF_MULTIPLIER
            }
        }
    }

    fun isRetryableNetworkError(error: Throwable): Boolean {
        var current: Throwable? = error

        while (current != null) {
            if (
                current is IOException ||
                current is FirebaseNetworkException
            ) {
                return true
            }

            current = current.cause
        }

        return false
    }
}
