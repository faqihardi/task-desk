package com.klmpk9.taskdesk.ui.screens.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

/**
 * Create Screen - Form pengajuan tiket desain baru.
 *
 * Layout:
 * - TopAppBar dengan tombol back
 * - Form scrollable: Judul, Brief, Link Drive
 * - Tombol submit di bawah form
 * - Snackbar untuk error feedback
 * - Auto navigate back setelah submit sukses
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    navController: NavController,
    viewModel: CreateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Focus requesters untuk navigasi keyboard antar field
    val titleFocusRequester = remember { FocusRequester() }
    val briefFocusRequester = remember { FocusRequester() }
    val driveLinkFocusRequester = remember { FocusRequester() }

    // === Auto navigate back setelah submit sukses ===
    LaunchedEffect(uiState.isSubmitSuccess) {
        if (uiState.isSubmitSuccess) {
            // Signal HomeScreen untuk refresh daftar tiket
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("refreshTickets", true)

            // Navigate back ke HomeScreen
            navController.popBackStack()

            // Reset state agar tidak trigger navigasi ulang
            viewModel.onNavigationComplete()
        }
    }

    // === Tampilkan Snackbar saat ada error ===
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Buat Tiket Baru",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding() // Adjust saat keyboard muncul
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // === Field: Judul Tiket ===
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text("Judul Tiket *") },
                placeholder = { Text("Contoh: Redesign Landing Page") },
                supportingText = { Text("Judul singkat untuk tiket desain") },
                singleLine = true,
                enabled = !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(titleFocusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { briefFocusRequester.requestFocus() }
                )
            )

            // === Field: Brief / Deskripsi ===
            OutlinedTextField(
                value = uiState.brief,
                onValueChange = { viewModel.onBriefChange(it) },
                label = { Text("Brief / Deskripsi *") },
                placeholder = { Text("Jelaskan spesifikasi dan kebutuhan desain...") },
                supportingText = { Text("Detail ukuran, warna, referensi, dll.") },
                minLines = 4,
                maxLines = 6,
                enabled = !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(briefFocusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { driveLinkFocusRequester.requestFocus() }
                )
            )

            // === Field: Link Google Drive (Opsional) ===
            OutlinedTextField(
                value = uiState.driveLink,
                onValueChange = { viewModel.onDriveLinkChange(it) },
                label = { Text("Link Google Drive (Opsional)") },
                placeholder = { Text("https://drive.google.com/...") },
                supportingText = { Text("Tautan referensi atau file pendukung") },
                singleLine = true,
                enabled = !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(driveLinkFocusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { viewModel.submitTicket() }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // === Tombol Submit ===
            val isFormValid = uiState.title.isNotBlank() && uiState.brief.isNotBlank()

            Button(
                onClick = { viewModel.submitTicket() },
                enabled = isFormValid && !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mengirim...")
                } else {
                    Text(
                        text = "Kirim Tiket",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // Spacer bawah agar tombol tidak terlalu dekat dengan edge
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}