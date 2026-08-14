package com.example.domain

data class ClassificationResult(
    val standardName: String,
    val sku: String,
    val category: WorkshopCategory,
    val storageLocation: String,
    val technicalSpecs: String,
    val defaultQuantity: String = "1",
    val clarifyingQuestion: String? = null,
    val rawInput: String = ""
) {
    val tableRowFormat: String
        get() = "$sku | $standardName | ${category.arabicName} | $storageLocation | $defaultQuantity"

    val fullFormattedOutput: String
        get() = buildString {
            appendLine("1. اسم العنصر: $standardName")
            appendLine("2. التصنيف والكود (SKU): $sku")
            appendLine("3. منطقة ومكان التخزين: $storageLocation")
            appendLine("4. المواصفات الفنية: $technicalSpecs")
            append("5. تنسيق صف جدول البيانات (جاهز للنسخ): $tableRowFormat")
            if (!clarifyingQuestion.isNullOrBlank()) {
                append("\n\n⚠️ سؤال توضيحي مقترح: $clarifyingQuestion")
            }
        }
}
