package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.WorkshopCategory
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.ZoneConsumablesE
import com.example.ui.theme.ZonePlumbingB
import com.example.ui.theme.ZoneSteelA
import com.example.ui.theme.ZoneToolsC
import com.example.ui.theme.ZoneWeldingD

fun getCategoryColor(category: WorkshopCategory): Color {
    return when (category) {
        WorkshopCategory.STEEL_FORGING -> ZoneSteelA
        WorkshopCategory.PLUMBING -> ZonePlumbingB
        WorkshopCategory.TOOLS -> ZoneToolsC
        WorkshopCategory.WELDING -> ZoneWeldingD
        WorkshopCategory.CONSUMABLES -> ZoneConsumablesE
    }
}

fun getCategoryColorByCode(code: String): Color {
    return getCategoryColor(WorkshopCategory.fromCode(code))
}

@Composable
fun ZoneBadge(
    category: WorkshopCategory,
    modifier: Modifier = Modifier,
    showPrefix: Boolean = true
) {
    val color = getCategoryColor(category)
    val icon = when (category) {
        WorkshopCategory.STEEL_FORGING -> Icons.Default.Construction
        WorkshopCategory.PLUMBING -> Icons.Default.Plumbing
        WorkshopCategory.TOOLS -> Icons.Default.Handyman
        WorkshopCategory.WELDING -> Icons.Default.LocalGasStation
        WorkshopCategory.CONSUMABLES -> Icons.Default.Widgets
    }

    val zoneShort = when (category) {
        WorkshopCategory.STEEL_FORGING -> "منطقة A"
        WorkshopCategory.PLUMBING -> "منطقة B"
        WorkshopCategory.TOOLS -> "منطقة C"
        WorkshopCategory.WELDING -> "منطقة D"
        WorkshopCategory.CONSUMABLES -> "منطقة E"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .size(13.dp)
                    .padding(end = 3.dp)
            )
            Text(
                text = if (showPrefix) "${category.prefix} • $zoneShort" else zoneShort,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SkuChip(
    sku: String,
    modifier: Modifier = Modifier
) {
    val category = WorkshopCategory.fromCode(sku)
    val color = getCategoryColor(category)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Slate900)
            .border(1.dp, Slate800, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = sku,
            color = Color(0xFFF1F5F9),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

