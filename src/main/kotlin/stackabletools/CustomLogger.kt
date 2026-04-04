package stackabletools

import java.io.File
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import stackabletools.config.ConfigManager

/**
 * Custom logger that writes messages both to the console and to a file.
 * Optimized with an asynchronous queue and file rotation management.
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

    /**
     * Starts the asynchronous worker that processes logs from the queue and writes them to the file.
     */
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
     * Logs an INFO message.
     * @param message The message to log.
     */
    fun info(message: String) = log("INFO", message)

    /**
     * Logs a WARNING message.
     * @param message The message to log.
     */
    fun warn(message: String) = log("WARN", message)

    /**
     * Logs an ERROR message.
     * @param message The message to log.
     */
    fun error(message: String) = log("ERROR", message)

    /**
     * Logs a DEBUG message.
     * @param message The message to log.
     */
    fun debug(message: String) = log("DEBUG", message)

    /**
     * Core logging logic. Filters by thread and handles output to console and file.
     * @param level The log level (INFO, WARN, etc.).
     * @param message The message to log.
     */
    private fun log(level: String, message: String) {
        val config = try { ConfigManager.getConfig() } catch (e: Exception) { null }
        if (config != null && !config.logging.enable) return

        // We only log if we are on the server thread to avoid client/server duplicates
        val isServerThread = Thread.currentThread().name.contains("Server thread", ignoreCase = true)
        val timestamp = LocalDateTime.now().format(formatter)
        val formattedMessage = "[$level][$timestamp] $message"

        // Console (if server or if authorized)
        if (config == null || config.logging.inConsole) {
            if (isServerThread || config == null) {
                println(formattedMessage.toAscii())
            }
        }

        // File (only via server thread to avoid access conflicts)
        if (isServerThread && (config == null || config.logging.inFile)) {
            logQueue.offer(formattedMessage)
        }
    }

    /**
     * Processes writing a message to the log file and handles rotation if needed.
     * @param message The formatted message to write.
     */
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

    /**
     * Renames the current log file with a timestamp and starts a new one.
     * @param logFile The current log file.
     */
    private fun rotateLogFile(logFile: File) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
        val rotatedFile = File("${LOG_FILE_PATH}.$timestamp.old")
        logFile.renameTo(rotatedFile)
    }

    /**
     * Extension to keep native Minecraft UTF-8 characters.
     */
    private fun String.toAscii(): String {
        // Keeping native Minecraft UTF-8 which is supported by most modern terminals.
        return this
    }
}