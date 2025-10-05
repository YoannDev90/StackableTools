package org.yoann.stackabletools;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yoann.stackabletools.config.StackableToolsConfig;

public class StackableToolsMod implements ModInitializer {
	public static final String MODID = "stackabletools";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	private static StackableToolsConfig config;

	@Override
	public void onInitialize() {
		// Load configuration (this ensures the file is created and loaded properly)
		config = StackableToolsConfig.load();

		LOGGER.info("Stackable Tools Mod initialized. Tools now stack up to {}.", config.getMaxStackSize());
	}

	/**
	 * Gets the mod configuration instance.
	 * Returns the loaded config if available, otherwise returns a default instance.
	 */
	public static StackableToolsConfig getConfig() {
		if (config == null) {
			return StackableToolsConfig.getInstance();
		}
		return config;
	}
}
