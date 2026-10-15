package com.sayon.ai.ai

/**
 * Intelligent Safety Moderation Engine for Sayon AI.
 * Filters prompts for explicit, dangerous, or unauthorized content.
 */
object SafetyModerator {

    private val forbiddenKeywords = listOf(
        "عنف", "إرهاب", "سلاح", "مخدرات", "اختراق", "احتيال", "إباحي", "سرقة", "قتل",
        "violence", "weapon", "drugs", "hack", "scam", "explicit", "nsfw", "illegal", "terrorism"
    )

    fun evaluatePrompt(prompt: String): SafetyResult {
        val normalized = prompt.lowercase().trim()
        val detected = forbiddenKeywords.filter { normalized.contains(it) }

        return if (detected.isNotEmpty()) {
            SafetyResult.Violating(
                reasonAr = "هذا الطلب مخالف لقواعد الاستخدام لتقنية الذكاء الاصطناعي التوليدي.",
                reasonEn = "This request contains content that violates Sayon AI safety policies.",
                detectedCategories = detected
            )
        } else {
            SafetyResult.Safe
        }
    }
}
