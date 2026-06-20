package stackabletools.mixin

import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
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
    private val isProcessingDamageInternal = ThreadLocal.withInitial { false }

    @Inject(method = ["damage(ILnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;)V"], at = [At("HEAD")])
    private fun onDamage(amount: Int, entity: LivingEntity, slot: EquipmentSlot, originalStack: ItemStack, ci: CallbackInfo) {
        if (isProcessingDamageInternal.get()) return

        val stack = this as Any as ItemStack

        if (entity is ServerPlayerEntity && stack.count > 1 && StackableToolsUtils.isStackableItem(stack)) {
            val player = entity

            if (stack.damage == 0 && amount > 0) {
                isProcessingDamageInternal.set(true)
                try {
                    val countBefore = stack.count

                    stack.count = 1
                    stack.damage = 1

                    val leftover = stack.copy()
                    leftover.count = countBefore - 1
                    leftover.damage = 0

                    CustomLogger.info("Forced separation: 1 tool used, ${leftover.count} fresh tools protected.")

                    if (!player.inventory.insertStack(leftover)) {
                        player.dropItem(leftover, false)
                    }

                    stack.damage = 0
                } finally {
                    isProcessingDamageInternal.set(false)
                }
            }
        }
    }
}
