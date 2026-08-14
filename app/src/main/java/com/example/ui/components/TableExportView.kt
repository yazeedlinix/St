package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.example.domain.WorkshopCategory
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SafetyRed
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfacePolish

@Composable
fun TableExportView(
    items: List<InventoryItem>,
    onCopyAllTable: (String) -> Unit,
    onCopySingleRow: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Top Action Card with CSV & PDF Export Buttons
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("export_actions_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePolish),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Slate200),
                width = 1.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "تصدير وطباعة بيانات المخزون",
                                color = Slate900,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "إجمالي العناصر المجدولة: ${items.size} عنصر",
                                color = Slate600,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Export Actions Grid: PDF & CSV & Copy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Export PDF Button
                    Button(
                        onClick = {
                            if (items.isEmpty()) {
                                Toast.makeText(context, "لا توجد عناصر لتصديرها!", Toast.LENGTH_SHORT).show()
                            } else {
                                InventoryExportHelper.exportAndSharePdf(context, items)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("export_pdf_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SafetyRed,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تصدير PDF (للطباعة)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Export CSV Button
                    Button(
                        onClick = {
                            if (items.isEmpty()) {
                                Toast.makeText(context, "لا توجد عناصر لتصديرها!", Toast.LENGTH_SHORT).show()
                            } else {
                                InventoryExportHelper.exportAndShareCsv(context, items)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("export_csv_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "CSV",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تصدير CSV (Excel)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Copy All as Table Button
                OutlinedButton(
                    onClick = {
                        val fullTsv = buildString {
                            appendLine("الكود (SKU)\tاسم العنصر القياسي\tالتصنيف\tمكان ومنطقة التخزين\tالكمية\tالمواصفات الفنية")
                            items.forEach { item ->
                                appendLine("${item.sku}\t${item.name}\t${item.categoryName}\t${item.storageLocation}\t${item.quantity} ${item.unit}\t${item.technicalSpecs}")
                            }
                        }
                        onCopyAllTable(fullTsv)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("copy_table_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = IndigoPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "نسخ جدول البيانات للحافظة (Clipboard)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Horizontal scrollable preview table
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(SurfacePolish)
                .border(1.dp, Slate200, RoundedCornerShape(14.dp))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(horizontalScrollState)
            ) {
                // Table Header
                item {
                    Row(
                        modifier = Modifier
                            .background(IndigoPrimary)
                            .padding(vertical = 12.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TableHeaderCell("الكود (SKU)", 110.dp)
                        TableHeaderCell("اسم العنصر القياسي", 200.dp)
                        TableHeaderCell("التصنيف", 130.dp)
                        TableHeaderCell("مكان التخزين", 180.dp)
                        TableHeaderCell("الكمية", 90.dp)
                        TableHeaderCell("المواصفات الفنية", 220.dp)
                        TableHeaderCell("نسخ", 60.dp)
                    }
                    HorizontalDivider(color = Slate300, thickness = 1.dp)
                }

                // Table Rows
                itemsIndexed(items) { index, item ->
                    val isEven = index % 2 == 0
                    val rowColor = if (isEven) SurfacePolish else Slate50

                    Row(
                        modifier = Modifier
                            .background(rowColor)
                            .padding(vertical = 9.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // SKU
                        Box(modifier = Modifier.width(110.dp)) {
                            SkuChip(sku = item.sku)
                        }

                        // Name
                        Text(
                            text = item.name,
                            color = Slate900,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.width(200.dp).padding(horizontal = 4.dp)
                        )

                        // Category
                        Text(
                            text = item.categoryName,
                            color = getCategoryColor(WorkshopCategory.fromCode(item.categoryCode)),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.width(130.dp).padding(horizontal = 4.dp)
                        )

                        // Storage Location
                        Text(
                            text = item.storageLocation,
                            color = IndigoPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.width(180.dp).padding(horizontal = 4.dp)
                        )

                        // Quantity
                        Text(
                            text = "${item.quantity} ${item.unit}",
                            color = Slate800,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(90.dp).padding(horizontal = 4.dp)
                        )

                        // Specs
                        Text(
                            text = item.technicalSpecs,
                            color = Slate600,
                            fontSize = 11.sp,
                            modifier = Modifier.width(220.dp).padding(horizontal = 4.dp)
                        )

                        // Copy Button
                        Box(modifier = Modifier.width(60.dp), contentAlignment = Alignment.Center) {
                            IconButton(
                                onClick = { onCopySingleRow(item.tableRowFormat) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "نسخ الصف",
                                    tint = IndigoPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = Slate200, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun TableHeaderCell(text: String, width: androidx.compose.ui.unit.Dp) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .width(width)
            .padding(horizontal = 4.dp)
    )
}
