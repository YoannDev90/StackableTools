package stackabletools.mixin

import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.PlayerScreenHandler
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/**
 * Mixin targeting armor slots to ensure they never accept more than 1 item,
 * even if our mod makes the item stackable in the general inventory.
 */
@Mixin(Slot::class)
abstract class ArmorSlotMixin {

    /**
     * Forces the maximum item count for armor slots to be 1.
     * This prevents stacking items like Elytras or Armor pieces in the equipment slots.
     */
    @Inject(method = ["getMaxItemCount"], at = [At("HEAD")], cancellable = true)
    private fun onGetMaxItemCount(stack: ItemStack, cir: CallbackInfoReturnable<Int>) {
        val slot = this as Slot
        
        // Check if this is an armor slot (PlayerScreenHandler inner classes target armor/offhand)
        // Armor slots are typically indices 5-8 in PlayerScreenHandler
        if (slot.javaClass.name.contains("PlayerScreenHandler") && 
            (slot.index in 5..8 || slot.javaClass.simpleName in listOf("OffhandSlot", "ArmorSlot"))) {
            cir.returnValue = 1
        }
    }
}
