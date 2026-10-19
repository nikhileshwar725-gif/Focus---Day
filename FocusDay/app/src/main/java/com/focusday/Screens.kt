package com.focusday

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private fun appLabel(pkg: String) = if (pkg.contains("instagram")) "Instagram" else "YouTube"

private fun Any.goHome(ctx: ComponentActivity) {
    ctx.startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME))
}

/** Soft gate outside the focus window: 10 second wait, then optional 10-minute pass. */
class PauseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pkg = intent.getStringExtra("pkg") ?: return finish()
        setContent {
            MaterialTheme {
                var left by remember { mutableIntStateOf(10) }
                LaunchedEffect(Unit) {
                    while (left > 0) { delay(1000); left-- }
                }
                Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Why are you opening ${appLabel(pkg)}?", style = MaterialTheme.typography.headlineSmall)
                    Text(if (left > 0) "Wait $left s" else "You can continue.")
                    Button(
                        onClick = { Rules.allowFor(pkg, 10 * 60_000L); finish() },
                        enabled = left == 0
                    ) { Text("Continue (10 min)") }
                    TextButton(onClick = { finish(); goHome(this@PauseActivity) }) { Text("Close") }
                }
            }
        }
    }
}

/** Hard gate inside the focus window: no continue button. */
class BlockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pkg = intent.getStringExtra("pkg") ?: return finish()
        setContent {
            MaterialTheme {
                Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("${appLabel(pkg)} is blocked", style = MaterialTheme.typography.headlineSmall)
                    Text("Focus window: 9:00 to 13:00. Go back to your checklist.")
                    TextButton(onClick = { finish(); goHome(this@BlockActivity) }) { Text("OK") }
                }
            }
        }
    }
}
