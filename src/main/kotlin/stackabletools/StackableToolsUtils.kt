package stackabletools

import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.item.PotionItem
import net.minecraft.item.ToolItem
import net.minecraft.registry.Registries
import stackabletools.config.ConfigManager
import stackabletools.config.StackableToolsConfig

object StackableToolsUtils {

    /**
     * Vérifie si deux stacks peuvent être fusionnés (même item, même durabilité, même NBT)
     */
    fun canStackSameDurability(a: ItemStack, b: ItemStack): Boolean {
        if (a.isEmpty || b.isEmpty) return false
        if (a.item !== b.item) return false
        
        // REGLE : On ne stacke que les outils NEUFS (damage == 0).
        if (a.item is ToolItem || a.item is ArmorItem) {
            // Si l'un des deux a ne serait-ce que 1 point de dégât, on refuse le stack.
            if (a.damage > 0 || b.damage > 0) return false
        }

        // Pour les autres items (potions, etc), on vérifie la durabilité exacte
        if (a.damage != b.damage) return false
        
        // On vérifie le NBT (enchantements, noms, etc) via la méthode native de Minecraft
        if (!ItemStack.canCombine(a, b)) return false
        
        return true
    }

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
