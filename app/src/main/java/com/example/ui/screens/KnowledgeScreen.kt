package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgronomicData
import com.example.data.model.CropCatalogItem
import com.example.data.model.LivestockCatalogItem
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AgricultureGreen
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.WarmSand
import com.example.viewmodel.MainViewModel

@Composable
fun KnowledgeScreen(
    viewModel: MainViewModel,
    initialTab: Int = 0,
    onAskAiAboutCrop: (String) -> Unit
) {
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }

    data class TabInfo(val title: String, val icon: ImageVector)
    val tabs = listOf(
        TabInfo("Cultures", Icons.Default.Grass),
        TabInfo("Élevage", Icons.Default.Pets),
        TabInfo("Eau & Sol", Icons.Default.WaterDrop),
        TabInfo("Marchés", Icons.AutoMirrored.Filled.TrendingUp)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("knowledge_screen")
    ) {
        // Top Banner with high contrast
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ForestGreen)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column {
                Text(
                    text = "SAVOIR & COMMERCIALISATION",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Guides techniques, irrigation & rentabilité de production",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFD4E5D7))
                )
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = selectedTab == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedTab = index },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isSelected) ForestGreen else Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (isSelected) ForestGreen else Color(0xFF64748B)
                            )
                        }
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> CropsGuideTab(onAskAiAboutCrop = onAskAiAboutCrop)
            1 -> LivestockGuideTab(onAskAi = onAskAiAboutCrop)
            2 -> WaterCalculatorTab(viewModel = viewModel)
            3 -> MarketAndProfitTab(viewModel = viewModel)
        }
    }
}

@Composable
private fun CropsGuideTab(
    onAskAiAboutCrop: (String) -> Unit
) {
    val crops = AgronomicData.crops

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(crops, key = { it.id }) { crop ->
            CropCardItem(crop = crop, onAskAi = onAskAiAboutCrop)
        }
    }
}

@Composable
private fun CropCardItem(
    crop: CropCatalogItem,
    onAskAi: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val categoryIcon = when (crop.cropCategoryCode) {
        "cereal" -> Icons.Default.Grass
        "legume" -> Icons.Default.Spa
        "vegetable" -> Icons.Default.Eco
        else -> Icons.Default.Grass
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = crop.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        )
                        Text(
                            text = crop.scientificName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(text = crop.category, containerColor = Color(0xFFE8F5E9), contentColor = ForestGreen)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = AgricultureGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Cycle : ${crop.cycleDays}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1B4D2E)
                    )
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                DetailVectorRow(icon = Icons.Default.Grass, title = "Semences", content = crop.seedRatePerHa)
                DetailVectorRow(icon = Icons.Default.Straighten, title = "Espacement", content = crop.spacing)
                DetailVectorRow(icon = Icons.Default.Landscape, title = "Sol adapté", content = crop.soilSuitability)
                DetailVectorRow(icon = Icons.Default.WaterDrop, title = "Besoins en eau", content = crop.wateringNeeds)
                DetailVectorRow(icon = Icons.Default.Science, title = "Fertilisation", content = crop.fertilization)
                DetailVectorRow(icon = Icons.Default.Warning, title = "Ravageurs et maladies", content = crop.keyDiseases.joinToString(", "))
                DetailVectorRow(icon = Icons.Default.Inventory2, title = "Récolte & Stockage", content = crop.harvestingTips)

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        onAskAi("Quels sont les meilleurs conseils pratiques pour réussir la culture de ${crop.name} au Burkina Faso ?")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Poser une question à l'IA sur le ${crop.name}", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun DetailVectorRow(icon: ImageVector, title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF1E293B),
                lineHeight = 18.sp
            )
        )
    }
}

