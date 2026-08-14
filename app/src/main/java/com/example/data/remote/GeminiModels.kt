package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>,
    @Json(name = "role") val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    @Json(name = "temperature") val temperature: Float = 0.2f,
    @Json(name = "responseMimeType") val responseMimeType: String = "application/json"
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class ParsedGeminiItem(
    @Json(name = "standardName") val standardName: String? = null,
    @Json(name = "skuPrefix") val skuPrefix: String? = null,
    @Json(name = "storageLocation") val storageLocation: String? = null,
    @Json(name = "technicalSpecs") val technicalSpecs: String? = null,
    @Json(name = "defaultQuantity") val defaultQuantity: String? = null,
    @Json(name = "clarifyingQuestion") val clarifyingQuestion: String? = null
)
