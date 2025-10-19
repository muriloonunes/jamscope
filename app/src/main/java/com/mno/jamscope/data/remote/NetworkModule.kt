package com.mno.jamscope.data.remote

import com.mno.jamscope.data.remote.api.LastFmServiceApi
import com.mno.jamscope.data.remote.api.LastFmServiceApiImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        json: Json,
    ): HttpClient {
        return HttpClient {
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "ws.audioscrobbler.com"
                    encodedPath = "/2.0/"
                }

                url.parameters.append("format", "json")
            }
            install(ContentNegotiation) {
                json(json)
            }
        }
    }

    @Provides
    @Singleton
    fun provideLastFmServiceApi(
        client: HttpClient,
    ): LastFmServiceApi {
        return LastFmServiceApiImpl(client, provideJson())
    }
}
