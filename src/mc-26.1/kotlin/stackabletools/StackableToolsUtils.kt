package stackabletools

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.PotionItem
import net.minecraft.world.item.TridentItem
import net.minecraft.core.registries.BuiltInRegistries
import java.util.concurrent.ConcurrentHashMap
import net.minecraft.core.component.DataComponents
import net.minecraft.core.component.DataComponentType
import stackabletools.config.ConfigManager
import stackabletools.config.StackingCategory
import net.minecraft.world.item.Items

object StackableToolsUtils {

    private val stackableCache = ConcurrentHashMap<Item, Boolean>()
    private var lastConfigHash = 0

    private val cosmetics = setOf(
        DataComponents.CUSTOM_NAME,
        DataComponents.ITEM_NAME,
        DataComponents.LORE,
        DataComponents.CUSTOM_MODEL_DATA,
    )

    private fun areComponentsEquivalent(a: ItemStack, b: ItemStack): Boolean {
        val aComponents = a.components
        val bComponents = b.components

        for (component in aComponents) {
            val type = component.type()
            if (type !in cosmetics) {
                @Suppress("UNCHECKED_CAST")
                val ct = type as DataComponentType<Any>
                if (aComponents.get(ct) != bComponents.get(ct)) return false
            }
        }
        for (component in bComponents) {
            val type = component.type()
            if (type !in cosmetics) {
                @Suppress("UNCHECKED_CAST")
                val ct = type as DataComponentType<Any>
                if (aComponents.get(ct) != bComponents.get(ct)) return false
            }
        }

        return true
    }

    fun canStackItems(a: ItemStack, b: ItemStack): Boolean {
        if (a.isEmpty || b.isEmpty) return false
        if (a.item !== b.item) return false

        if (a.get(DataComponents.DAMAGE) != null || a.get(DataComponents.MAX_DAMAGE) != null) {
            if (a.damageValue > 0 || b.damageValue > 0) return false
        }

        if (a.damageValue != b.damageValue) return false

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
        val hasToolComponent = stack.get(DataComponents.TOOL) != null
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
        val itemId = BuiltInRegistries.ITEM.getKey(item).toString()
        val shortItemId = itemId.substringAfter(':')

        val excluded = config.stacking.excludedItemIds
        if (itemId in excluded || shortItemId in excluded) return false

        val active = config.stacking.activeCategories
        val isAll = StackingCategory.ALL in active

        val hasToolComponent = stack.get(DataComponents.TOOL) != null
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
        val path = BuiltInRegistries.ITEM.getKey(item).path
        return path.endsWith("_sword")
    }

    private fun isArmorItem(stack: ItemStack): Boolean {
        val equippable = stack.get(DataComponents.EQUIPPABLE) ?: return false
        if (stack.item === Items.ELYTRA) return false
        return true
    }
}
