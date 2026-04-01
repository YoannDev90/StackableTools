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

@Mixin(ItemStack::class, priority = 900)
abstract class ItemStackMixin {

    @Inject(method = ["damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"], at = [At("HEAD")], cancellable = true)
    private fun onDamage(amount: Int, entity: LivingEntity, breakCallback: java.util.function.Consumer<LivingEntity>, ci: CallbackInfo) {
        val stack = this as Any as ItemStack
        
        // Si c'est un joueur et que l'objet est stacké (plus de 1) et que c'est un outil configurable
        if (entity is PlayerEntity && !entity.getWorld().isClient && stack.count > 1 && StackableToolsKotlinUtils.isToolOrManuallyRegistered(stack)) {
            val player = entity
            
            // On sauvegarde le count d'origine
            val countBefore = stack.count
            
            // ÉTAPE 1 : On réduit le stack actuel à 1 IMMEDIATEMENT pour que les dégâts ne s'appliquent qu'à cet item
            stack.count = 1
            
            // ÉTAPE 2 : Créer le stack restant (les outils NEUFS qui ne prennent PAS de dégâts)
            val leftover = stack.copy()
            leftover.count = countBefore - 1
            
            // On s'assure que le leftover reste NEUF (0 dégâts) avant les dégâts du premier
            leftover.damage = 0
            
            CustomLogger.info("Déstackage préventif : 1 outil prend les dégâts, ${leftover.count} outils neufs protégés dans l'inventaire.")

            // ÉTAPE 3 : Tenter de replacer les outils neufs ailleurs dans l'inventaire
            if (!player.inventory.insertStack(leftover)) {
                player.dropItem(leftover, false, true)
            }
            
            // On laisse l'exécution continuer : les dégâts s'appliqueront uniquement sur 'stack' qui a maintenant un count de 1.
        }
    }
}
