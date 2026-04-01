package stackabletoolskotlin

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import net.minecraft.server.MinecraftServer
import net.minecraft.text.Text
import stackabletoolskotlin.config.ConfigManager

/**
 * Logger personnalisé qui écrit les messages à la fois dans la console, dans un fichier et dans le chat
 */
object CustomLogger {
    private const val LOG_FILE_PATH = "logs/stackabletoolskotlin.log"
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private var server: MinecraftServer? = null

    /**
     * Définit l'instance du serveur pour les logs de chat
     */
    fun setServer(minecraftServer: MinecraftServer?) {
        server = minecraftServer
    }

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

        val timestamp = LocalDateTime.now().format(formatter)
        val formattedMessage = "[$level][$timestamp] $message"

        // Console
        if (config.logInConsole) {
            println(formattedMessage.toAscii())
        }

        // Fichier
        if (config.logInFile) {
            writeToFile(formattedMessage)
        }

        // Chat Minecraft
        if (config.logInChat) {
            writeToChat(formattedMessage)
        }
    }

    /**
     * Envoie le message à tous les joueurs en ligne
     */
    private fun writeToChat(message: String) {
        server?.playerManager?.playerList?.forEach { player ->
            player.sendMessage(Text.literal("§7[StackableTools]§r $message"), false)
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