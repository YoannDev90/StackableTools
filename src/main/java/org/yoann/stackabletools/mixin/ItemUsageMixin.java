package org.yoann.stackabletools.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.yoann.stackabletools.PlayerContext;

@Mixin(Item.class)
public class ItemUsageMixin {
    
    /**
     * Captures the player entity before mining a block.
     * This allows us to track which player is using the tool when damage is applied.
     */
    @Inject(method = "postMine", at = @At("HEAD"))
    private void beforePostMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner, CallbackInfoReturnable<Boolean> cir) {
        if (miner instanceof PlayerEntity) {
            PlayerContext.set((PlayerEntity) miner);
        }
    }
    
    /**
     * Captures the player entity before hitting an entity.
     * This allows us to track which player is using the tool when damage is applied.
     */
    @Inject(method = "postHit", at = @At("HEAD"))
    private void beforePostHit(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> cir) {
        if (attacker instanceof PlayerEntity) {
            PlayerContext.set((PlayerEntity) attacker);
        }
    }
}
