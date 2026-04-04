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

/**
 * Mixin aimed at protecting tool groups from simultaneous durability loss.
 * When a tool that is stacked (count > 1) is used, we isolate a single unit
 * so that only one tool takes damage while the rest of the stack remains untouched and intact.
 */
@Mixin(ItemStack::class, priority = 900)
abstract class ItemStackMixin {

    /**
     * Re-entry guard to prevent infinite recursion during stack separation.
     * Uses ThreadLocal for thread safety in multi-threaded Minecraft environments.
     */
    @Unique
    private val isProcessingDamageInternal = ThreadLocal.withInitial { false }

    /**
     * Injected into the damage function of ItemStack.
     * Intercepts damage events before they are applied.
     */
    @Inject(method = ["damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"], at = [At("HEAD")])
    private fun onDamage(amount: Int, entity: LivingEntity, breakCallback: java.util.function.Consumer<LivingEntity>, ci: CallbackInfo) {
        // Prevent recursive triggers if we modify the stack within this function
        if (isProcessingDamageInternal.get()) return
        
        val stack = this as Any as ItemStack
        
        // Only process for players on the server where tools are stacked
        if (entity is PlayerEntity && !entity.getWorld().isClient && stack.count > 1 && StackableToolsUtils.isStackableItem(stack)) {
            val player = entity
            
            // Separation logic: isolate the used unit so durability isn't shared by the whole stack
            // We only trigger this for new tools (damage == 0) to ensure predictable behavior
            if (stack.damage == 0 && amount > 0) { 
                isProcessingDamageInternal.set(true)
                try {
                    val countBefore = stack.count
                    
                    // 1. Reduce the current stack to 1 (representing the active tool being damaged)
                    stack.count = 1
                    
                    // 2. Create a new stack for the remaining healthy tools
                    val leftover = stack.copy()
                    leftover.count = countBefore - 1
                    leftover.damage = 0
                    
                    CustomLogger.info("Forced separation: 1 tool used, ${leftover.count} fresh tools protected.")

                    // 3. Return the healthy tools to the player's inventory or drop them if full
                    if (!player.inventory.insertStack(leftover)) {
                        player.dropItem(leftover, false)
                    }
                } finally {
                    isProcessingDamageInternal.set(false)
                }
            }
        }
    }
}
