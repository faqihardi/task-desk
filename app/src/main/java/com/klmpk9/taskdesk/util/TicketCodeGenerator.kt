package com.klmpk9.taskdesk.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 * Generator kode unik tiket.
 *
 * Format: TD-[DEPT_CODE]-[YYYYMMDD]-[3-digit random]
 * Contoh: TD-IT-20260105-847
 *         TD-MAR-20260105-123
 *         TD-HR-20260105-555
 *
 * Kode ini:
 * - Unik per tiket (kombinasi dept + tanggal + random)
 * - Mudah dibaca & diketik oleh IT Helpdesk
 * - Mengandung informasi departemen untuk filtering
 */
object TicketCodeGenerator {

    fun generate(department: String): String {
        val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val deptCode = department
            .replace(Regex("[^A-Za-z0-9]"), "")
            .take(3)
            .uppercase()
            .ifEmpty { "GEN" }
        val random = Random.nextInt(100, 1000)
        return "TD-$deptCode-$date-$random"
    }
}