package stackabletools

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

/**
 * Entry point for Fabric Data Generation.
 * Used for generating assets, tags, and recipes at compile time.
 */
object StackableToolsDataGenerator : DataGeneratorEntrypoint {
    /**
     * Initializes the data generator. No tasks registered currently.
     */
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
    }
}