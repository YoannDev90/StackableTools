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
import stackabletools.config.ConfigManager
import stackabletools.config.StackingCategory

object StackableToolsUtils {

    /**
     * Vérifie si deux stacks peuvent être fusionnés (même item, même durabilité, même NBT)
     */
    fun canStackItems(a: ItemStack, b: ItemStack): Boolean {
        if (a.isEmpty || b.isEmpty) return false
        if (a.item !== b.item) return false
        
        // REGLE : On ne stacke que les objets NEUFS (damage == 0).
        if (a.item is ToolItem || a.item is ArmorItem || a.item is SwordItem || a.item is TridentItem || a.item is ElytraItem) {
            if (a.damage > 0 || b.damage > 0) return false
        }

        // Pour les autres items, on vérifie la durabilité exacte
        if (a.damage != b.damage) return false
        
        // On vérifie le NBT
        if (!ItemStack.canCombine(a, b)) return false
        
        return true
    }

    /**
     * Retourne true si l'item est éligible à l'empilement selon la config
     */
    fun isStackableItem(stack: ItemStack): Boolean {
        if (stack.isEmpty) return false

        val config = ConfigManager.getConfig()
        if (!config.stacking.enable) return false

        val itemId = Registries.ITEM.getId(stack.item).toString()
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
