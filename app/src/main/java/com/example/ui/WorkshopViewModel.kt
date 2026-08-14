package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.WorkshopDatabase
import com.example.data.local.WorkshopItemEntity
import com.example.data.local.WorkshopRepository
import com.example.domain.ClassificationResult
import com.example.domain.WorkshopCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AssistantMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val classificationResult: ClassificationResult? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
    USER,
    ASSISTANT,
    SYSTEM
}

class WorkshopViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkshopRepository

    init {
        val db = WorkshopDatabase.getDatabase(application, viewModelScope)
        repository = WorkshopRepository(db.workshopItemDao())
    }

    private val _selectedCategoryFilter = MutableStateFlow("ALL")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isClassifying = MutableStateFlow(false)
    val isClassifying: StateFlow<Boolean> = _isClassifying.asStateFlow()

    private val _currentClassification = MutableStateFlow<ClassificationResult?>(null)
    val currentClassification: StateFlow<ClassificationResult?> = _currentClassification.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _conversationHistory = MutableStateFlow<List<AssistantMessage>>(listOf(
        AssistantMessage(
            sender = MessageSender.ASSISTANT,
            text = "مرحباً بك في نظام إدارة المخزون والأتمتة لورشة التصنيع الصناعي.\nأنا جاهز تماماً الآن لاستقبال أول عنصر يدوي من أرضية الورشة (مواد حدادة، سباكة، أدوات، ماكينات ومستهلكات لحام، أو قطع استهلاكية) لتصنيفه فوراً وتوليد كود SKU ومكان التخزين وجدولته."
        )
    ))
    val conversationHistory: StateFlow<List<AssistantMessage>> = _conversationHistory.asStateFlow()

    private val _itemToEdit = MutableStateFlow<WorkshopItemEntity?>(null)
    val itemToEdit: StateFlow<WorkshopItemEntity?> = _itemToEdit.asStateFlow()

    private val _showAddManualDialog = MutableStateFlow(false)
    val showAddManualDialog: StateFlow<Boolean> = _showAddManualDialog.asStateFlow()

    private val _showExportDialog = MutableStateFlow(false)
    val showExportDialog: StateFlow<Boolean> = _showExportDialog.asStateFlow()

    val inventoryItems: StateFlow<List<WorkshopItemEntity>> = combine(
        repository.allItems,
        _selectedCategoryFilter,
        _searchQuery
    ) { allItems, category, query ->
        var filtered = allItems
        if (category != "ALL") {
            filtered = filtered.filter { it.categoryCode == category }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            filtered = filtered.filter {
                it.name.lowercase().contains(q) ||
                it.sku.lowercase().contains(q) ||
                it.storageLocation.lowercase().contains(q) ||
                it.technicalSpecs.lowercase().contains(q)
            }
        }
        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setInputText(text: String) {
        _inputText.value = text
    }

    fun setCategoryFilter(categoryCode: String) {
        _selectedCategoryFilter.value = categoryCode
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun classifyInput(input: String) {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return

        _isClassifying.value = true
        _inputText.value = ""

        // Add user message to history
        val userMsg = AssistantMessage(
            sender = MessageSender.USER,
            text = trimmed
        )
        _conversationHistory.value = _conversationHistory.value + userMsg

        viewModelScope.launch {
            try {
                val result = repository.classifyRawFloorInput(trimmed)
                _currentClassification.value = result

                val assistantMsg = AssistantMessage(
                    sender = MessageSender.ASSISTANT,
                    text = result.fullFormattedOutput,
                    classificationResult = result
                )
                _conversationHistory.value = _conversationHistory.value + assistantMsg
            } catch (e: Exception) {
                val errorMsg = AssistantMessage(
                    sender = MessageSender.SYSTEM,
                    text = "تعذر إتمام التحليل: ${e.localizedMessage}"
                )
                _conversationHistory.value = _conversationHistory.value + errorMsg
            } finally {
                _isClassifying.value = false
            }
        }
    }

    fun saveCurrentClassification(quantity: Int = 1, unit: String? = null, notes: String = "") {
        val current = _currentClassification.value ?: return
        viewModelScope.launch {
            repository.insertFromClassification(current, quantity, unit, notes)
            val confirmationMsg = AssistantMessage(
                sender = MessageSender.SYSTEM,
                text = " تم حفظ العنصر [${current.sku} - ${current.standardName}] في مخزون الورشة بنجاح (الكمية: $quantity ${unit ?: current.category.defaultUnit})."
            )
            _conversationHistory.value = _conversationHistory.value + confirmationMsg
            _currentClassification.value = null
        }
    }

    fun addManualItem(
        name: String,
        category: WorkshopCategory,
        storageLocation: String,
        technicalSpecs: String,
        quantity: Int,
        unit: String,
        notes: String
    ) {
        viewModelScope.launch {
            val totalCount = inventoryItems.value.size + 1
            val sku = "${category.prefix}-${String.format(java.util.Locale.US, "%03d", totalCount)}"
            val entity = WorkshopItemEntity(
                sku = sku,
                name = name.trim(),
                categoryCode = category.code,
                categoryName = category.arabicName,
                storageLocation = storageLocation.trim(),
                technicalSpecs = technicalSpecs.trim(),
                quantity = quantity,
                unit = unit.trim(),
                defaultQuantity = "1 $unit",
                notes = notes.trim()
            )
            repository.insertItem(entity)
            _showAddManualDialog.value = false
        }
    }

    fun updateStock(id: Long, delta: Int) {
        val current = inventoryItems.value.firstOrNull { it.id == id } ?: return
        val newQty = (current.quantity + delta).coerceAtLeast(0)
        viewModelScope.launch {
            repository.updateQuantity(id, newQty)
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            repository.deleteItem(id)
        }
    }

    fun openEditDialog(item: WorkshopItemEntity) {
        _itemToEdit.value = item
    }

    fun closeEditDialog() {
        _itemToEdit.value = null
    }

    fun openAddManualDialog() {
        _showAddManualDialog.value = true
    }

    fun closeAddManualDialog() {
        _showAddManualDialog.value = false
    }

    fun openExportDialog() {
        _showExportDialog.value = true
    }

    fun closeExportDialog() {
        _showExportDialog.value = false
    }

    fun saveEditedItem(updated: WorkshopItemEntity) {
        viewModelScope.launch {
            repository.updateItem(updated)
            _itemToEdit.value = null
        }
    }

    fun copyToClipboard(context: Context, text: String, toastMessage: String = "تم نسخ النص إلى الحافظة") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Workshop Inventory", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
    }
}
