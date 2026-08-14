package com.example.domain

import com.example.BuildConfig
import com.example.data.remote.GeminiApiService
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenerationConfig
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import com.example.data.remote.ParsedGeminiItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.regex.Pattern

class WorkshopClassifierEngine(
    private val geminiService: GeminiApiService = GeminiApiService.create()
) {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val parsedItemAdapter = moshi.adapter(ParsedGeminiItem::class.java)

    /**
     * Classifies a workshop floor item input into standardized format with SKU and zone.
     * Uses Gemini API if configured & connected, or fast offline rule-based engine.
     */
    suspend fun classifyItem(
        input: String,
        nextItemNumber: Int = 1
    ): ClassificationResult = withContext(Dispatchers.IO) {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return@withContext defaultFallback(input, nextItemNumber)
        }

        // Try Gemini API if API key is present and not the placeholder
        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasValidApiKey = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        if (hasValidApiKey) {
            try {
                val geminiResult = callGeminiClassifier(trimmed, apiKey, nextItemNumber)
                if (geminiResult != null) {
                    return@withContext geminiResult
                }
            } catch (e: Exception) {
                // Fallback to local rule engine silently
            }
        }

        // Local Rule-Based NLP Classifier Engine (Fast, 100% Offline)
        return@withContext classifyLocally(trimmed, nextItemNumber)
    }

    private suspend fun callGeminiClassifier(
        input: String,
        apiKey: String,
        itemNumber: Int
    ): ClassificationResult? {
        val systemPrompt = """
            أنت مساعد ذكي ومتخصص في إدارة المخزون والأتمتة لورشة تصنيع وتصنيع صناعي تضم مواد حدادة، سباكة، أدوات عمل، وماكينات لحام ومستهلكات.
            مهمتك هي استقبال المدخلات اليدوية من أرضية الورشة وتوليد كائن JSON بالهيكل التالي:
            {
              "standardName": "اسم العنصر القياسي الاحترافي باللغة العربية",
              "skuPrefix": "A-STL أو B-PLV أو C-TLS أو D-WLD أو E-CNS",
              "storageLocation": "المنطقة مع الرف أو الصندوق المحدد بدقة",
              "technicalSpecs": "المواصفات الفنية المستخرجة بدقة (المقاس، الخامة، القدرة، النوع)",
              "defaultQuantity": "الكمية الافتراضية مثل 1 قطعة أو 1 علبة",
              "clarifyingQuestion": "سؤال توضيحي واحد مباشر إذا كان الوصف غامضاً أو ينقصه المقاس أو الخامة، وإلا اتركه null"
            }
            
            التصنيفات:
            - A-STL: مواد الحدادة (حديد شكلي، مواسير، أردواز، خردة، كمرات، زوايا، خوص، صاج) -> المنطقة A
            - B-PLV: مستلزمات السباكة (أكواع، محابس، مواسير بلاستيك/نحاس، جلب، نبل، فلانشات) -> المنطقة B
            - C-TLS: أدوات العمل اليدوية والصغيرة (مفكات، زنارج، أدوات قياس، صواريخ، شواكيش، بنس) -> المنطقة C
            - D-WLD: ماكينات اللحام ومستهلكاتها (أسلاك لحام، أسطوان غاز، بكرات تزويد، كابلات، بنس لحام) -> المنطقة D
            - E-CNS: المواد الاستهلاكية والعدّة (مسامير، براغي، صواميل، وردات، أقراص قطع، دهانات، عزل) -> المنطقة E
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = "عنصر من الورشة: $input"))
                )
            ),
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = systemPrompt))
            ),
            generationConfig = GeminiGenerationConfig(
                temperature = 0.1f,
                responseMimeType = "application/json"
            )
        )

        val response = geminiService.generateContent(apiKey, request)
        val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: return null

        val parsed = parsedItemAdapter.fromJson(responseText) ?: return null
        val category = WorkshopCategory.fromCode(parsed.skuPrefix ?: "E-CNS")
        val formattedNumber = String.format(Locale.US, "%03d", itemNumber)
        val sku = "${category.prefix}-$formattedNumber"

        return ClassificationResult(
            standardName = parsed.standardName ?: input,
            sku = sku,
            category = category,
            storageLocation = parsed.storageLocation ?: "${category.defaultLocationPrefix} R-01",
            technicalSpecs = parsed.technicalSpecs ?: "مطابق للمواصفات القياسية للورشة",
            defaultQuantity = parsed.defaultQuantity ?: "1 ${category.defaultUnit}",
            clarifyingQuestion = parsed.clarifyingQuestion,
            rawInput = input
        )
    }

    /**
     * Highly accurate local rule engine for workshop industrial terms.
     */
    fun classifyLocally(input: String, itemNumber: Int): ClassificationResult {
        val lower = input.lowercase(Locale.ROOT)
        val formattedNumber = String.format(Locale.US, "%03d", itemNumber)

        val category = detectCategory(lower)
        val sku = "${category.prefix}-$formattedNumber"
        val specs = extractTechnicalSpecs(input, category)
        val location = determineStorageLocation(input, category, formattedNumber)
        val standardName = standardizeName(input, category)
        val defaultQty = determineDefaultQuantity(input, category)
        val clarifyingQuestion = generateClarifyingQuestionIfNeeded(input, category, specs)

        return ClassificationResult(
            standardName = standardName,
            sku = sku,
            category = category,
            storageLocation = location,
            technicalSpecs = specs,
            defaultQuantity = defaultQty,
            clarifyingQuestion = clarifyingQuestion,
            rawInput = input
        )
    }

    private fun detectCategory(text: String): WorkshopCategory {
        // A-STL: Steel / Iron / Structural / Raw metal materials
        val steelKeywords = listOf(
            "حديد", "كمر", "كمرة", "زاوية", "زوايا", "خوصة", "خوص", "صاج", "لوح صاج",
            "سيخ", "أسياخ", "مجرى", "upn", "ipe", "hea", "heb", "تيوب", "مربع مفرغ",
            "مستطيل مفرغ", "أردواز", "خردة", "صلب", "قضيب", "بلتة", "بليتة", "ماسورة حديد", "أنابيب حديد"
        )
        // B-PLV: Plumbing & valves & fittings
        val plumbingKeywords = listOf(
            "سباكة", "محبس", "صمام", "كوع", "جلبة", "نبل", "تي", "تيه", "بوش", "فلانشة",
            "ماسورة بلاستيك", "مواسير بلاستيك", "ppr", "pvc", "upvc", "نحاس", "تغذية",
            "محابس", "أكواع", "خرطوم هيدروليك", "بلية محبس", "رداد", "شيك بلف", "وصلة مرنة"
        )
        // C-TLS: Tools & measuring & power tools
        val toolsKeywords = listOf(
            "صاروخ", "شنيور", "دريل", "هلتي", "مفك", "مفكات", "شاكوش", "مطرقة", "بنسة",
            "زرادية", "مفتاح", "مفاتيح", "كليبر", "قدمة ورنية", "ميكرومتر", "شريط قياس",
            "متر قياس", "زنارج", "زرجينة", "منجلة", "مبرد", "مقص صاج", "أجنة", "سنبك",
            "ميزان مياه", "زاوية قائمة", "طقم لقم", "سيستم"
        )
        // D-WLD: Welding machines & consumables
        val weldingKeywords = listOf(
            "لحام", "ماكينة لحام", "ميج", "تيج", "mig", "tig", "mma", "سلك لحام", "أسلاك لحام",
            "أرجون", "argon", "co2", "أكسجين", "أسيتيلين", "بنسة لحام", "بنسة ارضي",
            "شعلة لحام", "تورج", "توربين", "فونيات لحام", "فونية", "بكرة سلك لحام", "e6013",
            "e7018", "قناع لحام", "ماسك لحام", "زجاجة لحام"
        )
        // E-CNS: Consumables & Fasteners
        val consumablesKeywords = listOf(
            "مسمار", "مسامير", "برغي", "براغي", "صامولة", "صواميل", "وردة", "وردات",
            "فيشر", "أنكر", "أوميجا", "قرص قطع", "أقراص قطع", "قرص جلخ", "حجر جلخ",
            "قرص صنفرة", "صنفرة", "دهان", "بوية", "برايمر", "سيليكون", "معجون", "تفلون",
            "شيكارتون", "شريط لحام", "عازل", "غراء", "أقفال", "ريشة شنيور", "بنطة"
        )

        // Check for specific matches in priority order
        if (weldingKeywords.any { text.contains(it) }) return WorkshopCategory.WELDING
        if (consumablesKeywords.any { text.contains(it) }) return WorkshopCategory.CONSUMABLES
        if (toolsKeywords.any { text.contains(it) }) return WorkshopCategory.TOOLS
        if (plumbingKeywords.any { text.contains(it) }) return WorkshopCategory.PLUMBING
        if (steelKeywords.any { text.contains(it) }) return WorkshopCategory.STEEL_FORGING

        return WorkshopCategory.CONSUMABLES
    }

    private fun standardizeName(input: String, category: WorkshopCategory): String {
        var clean = input.replace(Regex("[|,\\[\\]]"), " ").trim()
        clean = clean.replace(Regex("\\s+"), " ")

        // If it starts with simple words, capitalize/formalize in Arabic
        return clean.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }

    private fun determineStorageLocation(input: String, category: WorkshopCategory, formattedNumber: String): String {
        val lower = input.lowercase(Locale.ROOT)
        return when (category) {
            WorkshopCategory.STEEL_FORGING -> {
                when {
                    lower.contains("كمر") || lower.contains("upn") || lower.contains("ipe") -> "المنطقة A - حامل الكمرات R-0${(formattedNumber.toIntOrNull() ?: 1) % 4 + 1}"
                    lower.contains("ماسورة") || lower.contains("تيوب") -> "المنطقة A - رف الأنابيب P-0${(formattedNumber.toIntOrNull() ?: 1) % 3 + 1}"
                    lower.contains("صاج") || lower.contains("لوح") -> "المنطقة A - طبلية الألواح S-01"
                    lower.contains("خردة") || lower.contains("أردواز") -> "المنطقة A - عنبر تجميع الخردة B-01"
                    else -> "المنطقة A - حامل مقاطع الحديد A-0${(formattedNumber.toIntOrNull() ?: 1) % 5 + 1}"
                }
            }
            WorkshopCategory.PLUMBING -> {
                when {
                    lower.contains("محبس") || lower.contains("صمام") -> "المنطقة B - رف الصمامات V-0${(formattedNumber.toIntOrNull() ?: 1) % 5 + 1}"
                    lower.contains("كوع") || lower.contains("تي") || lower.contains("جلبة") -> "المنطقة B - صندوق الوصلات F-0${(formattedNumber.toIntOrNull() ?: 1) % 8 + 1}"
                    lower.contains("نحاس") -> "المنطقة B - درج الوصلات النحاسية B-0${(formattedNumber.toIntOrNull() ?: 1) % 3 + 1}"
                    else -> "المنطقة B - أرفف مستلزمات السباكة L-0${(formattedNumber.toIntOrNull() ?: 1) % 4 + 1}"
                }
            }
            WorkshopCategory.TOOLS -> {
                when {
                    lower.contains("صاروخ") || lower.contains("شنيور") || lower.contains("هلتي") -> "المنطقة C - دولاب الصواريخ والعدد الكهربائية E-0${(formattedNumber.toIntOrNull() ?: 1) % 3 + 1}"
                    lower.contains("قياس") || lower.contains("كليبر") || lower.contains("ميكرومتر") -> "المنطقة C - دولاب أدوات القياس الدقيقة M-01"
                    lower.contains("مفتاح") || lower.contains("مفك") || lower.contains("شاكوش") -> "المنطقة C - لوح التعليق الجداري W-0${(formattedNumber.toIntOrNull() ?: 1) % 4 + 1}"
                    else -> "المنطقة C - دولاب العدد اليدوية T-0${(formattedNumber.toIntOrNull() ?: 1) % 4 + 1}"
                }
            }
            WorkshopCategory.WELDING -> {
                when {
                    lower.contains("سلك") || lower.contains("بكرة") -> "المنطقة D - فرن وأرفف أسلاك اللحام W-0${(formattedNumber.toIntOrNull() ?: 1) % 5 + 1}"
                    lower.contains("غاز") || lower.contains("أسطوان") || lower.contains("ارجون") -> "المنطقة D - مخزن أسطوانات الغاز الصناعي G-01"
                    lower.contains("ماكينة") -> "المنطقة D - رصيف ماكينات اللحام M-0${(formattedNumber.toIntOrNull() ?: 1) % 3 + 1}"
                    else -> "المنطقة D - رف ملحقات ومستهلكات اللحام A-0${(formattedNumber.toIntOrNull() ?: 1) % 4 + 1}"
                }
            }
            WorkshopCategory.CONSUMABLES -> {
                when {
                    lower.contains("مسمار") || lower.contains("صامولة") || lower.contains("برغي") -> "المنطقة E - درج المسامير والصواميل N-0${(formattedNumber.toIntOrNull() ?: 1) % 9 + 1}"
                    lower.contains("قرص") || lower.contains("صنفرة") || lower.contains("حجر") -> "المنطقة E - رف أقراص القطع والجلخ D-0${(formattedNumber.toIntOrNull() ?: 1) % 4 + 1}"
                    lower.contains("دهان") || lower.contains("سيليكون") || lower.contains("عازل") -> "المنطقة E - خزانة الكيماويات والمواد C-0${(formattedNumber.toIntOrNull() ?: 1) % 3 + 1}"
                    else -> "المنطقة E - سلال ومستودع المستهلكات E-0${(formattedNumber.toIntOrNull() ?: 1) % 5 + 1}"
                }
            }
        }
    }

    private fun extractTechnicalSpecs(input: String, category: WorkshopCategory): String {
        val extracted = mutableListOf<String>()

        // Detect dimensions like (12 مم، 2 بوصة، 70*50، M12، 220V، 250A، إلخ)
        val dimensionRegex = Regex("(\\d+(\\.\\d+)?\\s*(مم|سم|متر|م|بوصة|انش|\"|kg|كجم|طن|واط|w|أمبير|a|v|فولت|بار|bar))", RegexOption.IGNORE_CASE)
        val matches = dimensionRegex.findAll(input)
        for (match in matches) {
            extracted.add(match.value.trim())
        }

        // Detect bolt codes (M8, M10, M12, M16, 8.8, 10.9)
        val boltRegex = Regex("(M\\d+|8\\.8|10\\.9|12\\.9|PN\\d+|SCH\\d+|UPN\\s*\\d+|IPE\\s*\\d+|E6013|E7018|ER70S-6)", RegexOption.IGNORE_CASE)
        val boltMatches = boltRegex.findAll(input)
        for (match in boltMatches) {
            extracted.add(match.value.trim())
        }

        if (extracted.isNotEmpty()) {
            val dimensionsStr = extracted.distinct().joinToString("، ")
            return when (category) {
                WorkshopCategory.STEEL_FORGING -> "مقاس/أبعاد: $dimensionsStr، صلب صناعي قياسي"
                WorkshopCategory.PLUMBING -> "قطر/سن: $dimensionsStr، مواصفات سباكة صناعية"
                WorkshopCategory.TOOLS -> "مواصفات تشغيلية: $dimensionsStr، أدوات تصنيع مصلدة"
                WorkshopCategory.WELDING -> "مواصفات تقنية: $dimensionsStr، مطابقة لمعايير AWS اللحام"
                WorkshopCategory.CONSUMABLES -> "المقاس والخامة: $dimensionsStr، جودة صناعية عالية"
            }
        }

        return when (category) {
            WorkshopCategory.STEEL_FORGING -> "خامة حديد وصلب إنشائي، بحاجة لتأكيد الأبعاد والأطوال الدقيقة"
            WorkshopCategory.PLUMBING -> "مستلزمات سباكة وضغط، سن ومقاس قياسي"
            WorkshopCategory.TOOLS -> "أدوات ميكانيكية وورش صناعية عالية الكفاءة"
            WorkshopCategory.WELDING -> "معدات ومستهلكات لحام قياسية للورشة"
            WorkshopCategory.CONSUMABLES -> "مستهلكات وربط صناعي قياسي"
        }
    }

    private fun determineDefaultQuantity(input: String, category: WorkshopCategory): String {
        val qtyRegex = Regex("(\\d+)\\s*(قطعة|حبة|لوح|ماسورة|علبة|طقم|بكرة|شيكارة|عبوة|متر|كجم|طن)")
        val match = qtyRegex.find(input)
        if (match != null) {
            return match.value
        }
        return when (category) {
            WorkshopCategory.STEEL_FORGING -> "1 ماسورة/لوح"
            WorkshopCategory.PLUMBING -> "1 قطعة"
            WorkshopCategory.TOOLS -> "1 جهاز/طقم"
            WorkshopCategory.WELDING -> "1 عبوة/بكرة"
            WorkshopCategory.CONSUMABLES -> "10 قطع"
        }
    }

    private fun generateClarifyingQuestionIfNeeded(
        input: String,
        category: WorkshopCategory,
        specs: String
    ): String? {
        val lower = input.lowercase(Locale.ROOT)
        val hasNumbers = input.any { it.isDigit() }

        // If very brief or missing dimensions, ask focused single question
        if (!hasNumbers) {
            return when (category) {
                WorkshopCategory.STEEL_FORGING -> "ما هو المقاس المطلوب (مثل: القطر، سمك الصاج، أو أبعاد الكمر بالملم)؟"
                WorkshopCategory.PLUMBING -> "يرجى تحديد قطر المحبس أو الوصلة ونوع المادة (مثل: 1 بوصة نحاس أو بلاستيك PPR)؟"
                WorkshopCategory.TOOLS -> "ما هي القدرة أو المقاس المطلوب للأداة (مثل: مقاس القرص 7 بوصة أو قدرة الواط)؟"
                WorkshopCategory.WELDING -> "ما هو قطر السلك ونوع رمز اللحام (مثل: E6013 مقاس 3.2 مم أو سلك ميج 1 مم)؟"
                WorkshopCategory.CONSUMABLES -> "ما هو مقاس المسمار/القرص المطلوب وطوله (مثل: مسمار M10*50 مم أو قرص 9 بوصة)؟"
            }
        }

        // If steel without length/thickness
        if (category == WorkshopCategory.STEEL_FORGING && !lower.contains("مم") && !lower.contains("بوصة")) {
            return "يرجى توضيح سمك الخامة أو القطر الدقيق بالملم لضبط كود التخزين."
        }

        return null
    }

    private fun defaultFallback(input: String, nextItemNumber: Int): ClassificationResult {
        val formattedNumber = String.format(Locale.US, "%03d", nextItemNumber)
        return ClassificationResult(
            standardName = if (input.isBlank()) "عنصر ورشة غير محدد" else input,
            sku = "E-CNS-$formattedNumber",
            category = WorkshopCategory.CONSUMABLES,
            storageLocation = "المنطقة E - رف الاستقبال R-01",
            technicalSpecs = "مواصفات قيد التحديد اليدوي",
            defaultQuantity = "1",
            clarifyingQuestion = "يرجى كتابة اسم أو وصف العنصر مع المقاس لتحديد مكانه وكوده بدقة.",
            rawInput = input
        )
    }
}
