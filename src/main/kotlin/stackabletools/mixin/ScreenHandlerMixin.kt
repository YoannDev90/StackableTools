package stackabletools.mixin

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.SlotActionType
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import stackabletools.StackableToolsUtils

@Mixin(ScreenHandler::class)
abstract class ScreenHandlerMixin {

    @Inject(method = ["onSlotClick"], at = [At("HEAD")], cancellable = true)
    private fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity, ci: CallbackInfo) {
        if (player.getWorld().isClient) return
        
        // On intercepte le clic gauche classique (PICKUP) sur un slot
        if (actionType == SlotActionType.PICKUP) {
            val handler = this as Any as ScreenHandler
            if (slotIndex < 0 || slotIndex >= handler.slots.size) return
            
            val slot = handler.getSlot(slotIndex)
            val cursorStack = handler.cursorStack
            val slotStack = slot.stack

            // Si on essaie de poser un stack d'outils sur un autre stack d'outils identique
            if (!cursorStack.isEmpty && !slotStack.isEmpty && 
                StackableToolsUtils.isToolOrManuallyRegistered(cursorStack) &&
                StackableToolsUtils.canStackSameDurability(cursorStack, slotStack)) {
                
                val cfg = stackabletools.config.ConfigManager.getConfig()
                val maxStack = if (cursorStack.item is net.minecraft.item.ToolItem) cfg.maxToolStackSize else cfg.maxPotionStackSize
                
                val canTransfer = (maxStack.toInt() - slotStack.count).coerceAtLeast(0)
                if (canTransfer > 0) {
                    val toTransfer = minOf(cursorStack.count, canTransfer)
                    slotStack.count += toTransfer
                    cursorStack.count -= toTransfer
                    
                    // On force la mise à jour pour le client
                    handler.syncState()
                    ci.cancel() // On a géré le clic nous-mêmes
                }
            }
        }
    }
}