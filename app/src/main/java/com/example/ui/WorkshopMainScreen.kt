package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.WorkshopCategory
import com.example.ui.components.AddEditItemDialog
import com.example.ui.components.InventoryListView
import com.example.ui.components.ItemClassificationCard
import com.example.ui.components.TableExportView
import com.example.ui.components.WorkshopItemRow
import com.example.ui.components.getCategoryColor
import com.example.ui.components.outlinedTextFieldColors
import com.example.ui.theme.BackgroundPolish
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueLightBg
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SafetyRed
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfacePolish
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextOnDark
import com.example.ui.theme.TextOnDarkMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ZoneConsumablesE
import com.example.ui.theme.ZonePlumbingB
import com.example.ui.theme.ZoneSteelA
import com.example.ui.theme.ZoneToolsC
import com.example.ui.theme.ZoneWeldingD

@Composable
fun WorkshopMainScreen(viewModel: WorkshopViewModel) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val inputText by viewModel.inputText.collectAsState()
    val isClassifying by viewModel.isClassifying.collectAsState()
    val currentClassification by viewModel.currentClassification.collectAsState()
    val conversationHistory by viewModel.conversationHistory.collectAsState()
    val inventoryItems by viewModel.inventoryItems.collectAsState()
    val selectedFilter by viewModel.selectedCategoryFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val itemToEdit by viewModel.itemToEdit.collectAsState()
    val showAddManualDialog by viewModel.showAddManualDialog.collectAsState()

    // Enforce RTL for proper Arabic presentation
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPolish)
                .statusBarsPadding()
                .navigationBarsPadding(),
            bottomBar = {
                NavigationBar(
                    containerColor = SurfacePolish,
                    tonalElevation = 8.dp,
                    modifier = Modifier.border(width = 1.dp, color = Slate200)
                ) {
                    NavigationBarItem(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "الاستقبال والتصنيف"
                            )
                        },
                        label = { Text("الاستقبال والتصنيف", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary,
                            indicatorColor = BlueLightBg,
                            unselectedIconColor = Slate500,
                            unselectedTextColor = Slate500
                        ),
                        modifier = Modifier.testTag("nav_tab_entry")
                    )

                    NavigationBarItem(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = "سجل المخزون"
                            )
                        },
                        label = { Text("المخزون والمناطق", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary,
                            indicatorColor = BlueLightBg,
                            unselectedIconColor = Slate500,
                            unselectedTextColor = Slate500
                        ),
                        modifier = Modifier.testTag("nav_tab_inventory")
                    )

                    NavigationBarItem(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.TableChart,
                                contentDescription = "جدول البيانات"
                            )
                        },
                        label = { Text("جدول البيانات (Excel)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary,
                            indicatorColor = BlueLightBg,
                            unselectedIconColor = Slate500,
                            unselectedTextColor = Slate500
                        ),
                        modifier = Modifier.testTag("nav_tab_table")
                    )
                }
            },
            floatingActionButton = {
                if (selectedTabIndex == 1) {
                    FloatingActionButton(
                        onClick = { viewModel.openAddManualDialog() },
                        containerColor = IndigoPrimary,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("fab_add_item")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة عنصر")
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BackgroundPolish)
            ) {
                // Top Workshop Status Bar (Deep Indigo Header)
                WorkshopTopHeader(
                    onAddManualClick = { viewModel.openAddManualDialog() }
                )

                // Tab Content Switcher
                when (selectedTabIndex) {
                    0 -> FloorEntryConsoleTab(
                        viewModel = viewModel,
                        inputText = inputText,
                        isClassifying = isClassifying,
                        currentClassification = currentClassification,
                        conversationHistory = conversationHistory,
                        onClassify = { text -> viewModel.classifyInput(text) },
                        onTextChange = { text -> viewModel.setInputText(text) },
                        onSaveClassification = { qty, unit, notes ->
                            viewModel.saveCurrentClassification(qty, unit, notes)
                        },
                        onCopy = { text, msg ->
                            viewModel.copyToClipboard(context, text, msg)
                        }
                    )

                    1 -> InventoryStockTab(
                        items = inventoryItems,
                        selectedFilter = selectedFilter,
                        searchQuery = searchQuery,
                        onFilterSelected = { viewModel.setCategoryFilter(it) },
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onIncreaseStock = { viewModel.updateStock(it, 1) },
                        onDecreaseStock = { viewModel.updateStock(it, -1) },
                        onCopyRow = { row ->
                            viewModel.copyToClipboard(context, row, "تم نسخ صف الجدول")
                        },
                        onEditItem = { viewModel.openEditDialog(it) },
                        onDeleteItem = { viewModel.deleteItem(it) }
                    )

                    2 -> TableExportView(
                        items = inventoryItems,
                        onCopyAllTable = { fullTable ->
                            viewModel.copyToClipboard(context, fullTable, "تم نسخ كامل الجدول بصيغة TSV")
                        },
                        onCopySingleRow = { row ->
                            viewModel.copyToClipboard(context, row, "تم نسخ صف العنصر")
                        }
                    )
                }
            }
        }

        // Modals
        if (showAddManualDialog || itemToEdit != null) {
            AddEditItemDialog(
                itemToEdit = itemToEdit,
                onDismiss = {
                    viewModel.closeAddManualDialog()
                    viewModel.closeEditDialog()
                },
                onSaveNew = { name, cat, loc, specs, qty, unit, notes ->
                    viewModel.addManualItem(name, cat, loc, specs, qty, unit, notes)
                },
                onSaveEdited = { updated ->
                    viewModel.saveEditedItem(updated)
                }
            )
        }
    }
}

