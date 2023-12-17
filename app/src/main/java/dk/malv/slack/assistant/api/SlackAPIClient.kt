package dk.malv.slack.assistant.api


import android.util.Log
import dk.malv.slack.assistant.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.features.DefaultRequest
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.observer.ResponseObserver
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * Represents a client for making HTTP requests using Ktor library.
 *
 * This class sets up an HttpClient with various features such as timeout, JSON serialization, logging, response observation,
 * and default request headers.
 */
class SlackAPIClient {
    /**
     * The configured HttpClient instance for making HTTP requests.
     */
    val client = HttpClient {
        // Configuring HTTP timeout settings
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            socketTimeoutMillis = 14000
        }

        // Configuring JSON serialization/deserialization feature
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        // Configuring logging feature
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.v("Logger Ktor =>", message)
                }
            }
            level = LogLevel.ALL
        }

        // Configuring response observation feature
        install(ResponseObserver) {
            onResponse { response ->
                Log.d("HTTP status:", "${response.status.value}")
            }
        }

        // Configuring default request headers
        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header("Authorization", BuildConfig.USER_TOKEN)
        }
    }
}


/**
 * This function makes a POST request using the specified client.
 *
 * @param url The URL to which the POST request will be sent.
 * @param body The data to be sent in the POST request.
 * @return The response data of type [T].
 */
suspend inline fun <reified T> SlackAPIClient.postRequest(
    url: String,
    body: Any
): T = withContext(Dispatchers.IO) {
    client.post(
        urlString = url
    ) {
        this.body = body
    }
}


/**
 * Suspend function to make a GET request using the specified client.
 *
 * @param url the URL from which the GET request will be sent
 * @return the response data of type [T]
 */
suspend inline fun <reified T> SlackAPIClient.getRequest(url: String): T = withContext(Dispatchers.IO) {
    client.get(urlString = url)
}

