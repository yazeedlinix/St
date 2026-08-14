package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.ClassificationResult
import com.example.domain.WorkshopCategory

/**
 * فئة Entity في Room لتمثيل عنصر المخزون في الورشة وحفظه محلياً.
 * تتضمن الحقول الأساسية: المعرف، اسم العنصر، كود SKU، مكان التخزين، والمواصفات الفنية.
 */
@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val sku: String,
    val storageLocation: String,
    val technicalSpecs: String,
    val categoryCode: String = "E-CON",
    val categoryName: String = "مواد مستهلكة",
    val quantity: Int = 1,
    val unit: String = "قطعة",
    val defaultQuantity: String = "1",
    val minQuantityAlert: Int = 5,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    val tableRowFormat: String
        get() = "$sku | $name | $categoryName | $storageLocation | $defaultQuantity"

    fun toClassificationResult(): ClassificationResult {
        return ClassificationResult(
            standardName = name,
            sku = sku,
            category = WorkshopCategory.fromCode(categoryCode),
            storageLocation = storageLocation,
            technicalSpecs = technicalSpecs,
            defaultQuantity = defaultQuantity,
            rawInput = name
        )
    }
}

// الاسم المستعار للتوافق الكامل مع واجهات وقنوات العرض
typealias WorkshopItemEntity = InventoryItem
