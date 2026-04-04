package stackabletools.mixin

import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/**
 * Mixin specifically targeting armor slots to ensure they never accept more than 1 item,
 * even if our mod makes the item stackable in the general inventory.
 */
@Mixin(targets = ["net.minecraft.screen.PlayerScreenHandler$1"])
abstract class ArmorSlotMixin : Slot(null, 0, 0, 0) {

    /**
     * Forces the maximum item count for the armor slots in PlayerScreenHandler to be 1.
     * This prevents stacking items like Elytras or Armor pieces in the equipment slots.
     */
    @Inject(method = ["getMaxItemCount(Lnet/minecraft/item/ItemStack;)I"], at = [At("HEAD")], cancellable = true)
    private fun onGetMaxItemCount(stack: ItemStack, cir: CallbackInfoReturnable<Int>) {
        cir.returnValue = 1
    }
}
