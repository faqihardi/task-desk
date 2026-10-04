package com.klmpk9.taskdesk.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager untuk identitas unik device (tanpa login).
 *
 * Setiap device yang menginstall aplikasi mendapat UUID acak yang
 * disimpan permanen di EncryptedSharedPreferences. UUID ini dipakai
 * sebagai `requesterId` untuk memfilter tiket milik user ini.
 *
 * Data yang disimpan:
 * - requesterId: UUID unik per device
 * - requesterName: Nama user (auto-fill di form berikutnya)
 * - department: Departemen user (auto-fill di form berikutnya)
 *
 * EncryptedSharedPreferences memastikan data tidak mudah dibaca
 * oleh aplikasi lain atau dari rooted device.
 */
@Singleton
class DeviceIdentityManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Ambil requesterId. Jika belum ada, generate UUID baru.
     */
    fun getRequesterId(): String {
        return prefs.getString(KEY_REQUESTER_ID, null)
            ?: UUID.randomUUID().toString().also { newId ->
                prefs.edit().putString(KEY_REQUESTER_ID, newId).apply()
            }
    }

    /**
     * Ambil nama yang pernah di-input user sebelumnya.
     * Return null jika belum pernah diisi.
     */
    fun getSavedName(): String? {
        return prefs.getString(KEY_REQUESTER_NAME, null)
    }

    /**
     * Simpan nama user setelah submit tiket (untuk auto-fill).
     */
    fun saveName(name: String) {
        prefs.edit().putString(KEY_REQUESTER_NAME, name).apply()
    }

    /**
     * Ambil departemen yang pernah dipilih user sebelumnya.
     * Return null jika belum pernah dipilih.
     */
    fun getSavedDepartment(): String? {
        return prefs.getString(KEY_DEPARTMENT, null)
    }

    /**
     * Simpan departemen setelah submit tiket (untuk auto-fill).
     */
    fun saveDepartment(department: String) {
        prefs.edit().putString(KEY_DEPARTMENT, department).apply()
    }

    companion object {
        private const val PREFS_FILE_NAME = "taskdesk_identity_prefs"
        private const val KEY_REQUESTER_ID = "requester_id"
        private const val KEY_REQUESTER_NAME = "requester_name"
        private const val KEY_DEPARTMENT = "department"
    }
}