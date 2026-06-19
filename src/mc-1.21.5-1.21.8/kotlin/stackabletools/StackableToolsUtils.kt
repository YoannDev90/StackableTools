package stackabletools

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.PotionItem
import net.minecraft.item.TridentItem
import net.minecraft.registry.Registries
import java.util.concurrent.ConcurrentHashMap
import net.minecraft.component.DataComponentTypes
import stackabletools.config.ConfigManager
import stackabletools.config.StackingCategory
import net.minecraft.item.Items

object StackableToolsUtils {

    private val stackableCache = ConcurrentHashMap<Item, Boolean>()
    private var lastConfigHash = 0

    private val cosmetics = setOf(
        DataComponentTypes.CUSTOM_NAME,
        DataComponentTypes.ITEM_NAME,
        DataComponentTypes.LORE,
        DataComponentTypes.CUSTOM_MODEL_DATA,
    )

    private fun areComponentsEquivalent(a: ItemStack, b: ItemStack): Boolean {
        val filteredA = a.getComponents().filtered { type -> type !in cosmetics }
        val filteredB = b.getComponents().filtered { type -> type !in cosmetics }

        return filteredA == filteredB
    }

    fun canStackItems(a: ItemStack, b: ItemStack): Boolean {
        if (a.isEmpty || b.isEmpty) return false
        if (a.item !== b.item) return false

        if (a.get(DataComponentTypes.DAMAGE) != null || a.get(DataComponentTypes.MAX_DAMAGE) != null) {
            if (a.damage > 0 || b.damage > 0) return false
        }

        if (a.damage != b.damage) return false

        if (!areComponentsEquivalent(a, b)) return false

        return true
    }

    fun isStackableItem(stack: ItemStack): Boolean {
        if (stack.isEmpty) return false

        val config = ConfigManager.getConfig()
        if (!config.stacking.enable) return false

        val configHash = config.stacking.hashCode()
        if (configHash != lastConfigHash) {
            stackableCache.clear()
            lastConfigHash = configHash
        }

        val item = stack.item
        return stackableCache.getOrPut(item) {
            computeIsStackable(stack, config, item)
        }
    }

    fun maxStackFor(stack: ItemStack, cfg: stackabletools.config.StackableToolsConfig.StackingConfig): Int {
        val hasToolComponent = stack.get(DataComponentTypes.TOOL) != null
        val isSword = isSwordItem(stack.item)
        val isArmor = isArmorItem(stack)

        return when {
            isSword || stack.item is TridentItem -> cfg.maxWeaponsStackSize
            hasToolComponent && !isSword && stack.item !is TridentItem -> cfg.maxToolStackSize
            isArmor -> cfg.maxArmorPieceStackSize
            stack.item is PotionItem -> cfg.maxPotionStackSize
            stack.item === Items.ENCHANTED_BOOK -> cfg.maxEnchantedBooksStackSize
            stack.item === Items.ELYTRA -> cfg.maxElytraStackSize
            else -> cfg.maxStackSize
        }.toInt().coerceAtLeast(1)
    }

    private fun computeIsStackable(stack: ItemStack, config: stackabletools.config.StackableToolsConfig, item: Item): Boolean {
        val itemId = Registries.ITEM.getId(item).toString()
        val shortItemId = itemId.substringAfter(':')

        val excluded = config.stacking.excludedItemIds
        if (itemId in excluded || shortItemId in excluded) return false

        val active = config.stacking.activeCategories
        val isAll = StackingCategory.ALL in active

        val hasToolComponent = stack.get(DataComponentTypes.TOOL) != null
        val isSword = isSwordItem(item)
        val isArmor = isArmorItem(stack)

        when {
            isSword || item is TridentItem -> if (isAll || StackingCategory.WEAPONS in active) return true
            hasToolComponent && !isSword && item !is TridentItem -> if (isAll || StackingCategory.TOOLS in active) return true
            isArmor -> if (isAll || StackingCategory.ARMORS in active) return true
            stack.item is PotionItem -> if (isAll || StackingCategory.POTIONS in active) return true
            stack.item === Items.ENCHANTED_BOOK -> if (isAll || StackingCategory.ENCHANTED_BOOKS in active) return true
            stack.item === Items.ELYTRA -> if (isAll || StackingCategory.ELYTRA in active) return true
        }

        val manual = config.stacking.manualStackableItemIds
        if (itemId in manual || shortItemId in manual) return true

        return false
    }

    private fun isSwordItem(item: Item): Boolean {
        val path = Registries.ITEM.getId(item).path
        return path.endsWith("_sword")
    }

    private fun isArmorItem(stack: ItemStack): Boolean {
        val equippable = stack.get(DataComponentTypes.EQUIPPABLE) ?: return false
        if (stack.item === Items.ELYTRA) return false
        return true
    }
}
