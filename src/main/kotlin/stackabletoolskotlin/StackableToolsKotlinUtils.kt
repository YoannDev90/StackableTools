package stackabletoolskotlin

import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.item.PotionItem
import net.minecraft.item.ToolItem
import net.minecraft.registry.Registries
import stackabletoolskotlin.config.ConfigManager

object StackableToolsKotlinUtils {

    /**
     * Retourne true si l'item doit être empilé (tool/potion/configurable)
     */
    fun isToolOrManuallyRegistered(stack: ItemStack): Boolean {
        if (stack.isEmpty) return false

        val config = ConfigManager.getConfig()
        if (!config.enableStacking) return false

        val itemId = Registries.ITEM.getId(stack.item).toString()
        val shortItemId = itemId.substringAfter(':')

        val excludedItemIds = config.excludedItemIds
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (itemId in excludedItemIds || shortItemId in excludedItemIds) {
            return false
        }

        val activeCategories = config.activeCategories.map { it.trim().lowercase() }

        if (("tools" in activeCategories || "all" in activeCategories) && stack.item is ToolItem) {
            return true
        }

        if (("armors" in activeCategories || "armor" in activeCategories || "all" in activeCategories) && stack.item is ArmorItem) {
            return true
        }

        if (("potions" in activeCategories || "all" in activeCategories) && stack.item is PotionItem) {
            return true
        }

        val manualIds = config.manualStackableItemIds
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (itemId in manualIds || shortItemId in manualIds) {
            return true
        }

        return false
    }
}
