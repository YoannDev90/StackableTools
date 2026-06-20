package stackabletools.mixin

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.server.level.ServerPlayer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import stackabletools.StackableToolsUtils
import stackabletools.config.ConfigManager

@Mixin(AbstractContainerMenu::class)
abstract class ScreenHandlerMixin {

    private fun tryMerge(handler: AbstractContainerMenu, slotIndex: Int): Boolean {
        if (slotIndex < 0 || slotIndex >= handler.slots.size) return false
        val slot = handler.getSlot(slotIndex)
        val cursorStack = handler.carried
        val slotStack = slot.item
        if (cursorStack.isEmpty || slotStack.isEmpty) return false
        if (!StackableToolsUtils.isStackableItem(cursorStack) || !StackableToolsUtils.canStackItems(cursorStack, slotStack)) return false

        val cfg = ConfigManager.getConfig().stacking
        val maxStack = StackableToolsUtils.maxStackFor(cursorStack, cfg)

        val canTransfer = (maxStack - slotStack.count).coerceAtLeast(0)
        if (canTransfer <= 0) return false
        val toTransfer = minOf(cursorStack.count, canTransfer)
        slotStack.count += toTransfer
        cursorStack.count -= toTransfer
        handler.broadcastChanges()
        return true
    }

    @Inject(method = ["clicked"], at = [At("HEAD")], cancellable = true)
    private fun onSlotClick(slotIndex: Int, button: Int, input: ContainerInput, player: Player, ci: CallbackInfo) {
        if (player !is ServerPlayer) return
        val handler = this as Any as AbstractContainerMenu
        if (input == ContainerInput.PICKUP && tryMerge(handler, slotIndex)) ci.cancel()
    }
}
