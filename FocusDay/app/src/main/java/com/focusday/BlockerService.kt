package com.focusday

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class BlockerService : AccessibilityService() {

    private var lastPkg: String? = null
    private var lastLaunchMs = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return
        if (pkg !in Rules.blocked) return
        if (Rules.isAllowed(pkg)) return

        // Debounce: one trigger per app open, not one per window event.
        val now = System.currentTimeMillis()
        if (pkg == lastPkg && now - lastLaunchMs < 3000) return
        lastPkg = pkg
        lastLaunchMs = now

        val target = if (Rules.inHardWindow()) BlockActivity::class.java else PauseActivity::class.java
        startActivity(
            Intent(this, target)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra("pkg", pkg)
        )
    }

    override fun onInterrupt() {}
}
