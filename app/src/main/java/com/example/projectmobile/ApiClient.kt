package com.example.snapchatsphlasscreen

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

object ApiClient {

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    interface ApiCallback {
        fun onSuccess(response: JSONObject)
        fun onError(message: String)
    }

    fun postJson(
        urlString: String,
        body: JSONObject,
        callback: ApiCallback
    ) {
        executor.execute {
            var connection: HttpURLConnection? = null

            try {
                connection = URL(urlString)
                    .openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.doOutput = true

                connection.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=UTF-8"
                )

                connection.setRequestProperty(
                    "Accept",
                    "application/json"
                )

                val requestBody = body.toString()
                    .toByteArray(StandardCharsets.UTF_8)

                connection.outputStream.use {
                    it.write(requestBody)
                }

                val statusCode = connection.responseCode

                val stream = if (statusCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

                val responseText = stream
                    ?.bufferedReader(StandardCharsets.UTF_8)
                    ?.use { it.readText() }
                    ?: ""

                val responseJson = JSONObject(responseText)

                mainHandler.post {
                    if (statusCode in 200..299) {
                        callback.onSuccess(responseJson)
                    } else {
                        callback.onError(
                            responseJson.optString(
                                "message",
                                "Permintaan gagal. Kode: $statusCode"
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                mainHandler.post {
                    callback.onError(
                        e.message ?: "Tidak dapat terhubung ke server."
                    )
                }
            } finally {
                connection?.disconnect()
            }
        }
    }
}
