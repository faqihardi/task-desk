package com.klmpk9.taskdesk.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.klmpk9.taskdesk.ui.theme.TaskDeskTheme

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
private fun PreviewComponents() {
    TaskDeskTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Preview semua StatusBadge
            StatusBadge(status = TicketStatus.PENDING)
            Spacer(modifier = Modifier.height(8.dp))
            StatusBadge(status = TicketStatus.IN_PROGRESS)
            Spacer(modifier = Modifier.height(8.dp))
            StatusBadge(status = TicketStatus.DONE)
            Spacer(modifier = Modifier.height(8.dp))
            StatusBadge(status = TicketStatus.REJECTED)

            Spacer(modifier = Modifier.height(16.dp))

            // Preview TicketCard
            TicketCard(
                title = "Redesign Landing Page Medkominfo",
                brief = "Perlu redesign halaman utama dengan fokus pada UX yang lebih intuitif dan mobile-first approach.",
                status = "in_progress",
                createdAtTimestamp = System.currentTimeMillis(),
                onClick = {}
            )

            Spacer(modifier = Modifier.height(8.dp))

            TicketCard(
                title = "Buat Poster Event Campus Expo",
                brief = "Poster untuk event tahunan dengan tema 'Innovation for Future'.",
                status = "pending",
                createdAtTimestamp = System.currentTimeMillis() - 86400000, // 1 hari lalu
                onClick = {}
            )
        }
    }
}