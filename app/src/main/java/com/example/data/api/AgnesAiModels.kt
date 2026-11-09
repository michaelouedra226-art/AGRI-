package com.example.data.api

import com.squareup.moshi.Json

data class ChatCompletionRequest(
    @param:Json(name = "model") val model: String = "agnes-3.0-flash",
    @param:Json(name = "messages") val messages: List<AgnesMessage>,
    @param:Json(name = "temperature") val temperature: Double = 0.7,
    @param:Json(name = "max_tokens") val maxTokens: Int = 1500
)

data class AgnesMessage(
    @param:Json(name = "role") val role: String,
    @param:Json(name = "content") val content: String
)

data class ChatCompletionResponse(
    @param:Json(name = "id") val id: String?,
    @param:Json(name = "choices") val choices: List<ChatChoice>?,
    @param:Json(name = "error") val error: AgnesError? = null
)

data class ChatChoice(
    @param:Json(name = "index") val index: Int?,
    @param:Json(name = "message") val message: AgnesMessage?,
    @param:Json(name = "finish_reason") val finishReason: String?
)

data class AgnesError(
    @param:Json(name = "code") val code: String?,
    @param:Json(name = "message") val message: String?,
    @param:Json(name = "type") val type: String?
)
