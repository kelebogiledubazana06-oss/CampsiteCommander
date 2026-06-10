package com.example.campsitecommander

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.campsitecommander.ui.theme.CampsiteCommanderTheme

// Enum for Screen Navigation
enum class Screen {
    AddItem, Checklist
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampsiteCommanderTheme {
                CampsiteApp()
            }
        }
    }
}

@Composable
fun CampsiteApp() {
    // Parallel Arrays (using SnapshotStateList for Compose reactivity)
    // requirement: parallel arrays
    val itemNames = remember { mutableStateListOf<String>() }
    val itemCategories = remember { mutableStateListOf<String>() }
    val itemChecked = remember { mutableStateListOf<Boolean>() }

    var currentScreen by remember { mutableStateOf(Screen.AddItem) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == Screen.AddItem,
                    onClick = { currentScreen = Screen.AddItem },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                    label = { Text("Add Item") }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.Checklist,
                    onClick = { currentScreen = Screen.Checklist },
                    icon = { Icon(Icons.Default.List, contentDescription = "Checklist") },
                    label = { Text("Checklist") }
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.AddItem -> AddItemScreen(itemNames, itemCategories, itemChecked)
                Screen.Checklist -> ChecklistScreen(itemNames, itemCategories, itemChecked)
            }
        }
    }
}

@Composable
fun AddItemScreen(
    names: SnapshotStateList<String>,
    categories: SnapshotStateList<String>,
    checked: SnapshotStateList<Boolean>
) {
    var itemName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Gear") } 
    val categoryOptions = listOf("Gear", "Food")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Campsite Commander", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Manage your gear and food", style = MaterialTheme.typography.bodyMedium)
        
        Spacer(modifier = Modifier.height(32.dp))

        // requirement: getting user input
        OutlinedTextField(
            value = itemName,
            onValueChange = { itemName = it },
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Category:", style = MaterialTheme.typography.titleSmall)
        Row {
            categoryOptions.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    RadioButton(
                        selected = (category == option),
                        onClick = { category = option }
                    )
                    Text(option)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (itemName.isNotBlank()) {
                    // Updating parallel arrays
                    names.add(itemName)
                    categories.add(category)
                    checked.add(false)
                    itemName = "" 
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add to List")
        }
    }
}

@Composable
fun ChecklistScreen(
    names: SnapshotStateList<String>,
    categories: SnapshotStateList<String>,
    checked: SnapshotStateList<Boolean>
) {
    // requirement: loops to manage the packing list
    // Explicit loop to calculate packed count
    var packedCount = 0
    for (i in 0 until checked.size) {
        if (checked[i]) {
            packedCount++
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Packing Checklist", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Packed $packedCount of ${names.size} items",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        if (names.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your list is empty!")
            }
        } else {
            LazyColumn {
                // itemsIndexed loop to display parallel data
                itemsIndexed(names) { index, name ->
                    ListItem(
                        headlineContent = { Text(name) },
                        supportingContent = { Text("Type: ${categories[index]}") },
                        trailingContent = {
                            Checkbox(
                                checked = checked[index],
                                onCheckedChange = { checked[index] = it }
                            )
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
