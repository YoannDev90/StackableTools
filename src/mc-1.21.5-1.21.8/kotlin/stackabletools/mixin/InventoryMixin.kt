package stackabletools.mixin

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.TridentItem
import net.minecraft.item.Items
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import stackabletools.CustomLogger
import stackabletools.StackableToolsUtils
import stackabletools.config.ConfigManager
import net.minecraft.component.DataComponentTypes

@Mixin(PlayerInventory::class)
abstract class InventoryMixin {

    @Shadow
    abstract fun getStack(slot: Int): ItemStack

    @Shadow
    abstract fun setStack(slot: Int, stack: ItemStack)

    @Inject(method = ["insertStack(Lnet/minecraft/item/ItemStack;)Z"], at = [At("HEAD")], cancellable = true)
    private fun onInsertStack(stack: ItemStack, cir: CallbackInfoReturnable<Boolean>) {
        val inv = this as Any as PlayerInventory
        if (stack.isEmpty || inv.player.getWorld().isClient) return

        if (!StackableToolsUtils.isStackableItem(stack)) return

        val isDamageable = stack.get(DataComponentTypes.DAMAGE) != null || stack.get(DataComponentTypes.MAX_DAMAGE) != null
        if (stack.isDamaged && isDamageable) {
            return
        }

        val cfg = ConfigManager.getConfig().stacking
        val maxStackSize = StackableToolsUtils.maxStackFor(stack, cfg)

        var remaining = stack.count
        val originalCount = stack.count

        for (slot in 0 until 36) {
            if (remaining <= 0) break
            val existing = getStack(slot)
            if (existing.isEmpty) continue

            if (StackableToolsUtils.canStackItems(existing, stack)) {
                val freeSpace = (maxStackSize - existing.count).coerceAtLeast(0)
                if (freeSpace > 0) {
                    val toMove = minOf(remaining, freeSpace)
                    existing.count += toMove
                    remaining -= toMove
                }
            }
        }

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
