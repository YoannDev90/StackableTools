package stackabletools

import org.slf4j.LoggerFactory
import stackabletools.config.ConfigManager

object CustomLogger {
    private val logger = LoggerFactory.getLogger("stackabletools")

    private val config get() = try { ConfigManager.getConfig() } catch (_: Exception) { null }

    fun info(message: String) {
        val cfg = config
        if (cfg == null || cfg.logging.enable) logger.info(message)
    }

    fun warn(message: String) {
        val cfg = config
        if (cfg == null || cfg.logging.enable) logger.warn(message)
    }

    fun error(message: String) {
        val cfg = config
        if (cfg == null || cfg.logging.enable) logger.error(message)
    }

    fun debug(message: String) {
        val cfg = config
        if (cfg == null || cfg.logging.enable) logger.debug(message)
    }
}
