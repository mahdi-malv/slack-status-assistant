package dk.malv.slack.assistant.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun providesKtorClient(): HttpClient {
        return HttpClient {
            // Configuring HTTP timeout settings
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                socketTimeoutMillis = 14000
            }

            // Configuring JSON serialization/deserialization feature
            install(JsonFeature) {
                serializer = KotlinxSerializer(Json {
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


    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("slack_assistant", Context.MODE_PRIVATE)
    }
}