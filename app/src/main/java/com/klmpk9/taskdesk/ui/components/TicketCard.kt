package com.klmpk9.taskdesk.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.klmpk9.taskdesk.util.isMockApiPresent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Card untuk menampilkan satu tiket dalam daftar.
 * @param title Judul tiket
 * @brief Brief/deskripsi singkat
 * @param status String status dari API (akan dikonversi ke TicketStatus)
 * @param createdAtTimestamp Timestamp pembuatan (milliseconds)
 * @param onClick Callback saat card diklik
 * @param modifier Modifier untuk styling tambahan
 */
@Composable
fun TicketCard(
    title: String,
    brief: String,
    status: String,
    ticketCode: String,
    requesterName: String,
    priority: String,
    adminReply: String? = null,
    createdAtTimestamp: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ticketStatus = TicketStatus.fromString(status)
    val formattedDate = formatDate(createdAtTimestamp)
    val priorityColor = getPriorityColor(priority)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // Header: Ticket Code + Priority indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = ticketCode,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                // ikon balasan + prioritas
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (adminReply.isMockApiPresent("adminReply")) {
                        Icon(
                            imageVector = Icons.Filled.QuestionAnswer,
                            contentDescription = "Ada balasan dari IT Helpdesk",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    PriorityIndicator(priority = priority, color = priorityColor)
                }

//                PriorityIndicator(priority = priority, color = priorityColor)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Header: Judul + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Judul (flex 1)
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Status Badge
                StatusBadge(status = ticketStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Brief/Deskripsi
            Text(
                text = brief,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Nama + Tanggal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nama pengaju
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = requesterName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Tanggal
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Circle indicator untuk prioritas dengan warna.
 */
@Composable
private fun PriorityIndicator(priority: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = priority,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Mapping prioritas ke warna.
 */
private fun getPriorityColor(priority: String): Color {
    return when (priority.lowercase()) {
        "low" -> Color(0xFF4CAF50)      // Hijau
        "medium" -> Color(0xFFFF9800)   // Oranye
        "high" -> Color(0xFFF44336)     // Merah
        else -> Color(0xFF9E9E9E)       // Abu-abu
    }
}

/**
 * Helper function untuk format timestamp ke string tanggal.
 */
private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}