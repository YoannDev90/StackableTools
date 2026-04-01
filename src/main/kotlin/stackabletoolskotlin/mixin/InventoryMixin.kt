package stackabletoolskotlin.mixin

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.PotionItem
import net.minecraft.item.ToolItem
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import stackabletoolskotlin.CustomLogger
import stackabletoolskotlin.StackableToolsKotlinUtils
import stackabletoolskotlin.config.ConfigManager

@Mixin(PlayerInventory::class)
abstract class InventoryMixin {

    @Shadow
    abstract fun getStack(slot: Int): ItemStack

    @Shadow
    abstract fun setStack(slot: Int, stack: ItemStack)

    @Inject(method = ["insertStack", "(Lnet/minecraft/item/ItemStack;)Z"], at = [At("HEAD")], cancellable = true)
    private fun onInsertStack(stack: ItemStack, cir: CallbackInfoReturnable<Boolean>) {
        if (stack.isEmpty) return
        if (!StackableToolsKotlinUtils.isToolOrManuallyRegistered(stack)) return

        val inv = this as PlayerInventory
        val cfg = ConfigManager.getConfig()
        val maxStackSize = when {
            stack.item is ToolItem -> cfg.maxToolStackSize
            stack.item is PotionItem -> cfg.maxPotionStackSize
            else -> cfg.maxStackSize
        }.toInt()

        var remaining = stack.count
        val originalCount = stack.count
        val itemName = stack.name.string

        CustomLogger.info("Tentative d'insertion : $itemName x$originalCount (Durabilité: ${stack.damage})")

        // 1) Fusionner avec les piles existantes compatibles (même durabilité / même NBT)
        for (slot in 0 until inv.main.size) {
            if (remaining <= 0) break
            val existing = getStack(slot)
            if (existing.isEmpty) continue
            
            if (StackableToolsKotlinUtils.canStackSameDurability(existing, stack)) {
                val freeSpace = (maxStackSize - existing.count).coerceAtLeast(0)
                if (freeSpace > 0) {
                    val toMove = minOf(remaining, freeSpace)
                    existing.count += toMove
                    remaining -= toMove
                    CustomLogger.info("Fusion dans slot $slot : +$toMove (Nouvelle taille: ${existing.count})")
                }
            }
        }

        // 2) Placer dans des emplacements vides si nécessaire
        if (remaining > 0) {
            for (slot in 0 until inv.main.size) {
                if (remaining <= 0) break
                val existing = getStack(slot)
                if (existing.isEmpty) {
                    val toPut = stack.copy()
                    val countToPut = minOf(remaining, maxStackSize)
                    toPut.count = countToPut
                    setStack(slot, toPut)
                    remaining -= countToPut
                    CustomLogger.info("Placement slot vide $slot : $countToPut items")
                }
            }
        }

        if (remaining < originalCount) {
            CustomLogger.info("Insertion réussie : ${originalCount - remaining} insérés, $remaining restants")
        }

        stack.count = remaining
        cir.returnValue = remaining < originalCount
        cir.cancel()
    }

    @Inject(method = ["setStack", "(ILnet/minecraft/item/ItemStack;)V"], at = [At("RETURN")])
    private fun onSetStack(slot: Int, stack: ItemStack, cir: CallbackInfo) {
        if (stack.isEmpty) return

        val itemName = stack.name.string
        val count = stack.count
        val isStackable = StackableToolsKotlinUtils.isToolOrManuallyRegistered(stack)

        CustomLogger.info("Inventaire : slot $slot mis à jour -> $itemName x$count (outil/manuel=$isStackable)")
    }
}
