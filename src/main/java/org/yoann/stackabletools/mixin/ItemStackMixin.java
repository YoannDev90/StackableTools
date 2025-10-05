package org.yoann.stackabletools.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yoann.stackabletools.PlayerContext;
import org.yoann.stackabletools.StackableToolsMod;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract boolean isDamageable();
    @Shadow public abstract int getCount();
    @Shadow public abstract void setCount(int count);
    @Shadow public abstract int getDamage();
    @Shadow public abstract void setDamage(int damage);
    @Shadow public abstract int getMaxDamage();
    @Shadow public abstract ItemStack copy();

    @Unique
    private boolean stackabletools$isProcessingDamage = false;

    @Unique
    private PlayerEntity stackabletools$findPlayerInStackTrace() {
        // Traverse the stack trace to find a player
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            // Look for player-related classes
            if (className.contains("PlayerEntity") || className.contains("ServerPlayerEntity")) {

            }
        }

        // Try to retrieve from PlayerContext first
        PlayerEntity player = PlayerContext.get();
        if (player != null) {
            return player;
        }

        return null;
    }

    @Inject(method = "setDamage", at = @At("HEAD"), cancellable = true)
    private void onSetDamage(int damage, CallbackInfo ci) {
        // Avoid infinite loops
        if (stackabletools$isProcessingDamage) {
            return;
        }

        // If it's a damageable tool and we're trying to apply damage (damage > 0)
        if (this.isDamageable() && damage > 0) {
            int currentDamage = this.getDamage();

            // If the tool is in a stack (count > 1) and it was intact (currentDamage == 0)
            if (this.getCount() > 1 && currentDamage == 0) {
                ci.cancel();
                stackabletools$isProcessingDamage = true;

                try {
                    int stackSize = this.getCount();

                    // Create a copy of the stack for intact tools
                    ItemStack intactTools = this.copy();
                    intactTools.setCount(stackSize - 1);
                    intactTools.setDamage(0);

                    // The current tool becomes a single damaged tool
                    this.setCount(1);
                    this.setDamage(damage);

                    // Find the player
                    PlayerEntity player = stackabletools$findPlayerInStackTrace();
                    if (player != null) {
                        if (StackableToolsMod.getConfig().isLoggingEnabled()) {
                            StackableToolsMod.LOGGER.debug("Splitting tool stack: 1 damaged, {} intact returned to player",
                                intactTools.getCount());
                        }
                        PlayerContext.giveOrDropIntactTools(player, intactTools);
                    } else {
                        StackableToolsMod.LOGGER.error("Unable to find player when splitting tool stack!");
                        StackableToolsMod.LOGGER.error("The {} intact tools will be temporarily lost.", intactTools.getCount());
                        if (StackableToolsMod.getConfig().isLoggingEnabled()) {
                            StackableToolsMod.LOGGER.debug("Stack trace:");
                            for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
                                StackableToolsMod.LOGGER.debug("  {}", element.toString());
                            }
                        }
                    }
                } finally {
                    stackabletools$isProcessingDamage = false;
                    PlayerContext.clear();
                }
            }
            // If the tool is alone (count == 1) or already damaged, check if it will break
            else if (this.getCount() == 1 && damage >= this.getMaxDamage()) {
                // Normal behavior: the tool breaks
                if (StackableToolsMod.getConfig().isLoggingEnabled()) {
                    StackableToolsMod.LOGGER.debug("Tool breaking with damage {} / {}", damage, this.getMaxDamage());
                }
            }
        }
    }
}
