package stackabletools.mixin

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.item.PotionItem
import net.minecraft.item.ToolItem
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import stackabletools.CustomLogger
import stackabletools.StackableToolsUtils
import stackabletools.config.ConfigManager

@Mixin(PlayerInventory::class)
abstract class InventoryMixin {

    @Shadow
    abstract fun getStack(slot: Int): ItemStack

    @Shadow
    abstract fun setStack(slot: Int, stack: ItemStack)

    @Shadow
    abstract fun size(): Int

    @Inject(method = ["insertStack", "(Lnet/minecraft/item/ItemStack;)Z"], at = [At("HEAD")], cancellable = true)
    private fun onInsertStack(stack: ItemStack, cir: CallbackInfoReturnable<Boolean>) {
        val inv = this as Any as PlayerInventory
        // On vérifie seulement si c'est un Tool/Potion configurable (ceux dont la config permet de stacker)
        if (stack.isEmpty || inv.player.getWorld().isClient) return
        
        // Si c'est un item normal (non-stackable par le mod), on laisse Minecraft faire
        if (!StackableToolsUtils.isToolOrManuallyRegistered(stack)) return

        // REGLE ESSENTIELLE : Si l'objet est déjà abîmé, on refuse catégoriquement l'insertion gérée par le mod
        // pour que Minecraft le considère comme non-stackable (1 item par slot).
        if (stack.isDamaged && (stack.item is ToolItem || stack.item is ArmorItem)) {
            return
        }

        // Si l'objet est NEUF, on applique notre logique de stack custom
        val cfg = ConfigManager.getConfig()
        val maxStackSize = when {
            stack.item is ToolItem -> cfg.maxToolStackSize
            stack.item is PotionItem -> cfg.maxPotionStackSize
            else -> cfg.maxStackSize
        }.toInt()

        var remaining = stack.count
        val originalCount = stack.count
        val itemName = stack.name.string

        CustomLogger.info("Gestion Custom Insert : $itemName x$originalCount (Durabilité: ${stack.damage})")

        // 1) Fusionner avec les piles existantes compatibles (MÊME DURABILITÉ 0)
        for (slot in 0 until 36) { 
            if (remaining <= 0) break
            val existing = getStack(slot)
            if (existing.isEmpty) continue
            
            if (StackableToolsUtils.canStackSameDurability(existing, stack)) {
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
            for (slot in 0 until 36) {
                if (remaining <= 0) break
                val existing = getStack(slot)
                if (existing.isEmpty || existing.item.name.string == "Air") {
                    val countToPut = minOf(remaining, maxStackSize)
                    val toPut = stack.copy()
                    toPut.count = countToPut
                    
                    setStack(slot, toPut)
                    remaining -= countToPut
                    CustomLogger.info("Placement slot vide $slot : $countToPut items (Restant: $remaining)")
                }
            }
        }

        stack.count = remaining
        cir.returnValue = remaining < originalCount
        cir.cancel()
    }

    @Inject(method = ["setStack", "(ILnet/minecraft/item/ItemStack;)V"], at = [At("RETURN")])
    private fun onSetStack(slot: Int, stack: ItemStack, cir: CallbackInfo) {
        val inv = this as Any as PlayerInventory
        // Désactivé car trop verbeux et redondant avec insertStack
        // if (stack.isEmpty || inv.player.getWorld().isClient) return
        return
    }
}
