package stackabletools.mixin

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import stackabletools.CustomLogger
import stackabletools.StackableToolsUtils

@Mixin(ItemStack::class, priority = 900)
abstract class ItemStackMixin {

    @Unique
    private var isProcessingDamageInternal = false

    @Inject(method = ["damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"], at = [At("HEAD")])
    private fun onDamage(amount: Int, entity: LivingEntity, breakCallback: java.util.function.Consumer<LivingEntity>, ci: CallbackInfo) {
        if (isProcessingDamageInternal) return
        
        val stack = this as Any as ItemStack
        
        // Si c'est un joueur et que l'objet est stacké (plus de 1) et que c'est un outil configurable
        if (entity is PlayerEntity && !entity.getWorld().isClient && stack.count > 1 && StackableToolsUtils.isStackableItem(stack)) {
            val player = entity
            
            // On ne sépare que si l'outil est encore NEUF (pour éviter les boucles si déjà abîmé)
            // IMPORTANCE : On vérifie stack.damage == 0 AVANT que Minecraft n'applique quoi que ce soit
            if (stack.damage == 0 && amount > 0) { // On s'assure que c'est bien des DÉGÂTS (amount > 0)
                isProcessingDamageInternal = true
                try {
                    val countBefore = stack.count
                    
                    // 1. On diminue le stack AVANT de copier pour isoler l'item utilisé
                    stack.count = 1
                    
                    // 2. On crée le reste qui est GARANTI neuf
                    val leftover = stack.copy()
                    leftover.count = countBefore - 1
                    leftover.damage = 0
                    
                    CustomLogger.info("Séparation forcée : 1 outil utilisé, ${leftover.count} outils neufs protégés.")

                    // 3. On rend les outils neufs au joueur. 
                    var emptySlot = -1
                    for (i in 0 until 36) {
                        if (player.inventory.getStack(i).isEmpty) {
                            emptySlot = i
                            break
                        }
                    }

                    if (emptySlot != -1) {
                        player.inventory.setStack(emptySlot, leftover)
                    } else {
                        player.dropItem(leftover, false)
                    }
                } finally {
                    isProcessingDamageInternal = false
                }
            }
        }
    }
}
