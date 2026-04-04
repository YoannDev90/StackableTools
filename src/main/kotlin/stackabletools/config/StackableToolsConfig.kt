package stackabletools.config

/**
 * Class representing the StackableTools mod configuration.
 */
data class StackableToolsConfig(
    var logging: LoggingConfig = LoggingConfig(),
    var stacking: StackingConfig = StackingConfig(),
    var isLoaded: Boolean = false
) {
    /**
     * Logging specific configuration.
     */
    data class LoggingConfig(
        var enable: Boolean = true,
        var level: String = "INFO",
        var inFile: Boolean = true,
        var inConsole: Boolean = true
    )

    /**
     * Stacking specific configuration and limits.
     */
    data class StackingConfig(
        var enable: Boolean = true,
        var maxStackSize: Long = 64L,
        var maxToolStackSize: Long = 8L,
        var maxPotionStackSize: Long = 16L,
        var maxEnchantedBooksStackSize: Long = 4L,
        var maxWeaponsStackSize: Long = 1L,
        var maxElytraStackSize: Long = 1L,
        var maxArmorPieceStackSize: Long = 1L,
        var activeCategories: List<StackingCategory> = listOf(
            StackingCategory.TOOLS,
            StackingCategory.POTIONS,
            StackingCategory.ENCHANTED_BOOKS,
            StackingCategory.WEAPONS,
            StackingCategory.ARMORS,
            StackingCategory.ELYTRA
        ),
        var excludedItemIds: List<String> = listOf(),
        var manualStackableItemIds: List<String> = listOf()
    )

    companion object {
        /**
         * Returns a new instance of the configuration with default values.
         */
        fun getDefault(): StackableToolsConfig = StackableToolsConfig()
    }
}