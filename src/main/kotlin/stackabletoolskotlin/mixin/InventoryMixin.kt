package stackabletoolskotlin.mixin

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import stackabletoolskotlin.CustomLogger

@Mixin(PlayerInventory::class)
abstract class InventoryMixin {

    @Inject(method = ["insertStack", "(Lnet/minecraft/item/ItemStack;)Z"], at = [At("RETURN")])
    private fun onInsertStack(stack: ItemStack, cir: CallbackInfoReturnable<Boolean>) {
        if (!cir.returnValue || stack.isEmpty) return

        val itemName = stack.name.string
        val count = stack.count

        CustomLogger.info("Inventaire : objet ajouté -> $itemName x$count")
    }

    @Inject(method = ["setStack", "(ILnet/minecraft/item/ItemStack;)V"], at = [At("RETURN")])
    private fun onSetStack(slot: Int, stack: ItemStack, cir: CallbackInfo) {
        if (stack.isEmpty) return

        val itemName = stack.name.string
        val count = stack.count

        CustomLogger.info("Inventaire : slot $slot mis à jour -> $itemName x$count")
    }
}
