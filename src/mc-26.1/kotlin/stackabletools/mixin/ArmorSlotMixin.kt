package stackabletools.mixin

import net.minecraft.world.item.ItemStack
import net.minecraft.world.inventory.Slot
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import stackabletools.StackableToolsUtils
import stackabletools.config.ConfigManager

@Mixin(Slot::class)
abstract class ArmorSlotMixin {

    @Inject(method = ["getMaxStackSize(Lnet/minecraft/world/item/ItemStack;)I"], at = [At("HEAD")], cancellable = true)
    private fun onGetMaxItemCount(stack: ItemStack, cir: CallbackInfoReturnable<Int>) {
        if (this.javaClass.simpleName in listOf("ArmorSlot", "OffhandSlot")) {
            cir.returnValue = 1
            return
        }

        if (stack.isEmpty || !StackableToolsUtils.isStackableItem(stack)) return

        val cfg = ConfigManager.getConfig().stacking
        val maxStack = StackableToolsUtils.maxStackFor(stack, cfg)

        cir.returnValue = maxStack
        cir.cancel()
    }
}
