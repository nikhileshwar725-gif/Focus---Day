package com.focusday

import java.time.LocalTime

object Rules {
    // Apps to guard. Package names are exact.
    val blocked = setOf("com.instagram.android", "com.google.android.youtube")

    // Hard focus window, minutes from midnight. 09:00-13:00 by default.
    // TODO: make editable in Settings (PRD 4.4).
    private const val WINDOW_START = 9 * 60
    private const val WINDOW_END = 13 * 60

    private val allowUntil = mutableMapOf<String, Long>()

    fun inHardWindow(now: LocalTime = LocalTime.now()): Boolean {
        val m = now.hour * 60 + now.minute
        return m in WINDOW_START until WINDOW_END
    }

    fun allowFor(pkg: String, millis: Long) {
        allowUntil[pkg] = System.currentTimeMillis() + millis
    }

    fun isAllowed(pkg: String): Boolean =
        (allowUntil[pkg] ?: 0L) > System.currentTimeMillis()
}
