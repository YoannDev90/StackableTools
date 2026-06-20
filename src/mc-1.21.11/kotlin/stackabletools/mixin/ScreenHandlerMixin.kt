package stackabletools.mixin

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.PotionItem
import net.minecraft.item.TridentItem
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.SlotActionType
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import stackabletools.StackableToolsUtils
import stackabletools.config.ConfigManager
import net.minecraft.component.DataComponentTypes
import net.minecraft.registry.Registries

@Mixin(ScreenHandler::class)
abstract class ScreenHandlerMixin {

    private fun tryMerge(handler: ScreenHandler, slotIndex: Int): Boolean {
        if (slotIndex < 0 || slotIndex >= handler.slots.size) return false
        val slot = handler.getSlot(slotIndex)
        val cursorStack = handler.cursorStack
        val slotStack = slot.stack
        if (cursorStack.isEmpty || slotStack.isEmpty) return false
        if (!StackableToolsUtils.isStackableItem(cursorStack) || !StackableToolsUtils.canStackItems(cursorStack, slotStack)) return false

        val cfg = ConfigManager.getConfig().stacking
        val maxStack = StackableToolsUtils.maxStackFor(cursorStack, cfg)

        val canTransfer = (maxStack - slotStack.count).coerceAtLeast(0)
        if (canTransfer <= 0) return false
        val toTransfer = minOf(cursorStack.count, canTransfer)
        slotStack.count += toTransfer
        cursorStack.count -= toTransfer
        handler.syncState()
        return true
    }

    @Inject(method = ["onSlotClick"], at = [At("HEAD")], cancellable = true)
    private fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity, ci: CallbackInfo) {
        if (player !is ServerPlayerEntity) return
        val handler = this as Any as ScreenHandler
        if (actionType == SlotActionType.PICKUP && tryMerge(handler, slotIndex)) ci.cancel()
    }
}