@Composable
fun WorkshopTopHeader(
    onAddManualClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(IndigoPrimary)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Construction,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "نظام مخزون وأتمتة الورشة",
                        color = TextOnDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "المساعد الذكي جاهز للاستقبال",
                            color = TextOnDarkMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Quick Add Button
            IconButton(
                onClick = onAddManualClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة يدوية",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun FloorEntryConsoleTab(
    viewModel: WorkshopViewModel,
    inputText: String,
    isClassifying: Boolean,
    currentClassification: com.example.domain.ClassificationResult?,
    conversationHistory: List<AssistantMessage>,
    onClassify: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onSaveClassification: (quantity: Int, unit: String?, notes: String) -> Unit,
    onCopy: (String, String) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(conversationHistory.size) {
        if (conversationHistory.isNotEmpty()) {
            listState.animateScrollToItem(conversationHistory.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        // Quick floor chips for fast testing & operation
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "نماذج سريعة من أرضية الورشة:",
            color = Slate600,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        val quickFloorSamples = listOf(
            "سيخ حديد 16 مم طول 6م",
            "محبس بليّة 1 بوصة نحاس PN25",
            "صاروخ جلخ 7 بوصة 2200 واط",
            "سلك لحام 7018 مقاس 3.2 مم",
            "مسامير صلب M12*60 مع صامولة",
            "لوح صاج 3 مم 1250*2500",
            "كوع حديد مجلفن 1.5 بوصة",
            "بكرة سلك ميج 1 مم 15 كجم",
            "أقراص قطع 9 بوصة 230 مم",
            "قدمة ورنية قياس كليبر 150 مم"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(quickFloorSamples) { sample ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfacePolish)
                        .border(1.dp, Slate200, RoundedCornerShape(20.dp))
                        .clickable { onClassify(sample) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = sample,
                        color = Slate800,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Conversation / Output Feed
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(conversationHistory) { message ->
                when (message.sender) {
                    MessageSender.USER -> {
                        // User manual floor entry bubble
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                                    .background(BlueLightBg)
                                    .border(1.dp, BlueAccent.copy(alpha = 0.3f), RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "مدخل يدوي من الورشة:",
                                        color = BlueAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = message.text,
                                        color = Slate900,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    MessageSender.ASSISTANT -> {
                        if (message.classificationResult != null) {
                            // Rich 5-point Classification Card
                            ItemClassificationCard(
                                result = message.classificationResult,
                                onSaveToInventory = onSaveClassification,
                                onCopyTableRow = { row -> onCopy(row, "تم نسخ صف الجدول") },
                                onCopyFullOutput = { full -> onCopy(full, "تم نسخ التقرير الكامل") }
                            )
                        } else {
                            // Welcome or text message
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SurfacePolish)
                                    .border(1.dp, Slate200, RoundedCornerShape(14.dp))
                                    .padding(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = IndigoPrimary,
                                        modifier = Modifier.size(20.dp).padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = message.text,
                                        color = Slate800,
                                        fontSize = 13.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }

                    MessageSender.SYSTEM -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SuccessGreen.copy(alpha = 0.1f))
                                .border(1.dp, SuccessGreen.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = message.text,
                                color = SuccessGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (isClassifying) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfacePolish)
                            .border(1.dp, Slate200, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = IndigoPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "جاري تحليل وتصنيف العنصر وتحديد كود SKU ومكان التخزين...",
                                color = IndigoPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Floor Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onTextChange,
                placeholder = { Text("أدخل اسم أو وصف العنصر (مثال: محبس 1 بوصة نحاس)") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("floor_input_field"),
                shape = RoundedCornerShape(12.dp),
                colors = outlinedTextFieldColors(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (inputText.isNotBlank() && !isClassifying) {
                        onClassify(inputText)
                    }
                }),
                trailingIcon = {
                    if (inputText.isNotEmpty()) {
                        IconButton(onClick = { onTextChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "مسح",
                                tint = Slate500
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (inputText.isNotBlank() && !isClassifying) {
                        onClassify(inputText)
                    }
                },
                enabled = inputText.isNotBlank() && !isClassifying,
                modifier = Modifier
                    .height(54.dp)
                    .testTag("classify_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IndigoPrimary,
                    disabledContainerColor = Slate200
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "تحليل وتصنيف",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun InventoryStockTab(
    items: List<com.example.data.local.WorkshopItemEntity>,
    selectedFilter: String,
    searchQuery: String,
    onFilterSelected: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onIncreaseStock: (Long) -> Unit,
    onDecreaseStock: (Long) -> Unit,
    onCopyRow: (String) -> Unit,
    onEditItem: (com.example.data.local.WorkshopItemEntity) -> Unit,
    onDeleteItem: (Long) -> Unit
) {
    InventoryListView(
        items = items,
        searchQuery = searchQuery,
        onSearchChange = onSearchChange,
        selectedFilter = selectedFilter,
        onFilterSelected = onFilterSelected,
        onIncreaseStock = onIncreaseStock,
        onDecreaseStock = onDecreaseStock,
        onCopyRow = onCopyRow,
        onEditItem = onEditItem,
        onDeleteItem = onDeleteItem
    )
}

