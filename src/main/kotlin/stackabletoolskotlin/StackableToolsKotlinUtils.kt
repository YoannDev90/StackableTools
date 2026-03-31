package stackabletoolskotlin

import net.minecraft.item.ItemStack
import net.minecraft.item.ToolItem
import net.minecraft.registry.Registries
import stackabletoolskotlin.config.ConfigManager

object StackableToolsKotlinUtils {

    /**
     * Retourne true si l'item est un outil ou s'il est listé dans la config (stacking.manual_item_ids)
     */
    fun isToolOrManuallyRegistered(stack: ItemStack): Boolean {
        if (stack.isEmpty) return false

        if (stack.item is ToolItem) {
            return true
        }

        val itemId = Registries.ITEM.getId(stack.item).toString()

        val configIds = ConfigManager.getConfig().manualStackableItemIds
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (itemId in configIds) return true

        // supporte aussi la forme sans namespace (e.g. "stone")
        val shortId = itemId.substringAfter(':')
        if (shortId in configIds) return true

        return false
    }
}
