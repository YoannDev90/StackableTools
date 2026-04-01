package stackabletoolskotlin.mixin

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import stackabletoolskotlin.CustomLogger
import stackabletoolskotlin.StackableToolsKotlinUtils

@Mixin(ItemStack::class)
abstract class ItemStackMixin {

    @Inject(method = ["damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"], at = [At("HEAD")])
    private fun onDamage(amount: Int, entity: LivingEntity, breakCallback: java.util.function.Consumer<LivingEntity>, ci: CallbackInfo) {
        val stack = this as Any as ItemStack
        
        // Si c'est un joueur et que l'objet est stacké (plus de 1) et que c'est un outil configurable
        if (entity is PlayerEntity && stack.count > 1 && StackableToolsKotlinUtils.isToolOrManuallyRegistered(stack)) {
            val player = entity
            
            // Créer le stack restant (le reste de la pile qui ne prend pas de dégâts)
            val leftover = stack.copy()
            leftover.count = stack.count - 1
            
            // L'item en cours d'utilisation devient unitaire pour isoler ses dégâts
            stack.count = 1
            
            CustomLogger.info("Déstackage automatique : 1 outil conservé pour usage, ${leftover.count} renvoyés dans l'inventaire.")

            // Tenter de replacer le reste dans l'inventaire (ou drop si plein)
            if (!player.inventory.insertStack(leftover)) {
                player.dropItem(leftover, false, true)
            }
        }
    }
}
