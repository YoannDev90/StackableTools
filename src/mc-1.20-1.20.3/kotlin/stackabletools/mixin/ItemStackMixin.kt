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
    private var processingDamage = false

    @Inject(method = ["damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"], at = [At("HEAD")])
    private fun onDamage(amount: Int, entity: LivingEntity, breakCallback: java.util.function.Consumer<LivingEntity>, ci: CallbackInfo) {
        if (processingDamage) return
        val stack = this as Any as ItemStack
        if (entity !is PlayerEntity || entity.getWorld().isClient || stack.count <= 1 || !StackableToolsUtils.isStackableItem(stack)) return
        if (stack.damage != 0 || amount <= 0) return

        processingDamage = true
        try {
            stack.damage = 1

            val leftover = stack.copy()
            leftover.count = stack.count - 1
            leftover.damage = 0

            stack.count = 1

            CustomLogger.info("Forced separation: 1 tool used, ${leftover.count} fresh tools protected.")
            if (!entity.inventory.insertStack(leftover)) {
                entity.dropItem(leftover, false)
            }

            stack.damage = 0
        } finally {
            processingDamage = false
        }
    }
}
