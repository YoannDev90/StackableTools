package stackabletoolskotlin

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Logger personnalisé qui écrit les messages à la fois dans la console et dans un fichier
 */
object CustomLogger {
    private const val LOG_FILE_PATH = "logs/stackabletoolskotlin.log"
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    /**
     * Log un message INFO
     */
    fun info(message: String) {
        val timestamp = LocalDateTime.now().format(formatter)
        val logMessage = "[INFO][$timestamp] $message"
        // Console en ASCII (remplacement accents/non-ASCII)
        println(logMessage.toAscii())
        // Fichier en UTF-8
        writeToFile(logMessage)
    }

    /**
     * Log un message WARNING
     */
    fun warn(message: String) {
        val timestamp = LocalDateTime.now().format(formatter)
        val logMessage = "[WARN][$timestamp] $message"
        println(logMessage.toAscii())
        writeToFile(logMessage)
    }

    /**
     * Log un message ERROR
     */
    fun error(message: String) {
        val timestamp = LocalDateTime.now().format(formatter)
        val logMessage = "[ERROR][$timestamp] $message"
        println(logMessage.toAscii())
        writeToFile(logMessage)
    }

    /**
     * Log un message DEBUG
     */
    fun debug(message: String) {
        val timestamp = LocalDateTime.now().format(formatter)
        val logMessage = "[DEBUG][$timestamp] $message"
        println(logMessage.toAscii())
        writeToFile(logMessage)
    }

    /**
     * Écrit le message dans le fichier de log
     */
    private fun writeToFile(message: String) {
        try {
            val logDir = File(LOG_FILE_PATH).parentFile
            if (!logDir.exists()) {
                logDir.mkdirs()
            }
            File(LOG_FILE_PATH).appendText("$message\n", Charsets.UTF_8)
        } catch (e: Exception) {
            println("[ERROR] Impossible d'écrire dans le fichier de log: ${e.message}")
        }
    }

    // Extension pour transformer une chaîne en ASCII (remplacement accents/non-ASCII)
    private fun String.toAscii(): String {
        val normalized = java.text.Normalizer.normalize(this, java.text.Normalizer.Form.NFD)
        // Double backslash pour Kotlin
        return normalized.replace("[^\\p{ASCII}]".toRegex(), "?")
    }
}