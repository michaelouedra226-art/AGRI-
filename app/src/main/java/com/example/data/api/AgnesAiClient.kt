package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object AgnesAiClient {
    private const val DEFAULT_BASE_URL = "https://apihub.agnes-ai.com/v1/"
    private const val DEFAULT_API_KEY = "sk-opbUSSdj6xQlAeDdQwu79jTDGs34cw5kfH5Zc7DGDi7fub3H"

    val apiKey: String
        get() {
            val key = try {
                BuildConfig.AGNES_API_KEY
            } catch (e: Throwable) {
                ""
            }
            return if (!key.isNullOrBlank() && key != "null") key else DEFAULT_API_KEY
        }

    val baseUrl: String
        get() {
            val url = try {
                BuildConfig.AGNES_API_BASE_URL
            } catch (e: Throwable) {
                ""
            }
            return if (!url.isNullOrBlank() && url != "null") {
                if (url.endsWith("/")) url else "$url/"
            } else DEFAULT_BASE_URL
        }

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val service: AgnesAiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AgnesAiService::class.java)
    }
}
