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

@Mixin(ScreenHandler::class)
abstract class ScreenHandlerMixin {

    @Inject(method = ["onSlotClick"], at = [At("HEAD")], cancellable = true)
    private fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity, ci: CallbackInfo) {
        if (player.getWorld().isClient) return
        
        if (actionType == SlotActionType.PICKUP) {
            val handler = this as Any as ScreenHandler
            if (slotIndex < 0 || slotIndex >= handler.slots.size) return
            
            val slot = handler.getSlot(slotIndex)
            val cursorStack = handler.cursorStack
            val slotStack = slot.stack

            if (!cursorStack.isEmpty && !slotStack.isEmpty && 
                StackableToolsUtils.isStackableItem(cursorStack) &&
                StackableToolsUtils.canStackItems(cursorStack, slotStack)) {
                
                val cfg = ConfigManager.getConfig().stacking
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
                    
                    handler.syncState()
                    ci.cancel()
                }
            }
        }
    }
}
