
package stackabletools

import java.nio.charset.StandardCharsets

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.command.CommandManager
import net.minecraft.text.Text
import org.slf4j.LoggerFactory
import stackabletools.CustomLogger
import stackabletools.config.ConfigManager
import java.io.File
import java.awt.Desktop
import java.nio.file.*
import kotlin.concurrent.thread

object StackableTools : ModInitializer {
	private val logger = LoggerFactory.getLogger("stackabletools")
	private var watchThread: Thread? = null

	private fun getModVersion(): String {
		// Essaye de lire la version depuis fabric.mod.json dans le jar
		return try {
			val resource = javaClass.classLoader.getResourceAsStream("fabric.mod.json")
			if (resource != null) {
				val text = resource.reader(StandardCharsets.UTF_8).readText()
				val match = Regex("\"version\"\\s*:\\s*\"([^\"]+)\"").find(text)
				val version = match?.groupValues?.get(1) ?: "unknown"
				if (version.startsWith("$")) "unknown" else version
			} else {
				"unknown"
			}
		} catch (_: Exception) {
			"unknown"
		}
	}

	override fun onInitialize() {
		val version = getModVersion()
		CustomLogger.info("StackableTools version $version initializing")

		// Force le chargement et la création de la config au démarrage si elle n'existe pas
		val config = ConfigManager.getConfig()
		if (!config.isLoaded) {
			CustomLogger.info("StackableTools configuration loaded by default via ConfigManager")
		}

		registerCommands()
		startConfigWatcher()
	}

	private fun startConfigWatcher() {
		watchThread = thread(isDaemon = true, name = "StackableTools-ConfigWatcher") {
			try {
				val configPath = Paths.get("config/stackabletools.toml")
				var lastModified = if (Files.exists(configPath)) Files.getLastModifiedTime(configPath).toMillis() else 0L
				
				CustomLogger.info("Config watcher service enabled (Polling mode).")

				while (true) {
					Thread.sleep(2000) // Poll every 2 seconds even when game is paused
					
					if (Files.exists(configPath)) {
						val currentModified = Files.getLastModifiedTime(configPath).toMillis()
						if (currentModified > lastModified) {
							lastModified = currentModified
							ConfigManager.loadConfig()
							CustomLogger.info("Config file changed: auto-reload performed.")
						}
					}
				}
			} catch (e: Exception) {
				CustomLogger.error("Error in config watcher service: ${e.message}")
			}
		}
	}

	private fun registerCommands() {
		CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
			dispatcher.register(
				CommandManager.literal("debug-test")
					.requires { it.hasPermissionLevel(2) }
					.executes { context ->
						val player = context.source.player ?: return@executes 0
						
						// Give items
						val items = listOf(
							Pair(Items.NETHERITE_SHOVEL, 10),
							Pair(Items.NETHERITE_PICKAXE, 10),
							Pair(Items.NETHERITE_AXE, 10),
							Pair(Items.NETHERITE_SWORD, 3),
							Pair(Items.ELYTRA, 2),
							Pair(Items.POTION, 18),
							Pair(Items.ENCHANTED_BOOK, 5)
						)

						items.forEach { (item, count) ->
							val stack = ItemStack(item, count)
							if (!player.inventory.insertStack(stack)) {
								player.dropItem(stack, false)
							}
						}

						context.source.sendFeedback({ Text.literal("Test items given!") }, false)
						1
					}
			)

			dispatcher.register(
				CommandManager.literal("reload")
					.requires { it.hasPermissionLevel(2) }
					.executes { context ->
						ConfigManager.loadConfig()
						context.source.sendFeedback({ Text.literal("Configuration reloaded!") }, false)
						1
					}
			)

			dispatcher.register(
				CommandManager.literal("config-mod")
					.requires { it.hasPermissionLevel(2) }
					.executes { context ->
						try {
							val configFile = File("config/stackabletools.toml").absoluteFile
							if (!configFile.exists()) {
								context.source.sendError(Text.literal("Config file does not exist: ${configFile.path}"))
								return@executes 1
							}

							var opened = false
							
							// Tentative 1 : Desktop (standard Java)
							if (Desktop.isDesktopSupported()) {
								try {
									Desktop.getDesktop().open(configFile)
									opened = true
								} catch (_: Exception) {}
							}

							// Tentative 2 : xdg-open (Linux standard)
							if (!opened) {
								try {
									Runtime.getRuntime().exec(arrayOf("xdg-open", configFile.path))
									opened = true
								} catch (_: Exception) {}
							}

							// Tentative 3 : gnome-open (Vieux systèmes)
							if (!opened) {
								try {
									Runtime.getRuntime().exec(arrayOf("gnome-open", configFile.path))
									opened = true
								} catch (_: Exception) {}
							}

							if (opened) {
								context.source.sendFeedback({ Text.literal("Opening config file...") }, false)
							} else {
								context.source.sendError(Text.literal("Mechanically unable to open the file. Please open it manually: ${configFile.path}"))
							}
						} catch (e: Exception) {
							context.source.sendError(Text.literal("Critical error during opening: ${e.message}"))
						}
						1
					}
			)
		}
	}
}