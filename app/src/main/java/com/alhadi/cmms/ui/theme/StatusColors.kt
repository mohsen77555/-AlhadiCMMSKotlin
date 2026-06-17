package com.alhadi.cmms.ui.theme

import androidx.compose.ui.graphics.Color
import java.util.Locale

/** A pair of (content color, container/background color) for a semantic badge. */
data class StatusTone(val content: Color, val container: Color)

/** Maps an asset / work-order status string to a semantic color tone. */
fun statusTone(status: String): StatusTone {
    return when (status.lowercase(Locale.US)) {
        "running", "closed", "done", "completed", "ok", "active" ->
            StatusTone(StatusRunning, StatusRunningContainer)
        "technically completed" ->
            StatusTone(StatusInfo, StatusInfoContainer)
        "warning", "in progress", "scheduled", "pending", "due" ->
            StatusTone(StatusWarning, StatusWarningContainer)
        "stopped", "overdue", "failed", "critical" ->
            StatusTone(StatusStopped, StatusStoppedContainer)
        "open", "new" ->
            StatusTone(StatusInfo, StatusInfoContainer)
        else ->
            StatusTone(StatusNeutral, StatusNeutralContainer)
    }
}

/** Maps a priority string to a semantic color tone. */
fun priorityTone(priority: String): StatusTone {
    return when (priority.lowercase(Locale.US)) {
        "critical", "urgent" -> StatusTone(StatusStopped, StatusStoppedContainer)
        "high" -> StatusTone(StatusWarning, StatusWarningContainer)
        "medium" -> StatusTone(StatusInfo, StatusInfoContainer)
        "low" -> StatusTone(StatusRunning, StatusRunningContainer)
        else -> StatusTone(StatusNeutral, StatusNeutralContainer)
    }
}
