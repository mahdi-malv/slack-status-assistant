package dk.malv.slack.assistant.api.client


import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


/**
 * Represents a client for making HTTP requests using Ktor library.
 *
 * This class sets up an HttpClient with various features such as timeout, JSON serialization, logging, response observation,
 * and default request headers.
 */
class SlackAPIClient @Inject constructor(
    val client: HttpClient // Understand why you made this client public instead of private val
) {
    /**
     * This function makes a POST request using the specified client.
     *
     * @param url The URL to which the POST request will be sent.
     * @param body The data to be sent in the POST request.
     * @return The response data of type [T].
     */
    suspend inline fun <reified T> postRequest(
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
    suspend inline fun <reified T> getRequest(url: String): T = withContext(Dispatchers.IO) {
        client.get(urlString = url)
    }
}
