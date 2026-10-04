package com.klmpk9.taskdesk.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.klmpk9.taskdesk.ui.theme.DoneContainerDark
import com.klmpk9.taskdesk.ui.theme.DoneContainerLight
import com.klmpk9.taskdesk.ui.theme.InProgressContainerDark
import com.klmpk9.taskdesk.ui.theme.InProgressContainerLight
import com.klmpk9.taskdesk.ui.theme.OnDoneContainerDark
import com.klmpk9.taskdesk.ui.theme.OnDoneContainerLight
import com.klmpk9.taskdesk.ui.theme.OnInProgressContainerDark
import com.klmpk9.taskdesk.ui.theme.OnInProgressContainerLight
import com.klmpk9.taskdesk.ui.theme.OnPendingContainerDark
import com.klmpk9.taskdesk.ui.theme.OnPendingContainerLight
import com.klmpk9.taskdesk.ui.theme.OnRejectedContainerDark
import com.klmpk9.taskdesk.ui.theme.OnRejectedContainerLight
import com.klmpk9.taskdesk.ui.theme.PendingContainerDark
import com.klmpk9.taskdesk.ui.theme.PendingContainerLight
import com.klmpk9.taskdesk.ui.theme.RejectedContainerDark
import com.klmpk9.taskdesk.ui.theme.RejectedContainerLight


enum class TicketStatus(
    val displayName: String,
    val containerColorLight: Color,
    val onContainerColorLight: Color,
    val containerColorDark: Color,
    val onContainerColorDark: Color,
    val icon: ImageVector
) {
    PENDING(
        displayName = "Pending",
        containerColorLight = PendingContainerLight,
        onContainerColorLight = OnPendingContainerLight,
        containerColorDark = PendingContainerDark,
        onContainerColorDark = OnPendingContainerDark,
        icon = Icons.Filled.HourglassEmpty
    ),
    IN_PROGRESS(
        displayName = "In Progress",
        containerColorLight = InProgressContainerLight,
        onContainerColorLight = OnInProgressContainerLight,
        containerColorDark = InProgressContainerDark,
        onContainerColorDark = OnInProgressContainerDark,
        icon = Icons.Filled.PlayCircle
    ),
    DONE(
        displayName = "Done",
        containerColorLight = DoneContainerLight,
        onContainerColorLight = OnDoneContainerLight,
        containerColorDark = DoneContainerDark,
        onContainerColorDark = OnDoneContainerDark,
        icon = Icons.Filled.CheckCircle
    ),
    REJECTED(
        displayName = "Rejected",
        containerColorLight = RejectedContainerLight,
        onContainerColorLight = OnRejectedContainerLight,
        containerColorDark = RejectedContainerDark,
        onContainerColorDark = OnRejectedContainerDark,
        icon = Icons.Filled.Close
    );

    companion object {
        fun fromString(status: String): TicketStatus {
            return when (status.lowercase()) {
                "pending" -> PENDING
                "in_progress", "in progress", "inprogress" -> IN_PROGRESS
                "done", "completed" -> DONE
                "rejected", "declined" -> REJECTED
                else -> PENDING
            }
        }
    }
}

/**
 * Komponen badge untuk menampilkan status tiket.
 * @param status Enum TicketStatus yang akan ditampilkan
 * @param modifier Modifier untuk styling tambahan
 */
@Composable
fun StatusBadge(
    status: TicketStatus,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = MaterialTheme.colorScheme.background.red < 0.5f
    val containerColor = if (isDarkTheme) status.containerColorDark else status.containerColorLight
    val onContainerColor = if (isDarkTheme) status.onContainerColorDark else status.onContainerColorLight

    Row(
        modifier = modifier
            .semantics{
                contentDescription = "Status: ${status.displayName}"
            }
            .background(
                color = containerColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = status.icon,
            contentDescription = null,
            tint = onContainerColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = onContainerColor
        )
    }
}