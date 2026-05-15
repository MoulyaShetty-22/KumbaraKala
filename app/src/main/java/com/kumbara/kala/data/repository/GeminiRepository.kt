package com.kumbara.kala.data.repository

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.kumbara.kala.BuildConfig
import com.kumbara.kala.data.db.RetrofitClient
import com.kumbara.kala.data.model.GeminiContent
import com.kumbara.kala.data.model.GeminiPart
import com.kumbara.kala.data.model.GeminiRequest
import com.kumbara.kala.data.model.InlineData
import java.io.ByteArrayOutputStream

class GeminiRepository {

    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val TAG = "GeminiRepository"

    // Scale bitmap to max 1024px to avoid 413 Payload Too Large
    private fun scaleBitmap(bitmap: Bitmap, maxSize: Int = 1024): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxSize && height <= maxSize) return bitmap
        val ratio = width.toFloat() / height.toFloat()
        return if (width > height) {
            Bitmap.createScaledBitmap(bitmap, maxSize, (maxSize / ratio).toInt(), true)
        } else {
            Bitmap.createScaledBitmap(bitmap, (maxSize * ratio).toInt(), maxSize, true)
        }
    }

    // Convert bitmap to base64 string
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val scaled = scaleBitmap(bitmap)
        val outputStream = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val bytes = outputStream.toByteArray()
        Log.d(TAG, "Image size after scaling: ${bytes.size / 1024}KB")
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    // Read full error message from Google API response
    private fun parseError(code: Int, errorBody: String?): String {
        return try {
            if (!errorBody.isNullOrBlank()) {
                // Try to extract message from JSON
                val messageStart = errorBody.indexOf("\"message\":")
                if (messageStart != -1) {
                    val start = errorBody.indexOf("\"", messageStart + 10) + 1
                    val end = errorBody.indexOf("\"", start)
                    "API Error $code: ${errorBody.substring(start, end)}"
                } else {
                    "API Error $code: $errorBody"
                }
            } else {
                "API Error $code"
            }
        } catch (e: Exception) {
            "API Error $code"
        }
    }

    suspend fun generateBenefitCard(
        productName: String,
        bitmap: Bitmap,
        language: String = "English"
    ): Result<String> {
        return try {
            val base64Image = bitmapToBase64(bitmap)
            val langInstruction = if (language == "Kannada")
                "Respond in Kannada language." else "Respond in English."

            val prompt = """You are a health and sustainability expert. Analyze this clay product image.
Product Name: $productName
$langInstruction

Generate a benefit card with exactly 3 parts, separated by ||| :
1. HEALTH FACT: One specific health benefit of using clay for this product (mention clay pH regulation, mineral content, or cooling properties)
2. SCIENCE CLAIM: One scientific explanation (mention terracotta properties, natural materials, or traditional wisdom)
3. HERITAGE NOTE: One sentence about Karnataka's Kumbara pottery tradition and this product.

Keep each part to 1-2 sentences. Be specific to the product shown."""

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(
                            GeminiPart(
                                inlineData = InlineData(
                                    mimeType = "image/jpeg",
                                    data = base64Image
                                )
                            ),
                            GeminiPart(text = prompt)
                        )
                    )
                )
            )

            Log.d(TAG, "Sending benefit card request for: $productName")
            val response = RetrofitClient.geminiService.generateContent(apiKey, request)

            if (response.isSuccessful) {
                val body = response.body()
                // Check for API-level error inside successful response
                if (body?.error != null) {
                    val msg = "Gemini error: ${body.error.message}"
                    Log.e(TAG, msg)
                    return Result.failure(Exception(msg))
                }
                val text = body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    Log.d(TAG, "Benefit card generated successfully")
                    Result.success(text)
                } else {
                    Result.failure(Exception("Empty response from Gemini"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = parseError(response.code(), errorBody)
                Log.e(TAG, "Benefit card failed: $errorMsg")
                Log.e(TAG, "Full error body: $errorBody")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Benefit card exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun generateStory(
        productName: String,
        language: String = "English"
    ): Result<String> {
        return try {
            val langInstruction = if (language == "Kannada")
                "Write in Kannada language." else "Write in English."

            val prompt = """$langInstruction
You are a $productName made from Karnataka river clay by a Kumbara artisan.
Write a short first-person narrative (4-5 sentences) from the perspective of this clay object.
Mention: where the clay came from (a river or earth in Karnataka), how you were shaped by skilled hands, how long you were fired, and what purpose you now serve.
Make it warm, poetic, and emotionally resonant. Use "I" as the voice of the clay object."""

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                )
            )

            Log.d(TAG, "Sending story request for: $productName")
            val response = RetrofitClient.geminiService.generateContent(apiKey, request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.error != null) {
                    return Result.failure(Exception("Gemini error: ${body.error.message}"))
                }
                val text = body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) Result.success(text)
                else Result.failure(Exception("Empty response from Gemini"))
            } else {
                val errorMsg = parseError(response.code(), response.errorBody()?.string())
                Log.e(TAG, "Story failed: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Story exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun generateCareGuide(
        productName: String,
        language: String = "English"
    ): Result<String> {
        return try {
            val langInstruction = if (language == "Kannada")
                "Respond in Kannada language." else "Respond in English."

            val prompt = """$langInstruction
You are an expert in traditional clay products. Generate a care guide for: $productName (clay/terracotta product).

Format with these 4 sections:
HOW TO USE: [2-3 specific usage instructions]
HOW TO MAINTAIN: [2-3 cleaning and maintenance tips]
DO'S: [3 things to do]
DON'TS: [3 things to avoid]

Be specific to $productName. Keep it practical."""

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                )
            )

            Log.d(TAG, "Sending care guide request for: $productName")
            val response = RetrofitClient.geminiService.generateContent(apiKey, request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.error != null) {
                    return Result.failure(Exception("Gemini error: ${body.error.message}"))
                }
                val text = body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) Result.success(text)
                else Result.failure(Exception("Empty response from Gemini"))
            } else {
                val errorMsg = parseError(response.code(), response.errorBody()?.string())
                Log.e(TAG, "Care guide failed: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Care guide exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun generateDailyFact(language: String = "English"): Result<String> {
        return try {
            val langInstruction = if (language == "Kannada")
                "Respond in Kannada language." else "Respond in English."

            val prompt = """$langInstruction
Generate one fascinating fact about clay pottery, terracotta, or the Kumbara community of Karnataka.
The fact should be about health benefits, science, history, or environmental impact.
Keep it to 2 sentences. Start with "Did you know?"
Make it engaging and shareable."""

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                )
            )

            val response = RetrofitClient.geminiService.generateContent(apiKey, request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.error != null) {
                    return Result.failure(Exception("Gemini error: ${body.error.message}"))
                }
                val text = body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) Result.success(text)
                else Result.failure(Exception("Empty response"))
            } else {
                val errorMsg = parseError(response.code(), response.errorBody()?.string())
                Log.e(TAG, "Daily fact failed: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Daily fact exception: ${e.message}", e)
            Result.failure(e)
        }
    }
}