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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.auth.AuthManager
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.WarmSand
import com.example.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onSignedOut: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val userProfile by viewModel.userProfile.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showRegionDialog by remember { mutableStateOf(false) }

    val languages = listOf(
        Triple("fr", "Français", "Langue officielle de travail"),
        Triple("mos", "Mooré", "Yaa Gom-biiga (Burkina Faso)"),
        Triple("dyu", "Dioula", "Julakan (Afrique de l'Ouest)"),
        Triple("ful", "Fulfuldé", "Pulaar / Fulani"),
        Triple("en", "English", "International")
    )

    val regions = listOf(
        "Centre (Ouagadougou)",
        "Hauts-Bassins (Bobo-Dioulasso)",
        "Boucle du Mouhoun (Dédougou)",
        "Centre-Ouest (Koudougou)",
        "Nord (Ouahigouya)",
        "Centre-Est (Tenkodogo / Pouytenga)",
        "Est (Fada N'Gourma)",
        "Sahel (Dori)",
        "Cascades (Banfora)",
        "Sud-Ouest (Gaoua)",
        "Plateau-Central (Ziniaré)",
        "Centre-Sud (Manga)"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmSand)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ForestGreen),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E7D32)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = userProfile?.name?.ifBlank { "Mon Exploitation Agricole" } ?: "Mon Exploitation Agricole",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    if (!userProfile?.email.isNullOrBlank()) {
                        Text(
                            text = userProfile!!.email,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFD4E5D7))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusBadge(
                            text = "Cloud Firestore Actif",
                            containerColor = Color(0xFF1E3A24),
                            contentColor = FreshGreen
                        )
                        StatusBadge(
                            text = userProfile?.experienceLevel ?: "Intermédiaire",
                            containerColor = Color(0xFF1E3A24),
                            contentColor = Color(0xFFFFD54F)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Edit Profile Button
                    Button(
                        onClick = { showEditProfileDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("edit_profile_button")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Modifier mes informations agricoles", color = ForestGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Details of User's Farm Profile
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Fiche de l'exploitation",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    ProfileDetailRow(
                        icon = Icons.Default.LocationOn,
                        title = "Localisation & Région",
                        value = "${userProfile?.communeOrVillage?.ifBlank { "Commune non spécifiée" } ?: "Commune non spécifiée"} • ${userProfile?.region ?: "Centre (Ouagadougou)"}"
                    )

                    ProfileDetailRow(
                        icon = Icons.Default.Call,
                        title = "Téléphone de contact",
                        value = userProfile?.phone?.ifBlank { "Non renseigné" } ?: "Non renseigné"
                    )

                    ProfileDetailRow(
                        icon = Icons.Default.Flag,
                        title = "Objectif de campagne",
                        value = userProfile?.farmObjective?.ifBlank { "Augmenter le rendement et réduire les coûts d'intrants" } ?: "Augmenter le rendement et réduire les coûts d'intrants"
                    )

                    ProfileDetailRow(
                        icon = Icons.Default.School,
                        title = "Niveau d'expérience",
                        value = userProfile?.experienceLevel ?: "Intermédiaire"
                    )
                }
            }
        }

        // Application Settings
        item {
            SectionHeader(
                title = "Paramètres de l'application",
                subtitle = "Personnalisation & synchronisation"
            )
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    val langLabel = languages.firstOrNull { it.first == currentLang }?.second ?: "Français"
                    SettingRow(
                        icon = Icons.Default.Language,
                        title = "Langue de l'assistant",
                        subtitle = "$langLabel (changez pour Mooré, Dioula, Fulfuldé)",
                        onClick = { showLanguageDialog = true }
                    )

                    val currentRegion = userProfile?.region ?: "Centre (Ouagadougou)"
                    SettingRow(
                        icon = Icons.Default.LocationOn,
                        title = "Zone agroécologique principale",
                        subtitle = currentRegion,
                        onClick = { showRegionDialog = true }
                    )

                    SettingRow(
                        icon = Icons.Default.CloudDone,
                        title = "Base de données Cloud Firestore",
                        subtitle = "Parcelles, interventions, élevage et diagnostics synchronisés",
                        onClick = {
                            Toast.makeText(context, "Vos données agricoles sont sécurisées et synchronisées en temps réel.", Toast.LENGTH_SHORT).show()
                        }
                    )

                    SettingRow(
                        icon = Icons.Default.VerifiedUser,
                        title = "Moteur d'intelligence artificielle",
                        subtitle = "Agnes AI v1 (Spécialisé agro-écologie Afrique de l'Ouest)",
                        onClick = {
                            Toast.makeText(context, "Modèle connecté : agnes-3.0-flash", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // About AGRI-IA
        item {
            SectionHeader(title = "À propos de AGRI-IA")
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AGRI-IA v1.0",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        )
                    }
                    Text(
                        text = "Plateforme technologique agricole et assistant intelligent pour le Burkina Faso et l'Afrique de l'Ouest. Développé pour aider les producteurs dans la gestion de leurs parcelles, le diagnostic phytosanitaire et l'optimisation des intrants.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF334155), lineHeight = 19.sp)
                    )
                }
            }
        }

        // Sign Out Button
        item {
            Button(
                onClick = {
                    AuthManager.signOut(
                        credentialManager = credentialManager,
                        scope = coroutineScope,
                        onSignOutComplete = onSignedOut
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("sign_out_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEE2E2),
                    contentColor = Color(0xFFDC2626)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFDC2626))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Se déconnecter", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var inputName by remember { mutableStateOf(userProfile?.name ?: "") }
        var inputPhone by remember { mutableStateOf(userProfile?.phone ?: "") }
        var inputVillage by remember { mutableStateOf(userProfile?.communeOrVillage ?: "") }
        var inputRegion by remember { mutableStateOf(userProfile?.region ?: "Centre (Ouagadougou)") }
        var inputObjective by remember { mutableStateOf(userProfile?.farmObjective ?: "") }
        var inputExperience by remember { mutableStateOf(userProfile?.experienceLevel ?: "Intermédiaire") }

        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFF0F172A),
            unfocusedTextColor = Color(0xFF0F172A),
            focusedLabelColor = ForestGreen,
            unfocusedLabelColor = Color(0xFF475569),
            focusedBorderColor = ForestGreen,
            unfocusedBorderColor = Color(0xFFCBD5E1)
        )

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = "Modifier mes informations agricoles",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        label = { Text("Nom complet ou Nom d'exploitation") },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth().testTag("profile_input_name")
                    )

                    OutlinedTextField(
                        value = inputPhone,
                        onValueChange = { inputPhone = it },
                        label = { Text("Numéro de téléphone") },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth().testTag("profile_input_phone")
                    )

                    OutlinedTextField(
                        value = inputVillage,
                        onValueChange = { inputVillage = it },
                        label = { Text("Commune ou Village") },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth().testTag("profile_input_village")
                    )

                    OutlinedTextField(
                        value = inputRegion,
                        onValueChange = { inputRegion = it },
                        label = { Text("Région") },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth().testTag("profile_input_region")
                    )

                    OutlinedTextField(
                        value = inputObjective,
                        onValueChange = { inputObjective = it },
                        label = { Text("Objectif prioritaire de la campagne") },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth().testTag("profile_input_objective")
                    )

                    OutlinedTextField(
                        value = inputExperience,
                        onValueChange = { inputExperience = it },
                        label = { Text("Expérience (Débutant, Intermédiaire, Expérimenté...)") },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth().testTag("profile_input_experience")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateFullUserProfile(
                            name = inputName.trim(),
                            phone = inputPhone.trim(),
                            region = inputRegion.trim(),
                            communeOrVillage = inputVillage.trim(),
                            farmObjective = inputObjective.trim(),
                            experienceLevel = inputExperience.trim()
                        )
                        showEditProfileDialog = false
                        Toast.makeText(context, "Profil agricole mis à jour avec succès !", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    modifier = Modifier.testTag("profile_save_button")
                ) {
                    Text("Enregistrer", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Annuler", color = Color(0xFF64748B))
                }
            }
        )
    }

    // Language Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    text = "Sélectionnez la langue",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            },
            text = {
                Column {
                    languages.forEach { (code, name, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateLanguage(code)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentLang == code,
                                onClick = {
                                    viewModel.updateLanguage(code)
                                    showLanguageDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = ForestGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                                Text(text = desc, fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("Fermer", color = ForestGreen) }
            }
        )
    }

    // Region Dialog
    if (showRegionDialog) {
        AlertDialog(
            onDismissRequest = { showRegionDialog = false },
            title = {
                Text(
                    text = "Votre Région Agroécologique",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            },
            text = {
                LazyColumn(modifier = Modifier.height(280.dp)) {
                    items(regions) { reg ->
                        val isSelected = userProfile?.region == reg
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateRegion(reg)
                                    showRegionDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    viewModel.updateRegion(reg)
                                    showRegionDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = ForestGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = reg,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRegionDialog = false }) { Text("Fermer", color = ForestGreen) }
            }
        )
    }
}

@Composable
private fun ProfileDetailRow(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF0F172A), fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A)
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
            )
        }
    }
}
