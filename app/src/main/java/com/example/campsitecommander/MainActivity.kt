package com.example.campsitecommander

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campsitecommander.ui.theme.CampsiteCommanderTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Screen enum defining the different navigation destinations within the app.
 */
enum class Screen {
    Splash, Home, AddItem, Checklist, Detail
}

/**
 * MainActivity: The entry point of the application.
 * It sets up the Compose content and enables edge-to-edge display.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enables edge-to-edge layout for a modern look
        enableEdgeToEdge()
        setContent {
            // Apply the custom theme defined in ui/theme
            CampsiteCommanderTheme(darkTheme = true) {
                CampsiteApp()
            }
        }
    }
}

/**
 * CampsiteApp: The root Composable that manages global state and navigation.
 * Uses SnapshotStateLists to allow reactive updates to the UI when items are modified.
 */
@Composable
fun CampsiteApp() {
    // State management for inventory items using parallel SnapshotStateLists
    val itemNames = remember { 
        mutableStateListOf("Mountain Tent", "Sleeping Bag", "Trail Mix", "First Aid Kit", "Water Purifier") 
    }
    val itemCategories = remember { 
        mutableStateListOf("Gear", "Gear", "Food", "Gear", "Gear") 
    }
    val itemQuantities = remember { 
        mutableStateListOf("1", "2", "3", "1", "1") 
    }
    val itemComments = remember { 
        mutableStateListOf("Check for rainfly", "Rated for -5°C", "High protein mix", "Check expiration dates", "Extra filters included") 
    }
    val itemChecked = remember { 
        mutableStateListOf(false, false, false, false, false) 
    }

    // Navigation state
    var currentScreen by remember { mutableStateOf(Screen.Splash) }

    /**
     * Helper function to add a new item to the inventory lists.
     */
    fun addItem(name: String, cat: String, qty: String, comm: String, isChecked: Boolean = false) {
        itemNames.add(name)
        itemCategories.add(cat)
        itemQuantities.add(qty)
        itemComments.add(comm)
        itemChecked.add(isChecked)
    }

    // Crossfade provides a smooth transition between different screens
    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            Screen.Splash -> {
                SplashScreen { currentScreen = Screen.Home }
            }
            else -> {
                MainContent(
                    screen, 
                    itemNames, 
                    itemCategories, 
                    itemQuantities,
                    itemComments,
                    itemChecked,
                    onAddItem = { n, c, q, cm -> addItem(n, c, q, cm) },
                    onScreenChange = { currentScreen = it },
                    onUndoDelete = { n, c, q, cm, ch -> addItem(n, c, q, cm, ch) }
                )
            }
        }
    }
}

/**
 * SplashScreen: Displayed for 2 seconds on app launch.
 * Shows branding and a loading indicator.
 */
@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // LaunchedEffect triggers once when this Composable enters the composition
    LaunchedEffect(Unit) {
        delay(2000) // 2-second delay
        onTimeout()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Terrain,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Campsite Commander",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator()
        }
    }
}

/**
 * MainContent: Manages the layout scaffolding including the Bottom Navigation Bar
 * and the content of the current screen.
 */
@Composable
fun MainContent(
    screen: Screen,
    itemNames: SnapshotStateList<String>,
    itemCategories: SnapshotStateList<String>,
    itemQuantities: SnapshotStateList<String>,
    itemComments: SnapshotStateList<String>,
    itemChecked: SnapshotStateList<Boolean>,
    onAddItem: (String, String, String, String) -> Unit,
    onScreenChange: (Screen) -> Unit,
    onUndoDelete: (String, String, String, String, Boolean) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Hide bottom bar on Splash and Detail screens for better focus
            if (screen != Screen.Detail && screen != Screen.Splash) {
                NavigationBar {
                    NavigationBarItem(
                        selected = screen == Screen.Home,
                        onClick = { onScreenChange(Screen.Home) },
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = screen == Screen.AddItem,
                        onClick = { onScreenChange(Screen.AddItem) },
                        icon = { Icon(Icons.Default.Add, null) },
                        label = { Text("Add") }
                    )
                    NavigationBarItem(
                        selected = screen == Screen.Checklist,
                        onClick = { onScreenChange(Screen.Checklist) },
                        icon = { Icon(Icons.Default.List, null) },
                        label = { Text("Checklist") }
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (screen) {
                Screen.Home -> HomeScreen(
                    names = itemNames, 
                    checked = itemChecked, 
                    onScreenChange = onScreenChange,
                    onReset = {
                        itemNames.clear()
                        itemCategories.clear()
                        itemQuantities.clear()
                        itemComments.clear()
                        itemChecked.clear()
                        scope.launch { snackbarHostState.showSnackbar("Mission Manifest Wiped") }
                    },
                    onShowMessage = { scope.launch { snackbarHostState.showSnackbar(it) } }
                )
                Screen.AddItem -> AddItemScreen(
                    onAddItem = { n, c, q, cm -> 
                        onAddItem(n, c, q, cm)
                        scope.launch { 
                            val result = snackbarHostState.showSnackbar(
                                message = "Enlisted: $n",
                                actionLabel = "View",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                onScreenChange(Screen.Checklist)
                            }
                        }
                    }, 
                    onNavigate = onScreenChange,
                    currentCount = itemNames.size,
                    onShowMessage = { scope.launch { snackbarHostState.showSnackbar(it) } }
                )
                Screen.Checklist -> ChecklistScreen(
                    itemNames, 
                    itemCategories, 
                    itemQuantities, 
                    itemComments, 
                    itemChecked, 
                    onViewDetails = { onScreenChange(Screen.Detail) }, 
                    onNavigate = onScreenChange,
                    onShowMessage = { scope.launch { snackbarHostState.showSnackbar(it) } },
                    onUndoDelete = { n, c, q, cm, ch ->
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Discharged: $n",
                                actionLabel = "Undo",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                onUndoDelete(n, c, q, cm, ch)
                            }
                        }
                    }
                )
                Screen.Detail -> DetailScreen(itemNames, itemCategories, itemQuantities, itemComments) { onScreenChange(Screen.Home) }
                else -> {}
            }
        }
    }
}

