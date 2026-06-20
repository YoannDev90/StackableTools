package stackabletools.mixin

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.server.level.ServerPlayer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import stackabletools.CustomLogger
import stackabletools.StackableToolsUtils
import stackabletools.config.ConfigManager
import net.minecraft.core.component.DataComponents

@Mixin(Inventory::class)
abstract class InventoryMixin {

    @Shadow
    abstract fun getItem(slot: Int): ItemStack

    @Shadow
    abstract fun setItem(slot: Int, stack: ItemStack)

    @Inject(method = ["add(Lnet/minecraft/world/item/ItemStack;)Z"], at = [At("HEAD")], cancellable = true)
    private fun onInsertStack(stack: ItemStack, cir: CallbackInfoReturnable<Boolean>) {
        val inv = this as Any as Inventory
        if (stack.isEmpty || inv.player !is ServerPlayer) return

        if (!StackableToolsUtils.isStackableItem(stack)) return

        val isDamageable = stack.get(DataComponents.DAMAGE) != null || stack.get(DataComponents.MAX_DAMAGE) != null
        if (stack.isDamaged && isDamageable) {
            return
        }

        val cfg = ConfigManager.getConfig().stacking
        val maxStackSize = StackableToolsUtils.maxStackFor(stack, cfg)

        var remaining = stack.count
        val originalCount = stack.count

        for (slot in 0 until 36) {
            if (remaining <= 0) break
            val existing = getItem(slot)
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
                val existing = getItem(slot)
                if (existing.isEmpty) {
                    val countToPut = minOf(remaining, maxStackSize)
                    val toPut = stack.copy()
                    toPut.count = countToPut
                    setItem(slot, toPut)
                    remaining -= countToPut
                }
            }
        }

        stack.count = remaining
        cir.returnValue = remaining < originalCount
        cir.cancel()
    }
}
