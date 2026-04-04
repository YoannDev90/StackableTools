package stackabletools

import java.io.File
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import stackabletools.config.ConfigManager

/**
 * Logger personnalisé qui écrit les messages à la fois dans la console et dans un fichier.
 * Optimisé avec une file d'attente asynchrone et une gestion de rotation de fichiers.
 */
object CustomLogger {
    private const val LOG_FILE_PATH = "logs/stackabletools.log"
    private const val MAX_LOG_SIZE_MB = 5L
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    private val logQueue = ConcurrentLinkedQueue<String>()
    private val executor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "StackableTools-Logger-Thread").apply { isDaemon = true }
    }

    init {
        startLogWorker()
    }

    private fun startLogWorker() {
        executor.submit {
            while (true) {
                try {
                    val message = logQueue.poll()
                    if (message != null) {
                        processWriteToFile(message)
                    } else {
                        Thread.sleep(100)
                    }
                } catch (e: InterruptedException) {
                    break
                } catch (e: Exception) {
                    println("[CRITICAL] Error in logger worker: ${e.message}")
                }
            }
        }
    }

    /**
     * Log un message INFO
     */
    fun info(message: String) = log("INFO", message)

    /**
     * Log un message WARNING
     */
    fun warn(message: String) = log("WARN", message)

    /**
     * Log un message ERROR
     */
    fun error(message: String) = log("ERROR", message)

    /**
     * Log un message DEBUG
     */
    fun debug(message: String) = log("DEBUG", message)

    private fun log(level: String, message: String) {
        val config = try { ConfigManager.getConfig() } catch (e: Exception) { null }
        if (config != null && !config.logging.enable) return

        val isServerThread = Thread.currentThread().name.contains("Server thread", ignoreCase = true)
        val timestamp = LocalDateTime.now().format(formatter)
        val formattedMessage = "[$level][$timestamp] $message"

        if (config == null || config.logging.inConsole) {
            if (isServerThread || config == null) {
                println(formattedMessage.toAscii())
            }
        }

        if (isServerThread && (config == null || config.logging.inFile)) {
            logQueue.offer(formattedMessage)
        }
    }

    private fun processWriteToFile(message: String) {
        try {
            val logFile = File(LOG_FILE_PATH)
            val logDir = logFile.parentFile
            if (!logDir.exists()) logDir.mkdirs()

            if (logFile.exists() && logFile.length() > MAX_LOG_SIZE_MB * 1024 * 1024) {
                rotateLogFile(logFile)
            }

            logFile.appendText("$message\n", Charsets.UTF_8)
        } catch (e: Exception) {
            // Silently fail to avoid infinite recursion if println fails
        }
    }

    private fun rotateLogFile(logFile: File) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
        val rotatedFile = File("${LOG_FILE_PATH}.$timestamp.old")
        logFile.renameTo(rotatedFile)
    }

    private fun String.toAscii(): String {
        val normalized = java.text.Normalizer.normalize(this, java.text.Normalizer.Form.NFD)
        return normalized.replace("[^\\p{ASCII}]".toRegex(), "?")
    }
}