package stackabletools

import net.minecraft.item.ArmorItem
import net.minecraft.item.ElytraItem
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.PotionItem
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.item.TridentItem
import net.minecraft.registry.Registries
import java.util.concurrent.ConcurrentHashMap
import net.minecraft.component.DataComponentTypes
import stackabletools.config.ConfigManager
import stackabletools.config.StackingCategory
import net.minecraft.item.Items

object StackableToolsUtils {

    private val stackableCache = ConcurrentHashMap<String, Boolean>()
    private var lastConfigHash = 0

    /**
     * Compares two ItemStack component sets, ignoring cosmetic differences like custom names.
     */
    private fun areComponentsEquivalent(a: ItemStack, b: ItemStack): Boolean {
        val cosmetics = setOf(
            DataComponentTypes.CUSTOM_NAME,
            DataComponentTypes.ITEM_NAME,
            DataComponentTypes.LORE,
            DataComponentTypes.CUSTOM_MODEL_DATA,
            DataComponentTypes.HIDE_TOOLTIP,
            DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP,
        )

        val filteredA = a.getComponents().filtered { type -> type !in cosmetics }
        val filteredB = b.getComponents().filtered { type -> type !in cosmetics }

        return filteredA == filteredB
    }

    /**
     * Checks if two item stacks can be merged (same item, same durability, same NBT).
     * @param a First item stack.
     * @param b Second item stack.
     * @return True if the stacks can be merged.
     */
    fun canStackItems(a: ItemStack, b: ItemStack): Boolean {
        if (a.isEmpty || b.isEmpty) return false
        if (a.item !== b.item) return false
        
        // RULE: We only stack FRESH items (damage == 0).
        if (a.item is ToolItem || a.item is ArmorItem || a.item is SwordItem || a.item is TridentItem || a.item is ElytraItem) {
            if (a.damage > 0 || b.damage > 0) return false
        }

        // For other items, check exact durability
        if (a.damage != b.damage) return false
        
        // Compare NBT tags: enchantments must match (order-insensitive), 
        // but ignore cosmetic differences like custom names
        if (!areComponentsEquivalent(a, b)) return false
        
        return true
    }

    /**
     * Returns true if the item is eligible for stacking according to the configuration.
     * Uses a cache to optimize performance.
     * @param stack The item stack to check.
     * @return True if the item can be stacked.
     */
    fun isStackableItem(stack: ItemStack): Boolean {
        if (stack.isEmpty) return false

        val config = ConfigManager.getConfig()
        if (!config.stacking.enable) return false

        // Cache invalidation if config changes (simple hash check)
        val configHash = config.stacking.hashCode()
        if (configHash != lastConfigHash) {
            stackableCache.clear()
            lastConfigHash = configHash
        }

        val itemId = Registries.ITEM.getId(stack.item).toString()
        return stackableCache.getOrPut(itemId) {
            computeIsStackable(stack, config, itemId)
        }
    }

    /**
     * Internal logic to compute if an item is stackable.
     */
    private fun computeIsStackable(stack: ItemStack, config: stackabletools.config.StackableToolsConfig, itemId: String): Boolean {
        val shortItemId = itemId.substringAfter(':')

        val excluded = config.stacking.excludedItemIds
        if (itemId in excluded || shortItemId in excluded) return false

        val active = config.stacking.activeCategories
        val isAll = StackingCategory.ALL in active

        when (stack.item) {
            is SwordItem, is TridentItem -> if (isAll || StackingCategory.WEAPONS in active) return true
            is ToolItem -> if (isAll || StackingCategory.TOOLS in active) return true
            is ArmorItem -> if (isAll || StackingCategory.ARMORS in active) return true
            is PotionItem -> if (isAll || StackingCategory.POTIONS in active) return true
            is EnchantedBookItem -> if (isAll || StackingCategory.ENCHANTED_BOOKS in active) return true
            is ElytraItem -> if (isAll || StackingCategory.ELYTRA in active) return true
        }

        val manual = config.stacking.manualStackableItemIds
        if (itemId in manual || shortItemId in manual) return true

        return false
    }
}
