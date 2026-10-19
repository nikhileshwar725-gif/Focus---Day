package com.focusday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    private val db by lazy { AppDb.build(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { TodayScreen(db.taskDao()) } }
    }
}

@Composable
fun TodayScreen(dao: TaskDao) {
    val today = remember { LocalDate.now().toString() }
    val tasks by dao.forDate(today).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) { Text("+") }
        }
    ) { pad ->
        LazyColumn(Modifier.fillMaxSize().padding(pad).padding(16.dp)) {
            item { Text("Today", style = MaterialTheme.typography.headlineMedium) }
            if (tasks.isEmpty()) {
                item { Text("Nothing planned. Tap + to add one.") }
            }
            items(tasks, key = { it.id }) { t ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = t.done,
                        onCheckedChange = { c -> scope.launch { dao.setDone(t.id, c) } }
                    )
                    Text(listOfNotNull(t.startTime, t.title).joinToString("  "))
                }
            }
        }
    }

    if (showAdd) {
        AddTaskDialog(
            onDismiss = { showAdd = false },
            onSave = { title, time ->
                scope.launch { dao.insert(Task(title = title, date = today, startTime = time)) }
                showAdd = false
            }
        )
    }
}

@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onSave: (String, String?) -> Unit) {
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New task") },
        text = {
            Column {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Task") }, singleLine = true
                )
                OutlinedTextField(
                    value = time, onValueChange = { time = it },
                    label = { Text("Start HH:mm (optional)") }, singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank(),
                onClick = { onSave(title.trim(), time.trim().takeIf { it.isNotEmpty() }) }
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
