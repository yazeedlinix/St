package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.InventoryItem
import com.example.data.util.InventoryExportHelper
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SafetyRed
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfacePolish
import com.example.ui.theme.ZoneConsumablesE
import com.example.ui.theme.ZonePlumbingB
import com.example.ui.theme.ZoneSteelA
import com.example.ui.theme.ZoneToolsC
import com.example.ui.theme.ZoneWeldingD

/**
 * مكون عرض القائمة (LazyColumn) لعرض كافة عناصر المخزون المخزنة في قاعدة البيانات
 * مع إمكانية البحث المباشر حسب الاسم أو كود SKU وفلترة المناطق، بالإضافة لأزرار التصدير السريع CSV/PDF.
 */
@Composable
fun InventoryListView(
    items: List<InventoryItem>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: String = "ALL",
    onFilterSelected: (String) -> Unit = {},
    onIncreaseStock: (Long) -> Unit,
    onDecreaseStock: (Long) -> Unit,
    onCopyRow: (String) -> Unit,
    onEditItem: (InventoryItem) -> Unit,
    onDeleteItem: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Search bar with Instant SKU and Name search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("بحث بالاسم، كود SKU، أو مكان التخزين...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "أيقونة البحث",
                    tint = IndigoPrimary
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = searchQuery.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "مسح البحث",
                            tint = Slate500
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("inventory_search_field"),
            shape = RoundedCornerShape(12.dp),
            colors = outlinedTextFieldColors(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Zone filter chips
        val filters = listOf(
            Triple("ALL", "الكل", IndigoPrimary),
            Triple("A-STL", "A: حدادة", ZoneSteelA),
            Triple("B-PLV", "B: سباكة", ZonePlumbingB),
            Triple("C-TLS", "C: أدوات", ZoneToolsC),
            Triple("D-WLD", "D: لحام", ZoneWeldingD),
            Triple("E-CNS", "E: مستهلكات", ZoneConsumablesE)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(filters) { (code, label, color) ->
                val isSelected = selectedFilter == code
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) color.copy(alpha = 0.15f) else SurfacePolish)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) color else Slate200,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onFilterSelected(code) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) color else Slate700,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Summary counters & Quick Export PDF / CSV Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "العناصر: ${items.size}",
                    color = Slate700,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                val lowStockCount = items.count { it.quantity <= it.minQuantityAlert }
                if (lowStockCount > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SafetyRed.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "⚠️ $lowStockCount نقص",
                            color = SafetyRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Quick Export buttons in list header
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick PDF
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SafetyRed.copy(alpha = 0.12f))
                        .clickable {
                            if (items.isEmpty()) {
                                Toast.makeText(context, "لا توجد عناصر لتصديرها!", Toast.LENGTH_SHORT).show()
                            } else {
                                InventoryExportHelper.exportAndSharePdf(context, items)
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "تصدير PDF",
                            tint = SafetyRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PDF",
                            color = SafetyRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Quick CSV
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SuccessGreen.copy(alpha = 0.12f))
                        .clickable {
                            if (items.isEmpty()) {
                                Toast.makeText(context, "لا توجد عناصر لتصديرها!", Toast.LENGTH_SHORT).show()
                            } else {
                                InventoryExportHelper.exportAndShareCsv(context, items)
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "تصدير CSV",
                            tint = SuccessGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "CSV",
                            color = SuccessGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // LazyColumn List of Inventory Items
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = Slate300,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) "لا توجد نتائج بحث مطابقة لـ \"$searchQuery\"" else "لا توجد عناصر مسجلة في هذا التصنيف",
                        color = Slate700,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "يمكنك إدخال عناصر جديدة عبر التبويب الأول أو زر (+)",
                        color = Slate500,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("inventory_lazy_column"),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 64.dp)
            ) {
                items(
                    items = items,
                    key = { it.id }
                ) { item ->
                    WorkshopItemRow(
                        item = item,
                        onIncreaseStock = { onIncreaseStock(item.id) },
                        onDecreaseStock = { onDecreaseStock(item.id) },
                        onCopyTableRow = { onCopyRow(item.tableRowFormat) },
                        onEditItem = { onEditItem(item) },
                        onDeleteItem = { onDeleteItem(item.id) }
                    )
                }
            }
        }
    }
}
