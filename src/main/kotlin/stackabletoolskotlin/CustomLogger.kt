package stackabletoolskotlin

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import stackabletoolskotlin.config.ConfigManager

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
        log("INFO", message)
    }

    /**
     * Log un message WARNING
     */
    fun warn(message: String) {
        log("WARN", message)
    }

    /**
     * Log un message ERROR
     */
    fun error(message: String) {
        log("ERROR", message)
    }

    /**
     * Log un message DEBUG
     */
    fun debug(message: String) {
        log("DEBUG", message)
    }

    private fun log(level: String, message: String) {
        val config = ConfigManager.getConfig()
        if (!config.enableLogging) return

        // On ne log que si on est sur le thread serveur pour éviter les doublons client/serveur
        val isServerThread = Thread.currentThread().name.contains("Server thread", ignoreCase = true)

        val timestamp = LocalDateTime.now().format(formatter)
        val formattedMessage = "[$level][$timestamp] $message"

        // Console (si serveur ou si autorisé)
        if (config.logInConsole && isServerThread) {
            println(formattedMessage.toAscii())
        }

        // Fichier (uniquement via serveur pour éviter les conflits d'accès)
        if (config.logInFile && isServerThread) {
            writeToFile(formattedMessage)
        }
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