/**
 * HomeScreen: Dashboard showing mission readiness (progress) and primary actions.
 */
@Composable
fun HomeScreen(
    names: SnapshotStateList<String>,
    checked: SnapshotStateList<Boolean>,
    onScreenChange: (Screen) -> Unit,
    onReset: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }

    // Confirmation dialog before clearing all data
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Wipe Mission Manifest?") },
            text = { Text("This will permanently remove all enlisted gear from your current deployment. Are you sure, Commander?") },
            confirmButton = {
                TextButton(onClick = {
                    onReset()
                    showResetDialog = false
                }) {
                    Text("YES, WIPE IT", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }

    // Calculate progress stats
    var packedCount = 0
    for (i in 0 until checked.size) {
        if (checked[i]) packedCount++
    }

    var totalItems = 0
    for (i in 0 until names.size) {
        totalItems++
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Commander Dashboard", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Mission Readiness", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "$packedCount / $totalItems",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Items Prepared", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { onScreenChange(Screen.AddItem) },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(Modifier.width(8.dp))
            Text("Enlist New Gear")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { 
                if (names.isEmpty()) {
                    onShowMessage("Commander, enlist gear before launching the checklist!")
                } else {
                    onScreenChange(Screen.Checklist) 
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Default.Assignment, null)
            Spacer(Modifier.width(8.dp))
            Text("Launch Mission Checklist")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { 
                if (names.isEmpty()) {
                    onShowMessage("Intelligence: No gear reported in current manifest.")
                } else {
                    onScreenChange(Screen.Detail) 
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Default.Visibility, null)
            Spacer(Modifier.width(8.dp))
            Text("Detailed Intelligence Report")
        }

        if (names.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(onClick = { showResetDialog = true }) {
                Icon(Icons.Default.DeleteSweep, null)
                Spacer(Modifier.width(8.dp))
                Text("Wipe Mission Manifest", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

/**
 * AddItemScreen: Form for entering new equipment details.
 * Includes validation for name, quantity, and capacity limits.
 */
@Composable
fun AddItemScreen(
    onAddItem: (String, String, String, String) -> Unit,
    onNavigate: (Screen) -> Unit,
    currentCount: Int,
    onShowMessage: (String) -> Unit
) {
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("1") }
    var itemComment by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Gear") } 
    val options = listOf("Gear", "Food")

    var nameError by remember { mutableStateOf<String?>(null) }
    var quantityError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onNavigate(Screen.Home) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Abort")
            }
            Text("Enlist Equipment", style = MaterialTheme.typography.headlineMedium)
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        // Name input with length validation
        OutlinedTextField(
            value = itemName,
            onValueChange = { 
                if (it.length <= 25) {
                    itemName = it
                    if (it.isNotBlank()) nameError = null 
                }
            },
            label = { Text("Equipment Name") },
            placeholder = { Text("e.g. Tactical Flashlight") },
            modifier = Modifier.fillMaxWidth(),
            isError = nameError != null,
            supportingText = { 
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    if (nameError != null) Text(nameError!!)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${itemName.length}/25")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Quantity input: Numeric only
        OutlinedTextField(
            value = itemQuantity,
            onValueChange = { 
                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                    itemQuantity = it
                    if (it.isNotEmpty() && it.toIntOrNull() != null && it.toInt() > 0) {
                        quantityError = null
                    }
                }
            },
            label = { Text("Quantity") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            isError = quantityError != null,
            supportingText = { quantityError?.let { Text(it) } }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Comments/Tactical Notes input
        OutlinedTextField(
            value = itemComment,
            onValueChange = { 
                if (it.length <= 100) {
                    itemComment = it 
                }
            },
            label = { Text("Tactical Notes") },
            placeholder = { Text("Extra intel (optional)") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text("${itemComment.length}/100")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category selection using RadioButtons
        Text("Classification:", style = MaterialTheme.typography.titleSmall)
        Row {
            options.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                    RadioButton(selected = (category == option), onClick = { category = option })
                    Text(option)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                var hasError = false
                // Validation Logic
                if (itemName.isBlank()) {
                    nameError = "Commander, gear needs a name!"
                    hasError = true
                }
                
                if (itemQuantity.isBlank()) {
                    quantityError = "Manifest requires a quantity count, Commander."
                    hasError = true
                } else {
                    val qty = itemQuantity.toIntOrNull()
                    if (qty == null) {
                        quantityError = "Quantity is too large for deployment!"
                        hasError = true
                    } else if (qty <= 0) {
                        quantityError = "Units must be at least 1 for deployment, Commander."
                        hasError = true
                    }
                }

                // Global capacity check (max 25 items)
                if (currentCount >= 25 && !hasError) {
                    onShowMessage("Commander, manifest is at maximum capacity (25 units)!")
                    hasError = true
                }

                if (!hasError) {
                    onAddItem(itemName, category, itemQuantity, itemComment)
                    onNavigate(Screen.Checklist)
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Commit to Base")
        }
    }
}

/**
 * ChecklistScreen: Displays the list of gear with search and filtering capabilities.
 * Allows users to mark items as "packed" and remove items with an undo option.
 */
@Composable
fun ChecklistScreen(
    names: SnapshotStateList<String>,
    categories: SnapshotStateList<String>,
    quantities: SnapshotStateList<String>,
    comments: SnapshotStateList<String>,
    checked: SnapshotStateList<Boolean>,
    onViewDetails: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onShowMessage: (String) -> Unit,
    onUndoDelete: (String, String, String, String, Boolean) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val filters = listOf("All", "Gear", "Food")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onNavigate(Screen.Home) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("Tactical Checklist", style = MaterialTheme.typography.headlineMedium)
            }
            IconButton(onClick = onViewDetails) {
                Icon(Icons.Default.Info, contentDescription = "Intel Report")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar for filtering items by name
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search inventory...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips for category filtering
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (names.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No gear enlisted. Report to Add Gear.")
            }
        } else {
            // LazyColumn for efficient list rendering
            LazyColumn(modifier = Modifier.weight(1f)) {
                // Apply search and category filters to indices
                val filteredIndices = names.indices.filter { i ->
                    (selectedFilter == "All" || categories[i] == selectedFilter) &&
                    (names[i].contains(searchQuery.trim(), ignoreCase = true))
                }

                if (filteredIndices.isEmpty() && searchQuery.isNotEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No intelligence found for \"$searchQuery\"", color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }

                items(filteredIndices) { index ->
                    ListItem(
                        headlineContent = { Text(names[index], fontWeight = FontWeight.Bold) },
                        supportingContent = { 
                            Text("${categories[index]} | Units: ${quantities[index]}") 
                        },
                        leadingContent = {
                            Checkbox(
                                checked = checked[index],
                                onCheckedChange = { checked[index] = it }
                            )
                        },
                        trailingContent = {
                            IconButton(onClick = {
                                // Capture data before removal to support Undo
                                val removedName = names[index]
                                val removedCat = categories[index]
                                val removedQty = quantities[index]
                                val removedComm = comments[index]
                                val removedChecked = checked[index]

                                names.removeAt(index)
                                categories.removeAt(index)
                                quantities.removeAt(index)
                                comments.removeAt(index)
                                checked.removeAt(index)
                                
                                onUndoDelete(removedName, removedCat, removedQty, removedComm, removedChecked)
                            }) {
                                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

/**
 * DetailScreen: Shows comprehensive information for all items in the manifest.
 * Includes "Command Tips" based on category.
 */
@Composable
fun DetailScreen(
    names: SnapshotStateList<String>,
    categories: SnapshotStateList<String>,
    quantities: SnapshotStateList<String>,
    comments: SnapshotStateList<String>,
    onBackToBase: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackToBase) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Return")
            }
            Text("Intelligence Report", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (names.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("Intelligence: Manifest is empty.")
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(names.size) { index ->
                    // Dynamic tips based on the item category
                    val tip = when(categories[index]) {
                        "Gear" -> "COMMAND TIP: Double-check fasteners and batteries before deployment."
                        "Food" -> "COMMAND TIP: Keep all supplies sealed to deter local wildlife."
                        else -> "COMMAND TIP: Stay vigilant and keep gear organized."
                    }
                    val icon = if(categories[index] == "Gear") Icons.Default.Handyman else Icons.Default.Restaurant

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                                Spacer(Modifier.width(12.dp))
                                Text(text = names[index], style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                            
                            DetailRow(label = "Classification", value = categories[index])
                            DetailRow(label = "Deployment Units", value = quantities[index])
                            
                            if (comments[index].isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "Special Intel:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                Text(text = comments[index], style = MaterialTheme.typography.bodyMedium)
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Highlighted Tip section
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.TipsAndUpdates, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = tip,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBackToBase,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Default.Home, null)
            Spacer(Modifier.width(8.dp))
            Text("Return to Command Base", fontSize = 18.sp)
        }
    }
}

/**
 * Reusable component for displaying a labeled value in details.
 */
@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "$label:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}
