package stackabletoolskotlin.config

/**
 * Classe représentant la configuration du mod StackableToolsKotlin
 */
data class StackableToolsKotlinConfig(
    var enableLogging: Boolean = true,
    var logLevel: String = "INFO",

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
        fun getDefault(): StackableToolsKotlinConfig = StackableToolsKotlinConfig()
    }
}