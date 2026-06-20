package stackabletools

import java.nio.charset.StandardCharsets

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
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
                    Thread.sleep(2000)

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
                Commands.literal("debug-test")
                    .requires { true }
                    .executes { context ->
                        val source = context.source
                        val player = source.player ?: return@executes 0

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
                            if (!player.inventory.add(stack)) {
                                player.drop(stack, false)
                            }
                        }

                        source.sendSuccess({ Component.literal("Test items given!") }, false)
                        1
                    }
            )

            dispatcher.register(
                Commands.literal("stackabletools")
                    .then(
                        Commands.literal("reload")
                            .requires { true }
                            .executes { context ->
                                ConfigManager.loadConfig()
                                context.source.sendSuccess({ Component.literal("Configuration reloaded!") }, false)
                                1
                            }
                    )
            )

            dispatcher.register(
                Commands.literal("config-mod")
                    .requires { true }
                    .executes { context ->
                        try {
                            val configFile = File("config/stackabletools.toml").absoluteFile
                            if (!configFile.exists()) {
                                context.source.sendFailure(Component.literal("Config file does not exist: ${configFile.path}"))
                                return@executes 1
                            }

                            var opened = false

                            if (Desktop.isDesktopSupported()) {
                                try {
                                    Desktop.getDesktop().open(configFile)
                                    opened = true
                                } catch (_: Exception) {}
                            }

                            if (!opened) {
                                try {
                                    Runtime.getRuntime().exec(arrayOf("xdg-open", configFile.path))
                                    opened = true
                                } catch (_: Exception) {}
                            }

                            if (!opened) {
                                try {
                                    Runtime.getRuntime().exec(arrayOf("gio", "open", configFile.path))
                                    opened = true
                                } catch (_: Exception) {}
                            }

                            if (opened) {
                                context.source.sendSuccess({ Component.literal("Opening config file...") }, false)
                            } else {
                                context.source.sendFailure(Component.literal("Mechanically unable to open the file. Please open it manually: ${configFile.path}"))
                            }
                        } catch (e: Exception) {
                            context.source.sendFailure(Component.literal("Critical error during opening: ${e.message}"))
                        }
                        1
                    }
            )
        }
    }
}
