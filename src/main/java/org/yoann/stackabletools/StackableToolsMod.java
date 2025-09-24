package org.yoann.stackabletools;

import net.fabricmc.api.ModInitializer;

public class StackableToolsMod implements ModInitializer {
	public static final String MODID = "stackabletools";

	@Override
	public void onInitialize() {
		System.out.println("Stackable Tools Mod initialized. Tools now stack up to 8.");
	}
}
