package com.example.data.local

import com.example.domain.ClassificationResult
import com.example.domain.WorkshopCategory
import com.example.domain.WorkshopClassifierEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class WorkshopRepository(
    private val itemDao: WorkshopItemDao,
    private val classifierEngine: WorkshopClassifierEngine = WorkshopClassifierEngine()
) {
    val allItems: Flow<List<WorkshopItemEntity>> = itemDao.getAllItems()

    fun getItemsByCategory(categoryCode: String): Flow<List<WorkshopItemEntity>> {
        return if (categoryCode == "ALL") {
            itemDao.getAllItems()
        } else {
            itemDao.getItemsByCategory(categoryCode)
        }
    }

    fun searchItems(query: String): Flow<List<WorkshopItemEntity>> {
        return itemDao.searchItems(query.trim())
    }

    suspend fun classifyRawFloorInput(input: String): ClassificationResult {
        // Calculate the next item number based on existing count
        val totalCount = itemDao.getTotalCount()
        val nextNumber = totalCount + 1
        return classifierEngine.classifyItem(input, nextNumber)
    }

    suspend fun insertItem(item: WorkshopItemEntity): Long {
        return itemDao.insertItem(item)
    }

    suspend fun insertFromClassification(
        classification: ClassificationResult,
        quantity: Int = 1,
        unit: String? = null,
        notes: String = ""
    ): Long {
        val finalUnit = unit ?: classification.category.defaultUnit
        val entity = WorkshopItemEntity(
            sku = classification.sku,
            name = classification.standardName,
            categoryCode = classification.category.code,
            categoryName = classification.category.arabicName,
            storageLocation = classification.storageLocation,
            technicalSpecs = classification.technicalSpecs,
            quantity = quantity,
            unit = finalUnit,
            defaultQuantity = classification.defaultQuantity,
            notes = notes
        )
        return itemDao.insertItem(entity)
    }

    suspend fun updateQuantity(id: Long, newQuantity: Int) {
        val clamped = if (newQuantity < 0) 0 else newQuantity
        itemDao.updateQuantity(id, clamped)
    }

    suspend fun updateItem(item: WorkshopItemEntity) {
        itemDao.updateItem(item)
    }

    suspend fun deleteItem(id: Long) {
        itemDao.deleteById(id)
    }

    suspend fun generateExportTableText(): String {
        val items = allItems.first()
        return buildString {
            appendLine("الكود (SKU)\tاسم العنصر القياسي\tالتصنيف\tمكان ومنطقة التخزين\tالكمية المسجلة\tالمواصفات الفنية")
            items.forEach { item ->
                appendLine("${item.sku}\t${item.name}\t${item.categoryName}\t${item.storageLocation}\t${item.quantity} ${item.unit}\t${item.technicalSpecs}")
            }
        }
    }
}
