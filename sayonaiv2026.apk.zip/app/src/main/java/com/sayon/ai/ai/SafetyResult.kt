package com.sayon.ai.ai

/**
 * Result of prompt moderation evaluation by Sayon AI Safety Moderator.
 */
sealed class SafetyResult {
    object Safe : SafetyResult()
    data class Violating(
        val reasonAr: String = "هذا الطلب مخالف لقواعد الاستخدام والشروط الأمنية للذكاء الاصطناعي.",
        val reasonEn: String = "This request violates Sayon AI safety guidelines and content policies.",
        val detectedCategories: List<String> = emptyList()
    ) : SafetyResult()
}
