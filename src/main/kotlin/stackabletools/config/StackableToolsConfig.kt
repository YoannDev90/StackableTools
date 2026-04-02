package stackabletools.config

/**
 * Classe représentant la configuration du mod StackableTools
 */
data class StackableToolsConfig(
    var enableLogging: Boolean = true,
    var logLevel: String = "INFO",
    var logInFile: Boolean = true,
    var logInConsole: Boolean = true,

    var enableStacking: Boolean = true,
    var maxStackSize: Long = 64L,
    var maxToolStackSize: Long = 8L,
    var maxPotionStackSize: Long = 16L,

    var activeCategories: List<String> = listOf("tools", "potions"),
    var excludedItemIds: List<String> = listOf(),
    var manualStackableItemIds: List<String> = listOf(),

    var isLoaded: Boolean = false
) {
    companion object {
        fun getDefault(): StackableToolsConfig = StackableToolsConfig()
    }
}