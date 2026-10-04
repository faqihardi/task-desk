package com.klmpk9.taskdesk.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/**
 * Dialog konfirmasi reusable.
 * @param title       Judul dialog
 * @param message     Pesan/isi dialog
 * @param confirmText Teks tombol konfirmasi
 * @param dismissText Teks tombol batal
 * @param onConfirm   Callback saat tombol konfirmasi diklik
 * @param onDismiss   Callback saat tombol batal diklik atau dialog ditutup
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Ya",
    dismissText: String = "Tidak",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        },
        modifier = modifier.semantics {
            contentDescription = "Dialog konfirmasi: $title"
        }
    )
}