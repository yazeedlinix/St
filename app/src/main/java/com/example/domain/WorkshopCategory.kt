package com.example.domain

enum class WorkshopCategory(
    val code: String,
    val prefix: String,
    val arabicName: String,
    val zoneName: String,
    val defaultLocationPrefix: String,
    val description: String,
    val defaultUnit: String
) {
    STEEL_FORGING(
        code = "A-STL",
        prefix = "A-STL",
        arabicName = "مواد الحدادة",
        zoneName = "المنطقة A (عنبر المقاطع والحديد)",
        defaultLocationPrefix = "المنطقة A - حامل كمرات",
        description = "حديد شكلي، مواسير، أردواز، خردة، كمرات، زوايا، خوص، ألواح صاج",
        defaultUnit = "متر"
    ),
    PLUMBING(
        code = "B-PLV",
        prefix = "B-PLV",
        arabicName = "مستلزمات السباكة",
        zoneName = "المنطقة B (أرفف السباكة ومحابس السوائل)",
        defaultLocationPrefix = "المنطقة B - رف محابس",
        description = "أكواع، محابس، مواسير بلاستيك/PPR/نحاس، لوازم ربط، جلب، نبل، فلانشات",
        defaultUnit = "قطعة"
    ),
    TOOLS(
        code = "C-TLS",
        prefix = "C-TLS",
        arabicName = "أدوات العمل اليدوية والصغيرة",
        zoneName = "المنطقة C (دولاب العدّة وألواح التعليق)",
        defaultLocationPrefix = "المنطقة C - دولاب عدد",
        description = "مفكات، زنارج، أدوات قياس، صواريخ جلخ، شواكيش، بنس، مفاتيح ربط",
        defaultUnit = "قطعة"
    ),
    WELDING(
        code = "D-WLD",
        prefix = "D-WLD",
        arabicName = "ماكينات اللحام ومستهلكاتها",
        zoneName = "المنطقة D (منطقة ومستودع اللحام)",
        defaultLocationPrefix = "المنطقة D - مستودع لحام",
        description = "أسلاك لحام، أسطوان غاز، بكرات تزويد، كابلات، بنس لحام، شعلات TIG/MIG",
        defaultUnit = "عبوة"
    ),
    CONSUMABLES(
        code = "E-CNS",
        prefix = "E-CNS",
        arabicName = "المواد الاستهلاكية والعدّة",
        zoneName = "المنطقة E (أدراج وسلال القطع الصغيرة)",
        defaultLocationPrefix = "المنطقة E - درج مسامير",
        description = "مسامير، براغي، صواميل، وردات، أقراص قطع وجلخ، دهانات، مواد عزل وتثبيت",
        defaultUnit = "علبة"
    );

    companion object {
        fun fromCode(code: String): WorkshopCategory {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) || code.startsWith(it.prefix) }
                ?: CONSUMABLES
        }
    }
}