@Composable
private fun LivestockGuideTab(
    onAskAi: (String) -> Unit
) {
    val items = AgronomicData.livestock

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(items, key = { it.id }) { animal ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pets,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = animal.species,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            )
                            Text(
                                text = animal.localName,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                            )
                        }
                        StatusBadge(text = animal.category, containerColor = Color(0xFFFEF3C7), contentColor = Color(0xFF92400E))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    DetailVectorRow(icon = Icons.Default.Restaurant, title = "Alimentation & Rations", content = animal.feedingGuide)
                    DetailVectorRow(icon = Icons.Default.Home, title = "Logement & Hygiène", content = animal.housingGuide)
                    DetailVectorRow(icon = Icons.Default.MedicalServices, title = "Calendrier Sanitaire & Vaccins", content = animal.sanitaryCalendar)
                    DetailVectorRow(icon = Icons.Default.Warning, title = "Risques fréquents", content = animal.commonRisks)

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onAskAi("Conseils vétérinaires et prophylaxie pour : ${animal.species}") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Icon(imageVector = Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Conseils d'élevage avec l'IA", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun WaterCalculatorTab(viewModel: MainViewModel) {
    val context = LocalContext.current
    val plots by viewModel.plots.collectAsState()

    var crop by remember { mutableStateOf("Maïs") }
    var areaStr by remember { mutableStateOf("1.0") }
    var soil by remember { mutableStateOf("Sol sablo-limoneux") }
    var irrigationSystem by remember { mutableStateOf("Goutte-à-goutte") }
    var calculationResult by remember { mutableStateOf<String?>(null) }
    var calculatedM3PerDay by remember { mutableIntStateOf(0) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color(0xFF0F172A),
        unfocusedTextColor = Color(0xFF0F172A),
        focusedLabelColor = ForestGreen,
        unfocusedLabelColor = Color(0xFF475569),
        focusedBorderColor = ForestGreen,
        unfocusedBorderColor = Color(0xFFCBD5E1)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF0284C7))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Calculateur des besoins en eau",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = crop,
                        onValueChange = { crop = it },
                        label = { Text("Culture (Maïs, Tomate, Oignon, Niébé...)") },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = areaStr,
                        onValueChange = { areaStr = it },
                        label = { Text("Superficie (en hectares)") },
                        colors = textFieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = soil,
                        onValueChange = { soil = it },
                        label = { Text("Type de sol (Sablonneux, Argileux, Zaï...)") },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = irrigationSystem,
                        onValueChange = { irrigationSystem = it },
                        label = { Text("Système (Goutte-à-goutte, Gravitaire, Manuel)") },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val area = areaStr.toDoubleOrNull() ?: 1.0
                            val dailyNeedMm = when (crop.lowercase().trim()) {
                                "tomate" -> 5.5
                                "oignon" -> 4.5
                                "maïs", "mais" -> 6.0
                                "niébé", "niebe" -> 3.5
                                else -> 5.0
                            }
                            val volumeM3PerDay = (dailyNeedMm * 10 * area).toInt()
                            val efficiency = if (irrigationSystem.contains("Goutte", ignoreCase = true)) 0.9 else 0.65
                            val actualVolume = (volumeM3PerDay / efficiency).toInt()
                            calculatedM3PerDay = actualVolume

                            calculationResult = """
                                Estimation hydrique pour $area ha de $crop :
                                • Volume net journalier : $volumeM3PerDay m³/jour
                                • Volume total d'arrosage ($irrigationSystem) : $actualVolume m³/jour (soit ${actualVolume * 1000} litres)
                                • Fréquence recommandée : 1 à 2 fois par jour (tôt le matin avant 8h ou après 17h)
                                • Conseil de résilience : Paillage végétal épais au pied pour économiser 35% d'eau.
                            """.trimIndent()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Calculate, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Estimer les besoins en eau", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (calculationResult != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Résultat de l'estimation",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = calculationResult!!,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 22.sp,
                                color = Color(0xFF0F172A)
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        // Allow user to save calculation into their farm data!
                        OutlinedButton(
                            onClick = {
                                val targetPlot = plots.firstOrNull { it.cropName.contains(crop, ignoreCase = true) }
                                    ?: plots.firstOrNull()
                                val plotId = targetPlot?.id ?: "general"
                                val plotName = targetPlot?.name ?: "Parcelle $crop"

                                viewModel.addIntervention(
                                    plotId = plotId,
                                    plotName = plotName,
                                    type = "Arrosage",
                                    quantity = "$calculatedM3PerDay m³/jour ($irrigationSystem)",
                                    cost = 0L,
                                    notes = "Estimation hydrique calculée pour $crop ($areaStr ha). Paillage recommandé."
                                )
                                Toast.makeText(context, "Arrosage planifié enregistré dans votre carnet de parcelles !", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF166534)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Enregistrer dans mon carnet de parcelles", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketAndProfitTab(viewModel: MainViewModel) {
    val context = LocalContext.current
    val plots by viewModel.plots.collectAsState()
    val prices = AgronomicData.marketPrices

    var cropName by remember { mutableStateOf("Maïs") }
    var seedCostStr by remember { mutableStateOf("25000") }
    var fertCostStr by remember { mutableStateOf("95000") }
    var laborCostStr by remember { mutableStateOf("60000") }
    var yieldTonsStr by remember { mutableStateOf("3.0") }
    var salePricePerKgStr by remember { mutableStateOf("200") }
    var profitResult by remember { mutableStateOf<String?>(null) }
    var lastTotalCost by remember { mutableStateOf(0L) }
    var lastNetProfit by remember { mutableStateOf(0L) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color(0xFF0F172A),
        unfocusedTextColor = Color(0xFF0F172A),
        focusedLabelColor = ForestGreen,
        unfocusedLabelColor = Color(0xFF475569),
        focusedBorderColor = ForestGreen,
        unfocusedBorderColor = Color(0xFFCBD5E1)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Cours des marchés agricoles locaux",
                subtitle = "Burkina Faso • Prix indicatifs de gros"
            )
        }

        items(prices) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = item.product,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        )
                        Text(
                            text = "${item.market} (${item.region})",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                        )
                        Text(
                            text = item.unit,
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "%,d FCFA".format(item.priceFcfa),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = ForestGreen
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val trendIcon = when (item.trend) {
                                "hausse" -> Icons.AutoMirrored.Filled.TrendingUp
                                "baisse" -> Icons.AutoMirrored.Filled.TrendingDown
                                else -> Icons.AutoMirrored.Filled.TrendingFlat
                            }
                            val trendColor = when (item.trend) {
                                "hausse" -> Color(0xFF16A34A)
                                "baisse" -> Color(0xFFDC2626)
                                else -> Color(0xFF64748B)
                            }
                            Icon(imageVector = trendIcon, contentDescription = null, tint = trendColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = item.dateUpdated, fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                    }
                }
            }
        }

        // Profitability Calculator
        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(
                title = "Calculateur de rentabilité prévisionnelle",
                subtitle = "Saisissez vos charges et estimez votre bénéfice"
            )
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = cropName,
                        onValueChange = { cropName = it },
                        label = { Text("Culture") },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = seedCostStr,
                            onValueChange = { seedCostStr = it },
                            label = { Text("Semences (FCFA)") },
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = fertCostStr,
                            onValueChange = { fertCostStr = it },
                            label = { Text("Engrais (FCFA)") },
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = laborCostStr,
                            onValueChange = { laborCostStr = it },
                            label = { Text("Main d'œuvre (FCFA)") },
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = yieldTonsStr,
                            onValueChange = { yieldTonsStr = it },
                            label = { Text("Rendement (Tonnes)") },
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                    OutlinedTextField(
                        value = salePricePerKgStr,
                        onValueChange = { salePricePerKgStr = it },
                        label = { Text("Prix de vente estimé (FCFA/kg)") },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Button(
                        onClick = {
                            val seedCost = seedCostStr.toLongOrNull() ?: 0L
                            val fertCost = fertCostStr.toLongOrNull() ?: 0L
                            val laborCost = laborCostStr.toLongOrNull() ?: 0L
                            val totalCost = seedCost + fertCost + laborCost
                            lastTotalCost = totalCost

                            val yieldKg = ((yieldTonsStr.toDoubleOrNull() ?: 3.0) * 1000).toLong()
                            val salePrice = salePricePerKgStr.toLongOrNull() ?: 200L
                            val totalRevenue = yieldKg * salePrice
                            val profit = totalRevenue - totalCost
                            lastNetProfit = profit
                            val roi = if (totalCost > 0) (profit.toDouble() / totalCost * 100).toInt() else 0

                            profitResult = """
                                Bilan financier prévisionnel pour $cropName :
                                • Dépenses totales de production : %,d FCFA
                                • Chiffre d'affaires attendu ($yieldKg kg x $salePrice F) : %,d FCFA
                                • Marge nette bénéficiaire : %,d FCFA
                                • Taux de rentabilité (ROI) : +$roi %
                            """.trimIndent().format(totalCost, totalRevenue, profit)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Calculate, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calculer la marge nette", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    if (profitResult != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF0FDF4))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    text = profitResult!!,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF166534),
                                        lineHeight = 22.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = {
                                        val targetPlot = plots.firstOrNull { it.cropName.contains(cropName, ignoreCase = true) }
                                            ?: plots.firstOrNull()
                                        val plotId = targetPlot?.id ?: "budget_plot"
                                        val plotName = targetPlot?.name ?: "Parcelle $cropName"

                                        viewModel.addIntervention(
                                            plotId = plotId,
                                            plotName = plotName,
                                            type = "Fertilisation",
                                            quantity = "$yieldTonsStr T prévues",
                                            cost = lastTotalCost,
                                            notes = "Budget prévisionnel $cropName: charges %,d FCFA, bénéfice estimé %,d FCFA.".format(lastTotalCost, lastNetProfit)
                                        )
                                        Toast.makeText(context, "Dépenses prévisionnelles ajoutées au journal de bord !", Toast.LENGTH_LONG).show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF166534)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Enregistrer ces charges dans mon exploitation", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
