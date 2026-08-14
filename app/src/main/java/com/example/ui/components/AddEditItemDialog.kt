package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.WorkshopItemEntity
import com.example.domain.WorkshopCategory
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueLightBg
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.SurfacePolish
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AddEditItemDialog(
    itemToEdit: WorkshopItemEntity? = null,
    onDismiss: () -> Unit,
    onSaveNew: (
        name: String,
        category: WorkshopCategory,
        storageLocation: String,
        technicalSpecs: String,
        quantity: Int,
        unit: String,
        notes: String
    ) -> Unit,
    onSaveEdited: (WorkshopItemEntity) -> Unit
) {
    val isEditing = itemToEdit != null

    var name by remember { mutableStateOf(itemToEdit?.name ?: "") }
    var selectedCategory by remember {
        mutableStateOf(
            if (itemToEdit != null) WorkshopCategory.fromCode(itemToEdit.categoryCode)
            else WorkshopCategory.STEEL_FORGING
        )
    }
    var storageLocation by remember {
        mutableStateOf(itemToEdit?.storageLocation ?: selectedCategory.defaultLocationPrefix)
    }
    var technicalSpecs by remember { mutableStateOf(itemToEdit?.technicalSpecs ?: "") }
    var quantityText by remember { mutableStateOf((itemToEdit?.quantity ?: 1).toString()) }
    var unit by remember { mutableStateOf(itemToEdit?.unit ?: selectedCategory.defaultUnit) }
    var minAlertText by remember { mutableStateOf((itemToEdit?.minQuantityAlert ?: 5).toString()) }
    var notes by remember { mutableStateOf(itemToEdit?.notes ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePolish),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Slate200),
                width = 1.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) "تعديل عنصر المخزون (${itemToEdit?.sku})" else "إضافة عنصر يدوي للمخزون",
                        color = IndigoPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = Slate500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم العنصر القياسي") },
                    placeholder = { Text("مثال: سيخ حديد 16 مم أو محبس 1 بوصة") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = outlinedTextFieldColors()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category selector
                Text(
                    text = "التصنيف والمنطقة (SKU Category):",
                    color = Slate700,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    WorkshopCategory.entries.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        val color = getCategoryColor(cat)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) color.copy(alpha = 0.12f) else Slate50)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) color else Slate200,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    selectedCategory = cat
                                    if (!isEditing) {
                                        storageLocation = cat.defaultLocationPrefix
                                        unit = cat.defaultUnit
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${cat.prefix} • ${cat.arabicName}",
                                    color = if (isSelected) color else Slate900,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = cat.zoneName,
                                    color = if (isSelected) color else Slate500,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Storage Location
                OutlinedTextField(
                    value = storageLocation,
                    onValueChange = { storageLocation = it },
                    label = { Text("منطقة ومكان التخزين الدقيق (الرف/الصندوق)") },
                    placeholder = { Text("مثال: المنطقة A - رف الكمرات R-02") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = outlinedTextFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Technical Specs
                OutlinedTextField(
                    value = technicalSpecs,
                    onValueChange = { technicalSpecs = it },
                    label = { Text("المواصفات الفنية (القطر، المقاس، الخامة)") },
                    placeholder = { Text("مثال: قطر 16 مم، صلب مجدول، طول 12 متر") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = outlinedTextFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quantity & Unit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("الكمية") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = outlinedTextFieldColors()
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("الوحدة") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = outlinedTextFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Min Alert & Notes
                OutlinedTextField(
                    value = minAlertText,
                    onValueChange = { minAlertText = it },
                    label = { Text("الحد الأدنى لتنبيه نقص المخزون") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = outlinedTextFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات إضافية") },
                    placeholder = { Text("اختياري: استخدامات محددة أو موردين") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = outlinedTextFieldColors()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        val parsedQty = quantityText.toIntOrNull() ?: 1
                        val parsedMin = minAlertText.toIntOrNull() ?: 5
                        val finalName = if (name.isBlank()) "عنصر بدون اسم" else name.trim()

                        if (isEditing && itemToEdit != null) {
                            val updated = itemToEdit.copy(
                                name = finalName,
                                categoryCode = selectedCategory.code,
                                categoryName = selectedCategory.arabicName,
                                storageLocation = storageLocation.ifBlank { selectedCategory.defaultLocationPrefix },
                                technicalSpecs = technicalSpecs,
                                quantity = parsedQty,
                                unit = unit.ifBlank { selectedCategory.defaultUnit },
                                minQuantityAlert = parsedMin,
                                notes = notes
                            )
                            onSaveEdited(updated)
                        } else {
                            onSaveNew(
                                finalName,
                                selectedCategory,
                                storageLocation.ifBlank { selectedCategory.defaultLocationPrefix },
                                technicalSpecs,
                                parsedQty,
                                unit.ifBlank { selectedCategory.defaultUnit },
                                notes
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEditing) "حفظ التعديلات" else "إضافة للمخزون",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Slate900,
    unfocusedTextColor = Slate800,
    focusedContainerColor = SurfacePolish,
    unfocusedContainerColor = SurfacePolish,
    focusedBorderColor = IndigoPrimary,
    unfocusedBorderColor = Slate300,
    focusedLabelColor = IndigoPrimary,
    unfocusedLabelColor = Slate600,
    cursorColor = IndigoPrimary
)

