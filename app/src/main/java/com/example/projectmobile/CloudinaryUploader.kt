package com.example.projectmobile

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import java.util.concurrent.Executors

object CloudinaryUploader {

    private const val CLOUD_NAME = "tq5yutqv"
    private const val UPLOAD_PRESET = "temanstudi_profile"
    private const val MAX_SIZE = 2 * 1024 * 1024

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun upload(
        context: Context,
        uri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val appContext = context.applicationContext

        executor.execute {
            try {
                val resolver = appContext.contentResolver
                val mimeType = resolver.getType(uri).orEmpty()

                val input = resolver.openInputStream(uri)
                    ?: throw IllegalArgumentException("File foto tidak dapat dibaca.")

                val bytes = input.use { it.readBytes() }

                uploadToCloudinary(bytes, mimeType, onSuccess, onFailure)
            } catch (error: Exception) {
                notifyFailure(
                    onFailure,
                    error.message ?: "Gagal membaca foto."
                )
            }
        }
    }

    fun uploadBytes(
        bytes: ByteArray,
        mimeType: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val safeBytes = bytes.copyOf()

        executor.execute {
            uploadToCloudinary(
                safeBytes,
                mimeType,
                onSuccess,
                onFailure
            )
        }
    }

    private fun uploadToCloudinary(
        bytes: ByteArray,
        mimeType: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        var connection: HttpURLConnection? = null

        try {
            if (mimeType !in listOf("image/jpeg", "image/png")) {
                throw IllegalArgumentException(
                    "Foto harus berformat JPG, JPEG, atau PNG."
                )
            }

            if (bytes.isEmpty()) {
                throw IllegalArgumentException("File foto kosong.")
            }

            if (bytes.size > MAX_SIZE) {
                throw IllegalArgumentException("Ukuran foto maksimal 2 MB.")
            }

            val extension = if (mimeType == "image/png") "png" else "jpg"
            val boundary = "----TemanStudi${UUID.randomUUID()}"

            val endpoint =
                "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"

            connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                connectTimeout = 15000
                readTimeout = 30000
                setRequestProperty(
                    "Content-Type",
                    "multipart/form-data; boundary=$boundary"
                )
            }

            connection.outputStream.use { output ->
                fun writeText(value: String) {
                    output.write(value.toByteArray(Charsets.UTF_8))
                }

                writeText("--$boundary\r\n")
                writeText(
                    "Content-Disposition: form-data; name=\"upload_preset\"\r\n\r\n"
                )
                writeText("$UPLOAD_PRESET\r\n")

                writeText("--$boundary\r\n")
                writeText(
                    "Content-Disposition: form-data; name=\"folder\"\r\n\r\n"
                )
                writeText("temanstudi/profile\r\n")

                writeText("--$boundary\r\n")
                writeText(
                    "Content-Disposition: form-data; name=\"file\"; " +
                        "filename=\"profile_${UUID.randomUUID()}.$extension\"\r\n"
                )
                writeText("Content-Type: $mimeType\r\n\r\n")
                output.write(bytes)
                writeText("\r\n--$boundary--\r\n")
            }

            val responseCode = connection.responseCode
            val responseStream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val response = responseStream?.use { stream ->
                BufferedReader(InputStreamReader(stream)).readText()
            }.orEmpty()

            if (responseCode !in 200..299) {
                val message = try {
                    JSONObject(response)
                        .optJSONObject("error")
                        ?.optString("message")
                        ?: "Upload Cloudinary gagal (HTTP $responseCode)."
                } catch (_: Exception) {
                    "Upload Cloudinary gagal (HTTP $responseCode)."
                }

                throw IllegalStateException(message)
            }

            val secureUrl = JSONObject(response).optString("secure_url")

            if (secureUrl.isBlank()) {
                throw IllegalStateException(
                    "URL foto tidak ditemukan dari Cloudinary."
                )
            }

            mainHandler.post {
                onSuccess(secureUrl)
            }

        } catch (error: Exception) {
            notifyFailure(
                onFailure,
                error.message ?: "Gagal mengunggah foto."
            )
        } finally {
            connection?.disconnect()
        }
    }

    private fun notifyFailure(
        callback: (String) -> Unit,
        message: String
    ) {
        mainHandler.post {
            callback(message)
        }
    }
}
