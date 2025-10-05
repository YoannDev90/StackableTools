package org.yoann.stackabletools.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.yoann.stackabletools.PlayerContext;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Shadow
    @Final
    protected ServerPlayerEntity player;

    /**
     * Captures the player entity before processing block breaking actions.
     * This ensures the PlayerContext is set for block mining scenarios.
     */
    @Inject(method = "processBlockBreakingAction", at = @At("HEAD"))
    private void beforeProcessBlockBreakingAction(BlockPos pos, net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, int sequence, CallbackInfo ci) {
        PlayerContext.set(player);
    }
    
    /**
     * Captures the player entity before item interaction.
     * This ensures the PlayerContext is set for item usage scenarios.
     */
    @Inject(method = "interactItem", at = @At("HEAD"))
    private void beforeInteractItem(ServerPlayerEntity player2, net.minecraft.world.World world, net.minecraft.item.ItemStack stack, net.minecraft.util.Hand hand, CallbackInfoReturnable<?> cir) {
        PlayerContext.set(player);
    }
}
