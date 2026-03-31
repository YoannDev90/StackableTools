package stackabletoolskotlin.config

/**
 * Classe représentant la configuration du mod StackableToolsKotlin
 */
data class StackableToolsKotlinConfig(
    var enableLogging: Boolean = true,
    var logLevel: String = "INFO",
    var maxStackSize: Long = 64L,
    var enableStacking: Boolean = true,
    var manualStackableItemIds: List<String> = listOf(),
    var isLoaded: Boolean = false
) {
    companion object {
        fun getDefault(): StackableToolsKotlinConfig = StackableToolsKotlinConfig()
    }
}