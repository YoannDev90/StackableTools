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
        
        // Écriture dans la console
        println(logMessage)
        
        // Écriture dans le fichier
        writeToFile(logMessage)
    }

    /**
     * Log un message WARNING
     */
    fun warn(message: String) {
        val timestamp = LocalDateTime.now().format(formatter)
        val logMessage = "[WARN][$timestamp] $message"
        
        // Écriture dans la console
        println(logMessage)
        
        // Écriture dans le fichier
        writeToFile(logMessage)
    }

    /**
     * Log un message ERROR
     */
    fun error(message: String) {
        val timestamp = LocalDateTime.now().format(formatter)
        val logMessage = "[ERROR][$timestamp] $message"
        
        // Écriture dans la console
        println(logMessage)
        
        // Écriture dans le fichier
        writeToFile(logMessage)
    }

    /**
     * Log un message DEBUG
     */
    fun debug(message: String) {
        val timestamp = LocalDateTime.now().format(formatter)
        val logMessage = "[DEBUG][$timestamp] $message"
        
        // Écriture dans la console
        println(logMessage)
        
        // Écriture dans le fichier
        writeToFile(logMessage)
    }

    /**
     * Écrit le message dans le fichier de log
     */
    private fun writeToFile(message: String) {
        try {
            // Créer le répertoire logs s'il n'existe pas
            val logDir = File(LOG_FILE_PATH).parentFile
            if (!logDir.exists()) {
                logDir.mkdirs()
            }
            
            // Ajouter le message au fichier
            File(LOG_FILE_PATH).appendText("$message\n")
        } catch (e: Exception) {
            // En cas d'erreur d'écriture, afficher dans la console
            println("[ERROR] Impossible d'écrire dans le fichier de log: ${e.message}")
        }
    }
}