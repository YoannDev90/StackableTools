package stackabletools.mixin

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
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

    @Inject(method = ["hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V"], at = [At("HEAD")])
    private fun onDamage(amount: Int, entity: LivingEntity, slot: EquipmentSlot, ci: CallbackInfo) {
        if (isProcessingDamageInternal.get()) return

        val stack = this as Any as ItemStack

        if (entity is ServerPlayer && stack.count > 1 && StackableToolsUtils.isStackableItem(stack)) {
            val player = entity

            if (stack.damageValue == 0 && amount > 0) {
                isProcessingDamageInternal.set(true)
                try {
                    val countBefore = stack.count

                    stack.count = 1
                    stack.damageValue = 1

                    val leftover = stack.copy()
                    leftover.count = countBefore - 1
                    leftover.damageValue = 0

                    CustomLogger.info("Forced separation: 1 tool used, ${leftover.count} fresh tools protected.")

                    if (!player.inventory.add(leftover)) {
                        player.drop(leftover, false)
                    }

                    stack.damageValue = 0
                } finally {
                    isProcessingDamageInternal.set(false)
                }
            }
        }
    }
}
