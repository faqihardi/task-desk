package com.klmpk9.taskdesk.ui.screens.create

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.runtime.mutableStateOf
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
import com.klmpk9.taskdesk.ui.components.ConfirmDialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

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

    // Focus requesters
    val titleFocusRequester = remember { FocusRequester() }
    val briefFocusRequester = remember { FocusRequester() }
    val nameFocusRequester = remember { FocusRequester() }
    val driveLinkFocusRequester = remember { FocusRequester() }


    // State untuk dialog unsaved changes
    var showUnsavedChangesDialog by remember { mutableStateOf(false) }

    var expandedDepartment by remember { mutableStateOf(false) }
    var expandedPriority by remember { mutableStateOf(false) }

    // Deteksi apakah ada perubahan yang belum disimpan
    val hasUnsavedChanges = uiState.title.isNotBlank() ||
            uiState.brief.isNotBlank() ||
            uiState.driveLink.isNotBlank()

    // === Intercept system back button ===
    BackHandler (enabled = hasUnsavedChanges && !uiState.isSubmitSuccess) {
        showUnsavedChangesDialog = true
    }

    // === Dialog konfirmasi unsaved changes ===
    if (showUnsavedChangesDialog) {
        ConfirmDialog(
            title = "Perubahan Belum Disimpan",
            message = "Data yang kamu masukkan akan hilang jika keluar dari halaman ini. Yakin ingin keluar?",
            confirmText = "Keluar",
            dismissText = "Tetap di Sini",
            onConfirm = {
                showUnsavedChangesDialog = false
                navController.popBackStack()
            },
            onDismiss = {
                showUnsavedChangesDialog = false
            }
        )
    }

    // === Auto navigate back setelah submit sukses ===
    LaunchedEffect(uiState.isSubmitSuccess) {
        if (uiState.isSubmitSuccess) {
            // Signal HomeScreen untuk refresh daftar tiket
            navController.previousBackStackEntry?.savedStateHandle?.apply {
                set("refreshTickets", true)
                set("snackbarMessage", "Tiket berhasil dibuat ✓")
            }

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
                    IconButton(
                        onClick = {
                            if (hasUnsavedChanges) {
                                showUnsavedChangesDialog = true
                            } else {
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.semantics {
                            contentDescription = "Kembali"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // === Field: Nama ===
            OutlinedTextField(
                value = uiState.requesterName,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Nama") },
                placeholder = { Text("Masukkan nama Anda") },
                singleLine = true,
                enabled = !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(nameFocusRequester)
                    .semantics { contentDescription = "Nama pengaju tiket" },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { titleFocusRequester.requestFocus() }
                )
            )

            // === Field: Judul Tiket ===
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text("Judul Tiket") },
                placeholder = { Text("Contoh: Komputer Rusak") },
                supportingText = { Text("Judul singkat untuk problem Anda") },
                singleLine = true,
                enabled = !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(titleFocusRequester)
                    .semantics { contentDescription = "Judul tiket, wajib diisi" },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { briefFocusRequester.requestFocus() }
                )
            )

            // === Dropdown: Departemen ===
            ExposedDropdownMenuBox(
                expanded = expandedDepartment,
                onExpandedChange = { expandedDepartment = it}
            ) {
                OutlinedTextField(
                    value = uiState.department,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Departemen") },
                    placeholder = { Text("Pilih departemen") },
                    supportingText = { Text("Departemen asal pengajuan") },
                    trailingIcon = {
//                        Icon(
//                            imageVector = Icons.Filled.KeyboardArrowDown,
//                            contentDescription = null
//                        )
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedDepartment
                        )
                    },
                    enabled = !uiState.isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                )

                ExposedDropdownMenu(
                    expanded = expandedDepartment,
                    onDismissRequest = { expandedDepartment = false}
                ) {
                    uiState.departments.forEach { dept ->
                        DropdownMenuItem(
                            text = { Text(dept) },
                            onClick = {
                                viewModel.onDepartmentChange(dept)
                                expandedDepartment = false
                            }
                        )
                    }
                }
            }// === Dropdown: Prioritas ===
            ExposedDropdownMenuBox(
                expanded = expandedPriority,
                onExpandedChange = { expandedPriority = it}
            ) {
                OutlinedTextField(
                    value = uiState.priority,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Prioritas") },
                    supportingText = { Text("Tingkat urgensi tiket") },
                    trailingIcon = {
//                        Icon(
//                            imageVector = Icons.Filled.KeyboardArrowDown,
//                            contentDescription = null
//                        )
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedPriority
                        )
                    },
                    enabled = !uiState.isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                )

                ExposedDropdownMenu(
                    expanded = expandedPriority,
                    onDismissRequest = { expandedPriority = false}
                ) {
                    uiState.priorities.forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(priority) },
                            onClick = {
                                viewModel.onPriorityChange(priority)
                                expandedPriority = false
                            }
                        )
                    }
                }
            }



            // === Field: Brief / Deskripsi ===
            OutlinedTextField(
                value = uiState.brief,
                onValueChange = { viewModel.onBriefChange(it) },
                label = { Text("Brief / Deskripsi") },
                placeholder = { Text("Jelaskan spesifikasi dan kebutuhan Anda...") },
                minLines = 4,
                maxLines = 6,
                enabled = !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(briefFocusRequester)
                    .semantics { contentDescription = "Brief atau deskripsi desain, wajib diisi" },
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
                label = { Text("Tautan Pendukung (Opsional)") },
                placeholder = { Text("https://drive.google.com/...") },
                supportingText = { Text("Tautan referensi atau pendukung") },
                singleLine = true,
                enabled = !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(driveLinkFocusRequester)
                    .semantics { contentDescription = "Link Pendukung, opsional" },
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
                    .semantics { contentDescription = "Kirim tiket baru" }
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