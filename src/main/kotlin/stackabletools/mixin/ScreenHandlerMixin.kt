package stackabletools.mixin

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.SlotActionType
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import stackabletools.StackableToolsUtils
import stackabletools.config.ConfigManager

/**
 * Mixin for ScreenHandler to handle inventory interactions within graphical interfaces.
 * Allows items that normally do not stack to be managed neatly by the player (moving, merging, etc.)
 */
@Mixin(ScreenHandler::class)
abstract class ScreenHandlerMixin {

    /**
     * Injected into the onSlotClick function to monitor and modify player mouse clicks within inventories.
     * We manually handle the PICKUP action when the item is marked as stackable by the mod.
     */
    @Inject(method = ["onSlotClick"], at = [At("HEAD")], cancellable = true)
    private fun onSlotClick(slotIndex: Int, button: Int, actionActionType: SlotActionType, player: PlayerEntity, ci: CallbackInfo) {
        // Run logic only on the server to prevent synchronization mismatches
        if (player.getWorld().isClient) return
        
        if (actionActionType == SlotActionType.PICKUP) {
            val handler = this as Any as ScreenHandler
            if (slotIndex < 0 || slotIndex >= handler.slots.size) return
            
            val slot = handler.getSlot(slotIndex)
            val cursorStack = handler.cursorStack
            val slotStack = slot.stack

            // Logic: if current cursor item and slot item are the same "stackable" tool/potion, merge them manually
            if (!cursorStack.isEmpty && !slotStack.isEmpty && 
                StackableToolsUtils.isStackableItem(cursorStack) &&
                StackableToolsUtils.canStackItems(cursorStack, slotStack)) {
                
                val cfg = ConfigManager.getConfig().stacking
                // Determine limits for the category
                val maxStack = when (cursorStack.item) {
                    is SwordItem, is TridentItem -> cfg.maxWeaponsStackSize
                    is ToolItem -> cfg.maxToolStackSize
                    is PotionItem -> cfg.maxPotionStackSize
                    is EnchantedBookItem -> cfg.maxEnchantedBooksStackSize
                    is ElytraItem -> cfg.maxElytraStackSize
                    is ArmorItem -> cfg.maxArmorPieceStackSize
                    else -> cfg.maxStackSize
                }.toInt()
                
                val canTransfer = (maxStack - slotStack.count).coerceAtLeast(0)
                if (canTransfer > 0) {
                    val toTransfer = minOf(cursorStack.count, canTransfer)
                    slotStack.count += toTransfer
                    cursorStack.count -= toTransfer
                    
                    // Sync the state with client side to ensure visual representation is correct
                    handler.syncState()
                    ci.cancel() // Interrupt vanilla behavior
                }
            }
        }
    }
}
