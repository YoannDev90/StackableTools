package stackabletools.mixin

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ArmorItem
import net.minecraft.item.ElytraItem
import net.minecraft.item.ItemStack
import net.minecraft.item.PotionItem
import net.minecraft.item.ToolItem
import net.minecraft.item.SwordItem
import net.minecraft.item.TridentItem
import net.minecraft.item.EnchantedBookItem
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import stackabletools.CustomLogger
import stackabletools.StackableToolsUtils
import stackabletools.config.ConfigManager

/**
 * Mixin for the player's inventory to handle the insertion of items that can be stacked.
 * Overrides the base logic to allow merging stacks of items that normally don't stack (tools, potions, etc.).
 */
@Mixin(PlayerInventory::class)
abstract class InventoryMixin {

    @Shadow
    abstract fun getStack(slot: Int): ItemStack

    @Shadow
    abstract fun setStack(slot: Int, stack: ItemStack)

    /**
     * Injects at the beginning of insertStack to handle custom stacking logic.
     * If the item is stackable according to our mod, we manually process its insertion to respect custom stack sizes.
     */
    @Inject(method = ["insertStack(Lnet/minecraft/item/ItemStack;)Z"], at = [At("HEAD")], cancellable = true)
    private fun onInsertStack(stack: ItemStack, cir: CallbackInfoReturnable<Boolean>) {
        val inv = this as Any as PlayerInventory
        // We only work on the server side and for non-empty stacks
        if (stack.isEmpty || inv.player.getWorld().isClient) return
        
        // Check if the item is eligible for stacking
        if (!StackableToolsUtils.isStackableItem(stack)) return

        // RULE: We do not stack damaged items to avoid loss of durability data or complex merges
        if (stack.isDamaged && (stack.item is ToolItem || stack.item is ArmorItem || stack.item is SwordItem || stack.item is TridentItem || stack.item is ElytraItem)) {
            return
        }

        val cfg = ConfigManager.getConfig().stacking
        // Determine the max stack size based on item category
        val maxStackSize = when (stack.item) {
            is SwordItem, is TridentItem -> cfg.maxWeaponsStackSize
            is ToolItem -> cfg.maxToolStackSize
            is PotionItem -> cfg.maxPotionStackSize
            is EnchantedBookItem -> cfg.maxEnchantedBooksStackSize
            is ElytraItem -> cfg.maxElytraStackSize
            is ArmorItem -> cfg.maxArmorPieceStackSize
            else -> cfg.maxStackSize
        }.toInt()

        var remaining = stack.count
        val originalCount = stack.count

        // Step 1: Attempt to merge with existing stacks
        for (slot in 0 until 36) { 
            if (remaining <= 0) break
            val existing = getStack(slot)
            if (existing.isEmpty) continue
            
            // Check if items are compatible (NBT, Enchantments, etc.)
            if (StackableToolsUtils.canStackItems(existing, stack)) {
                val freeSpace = (maxStackSize - existing.count).coerceAtLeast(0)
                if (freeSpace > 0) {
                    val toMove = minOf(remaining, freeSpace)
                    existing.count += toMove
                    remaining -= toMove
                }
            }
        }

        // Step 2: Fill empty slots if there is still remaining item count
        if (remaining > 0) {
            for (slot in 0 until 36) {
                if (remaining <= 0) break
                val existing = getStack(slot)
                if (existing.isEmpty) {
                    val countToPut = minOf(remaining, maxStackSize)
                    val toPut = stack.copy()
                    toPut.count = countToPut
                    setStack(slot, toPut)
                    remaining -= countToPut
                }
            }
        }

        stack.count = remaining
        cir.returnValue = remaining < originalCount
        cir.cancel()
    }
}
