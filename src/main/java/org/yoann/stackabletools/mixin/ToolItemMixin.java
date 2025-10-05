package org.yoann.stackabletools.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yoann.stackabletools.config.StackableToolsConfig;
import org.yoann.stackabletools.StackableToolsMod;

@Mixin(ToolItem.class)
public abstract class ToolItemMixin {
    /**
     * Sets the max stack count for all tools after their initialization.
     * This is called during the tool's constructor.
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void afterInit(ToolMaterial material, Item.Settings settings, CallbackInfo ci) {
        // Use getInstance() instead of getConfig() to avoid NPE during early initialization
        int maxStackSize = StackableToolsConfig.getInstance().getMaxStackSize();
        ((ItemAccessor) this).setMaxCount(maxStackSize);

        if (StackableToolsConfig.getInstance().isLoggingEnabled()) {
            StackableToolsMod.LOGGER.debug("Set max stack count to {} for tool: {}", maxStackSize, this.getClass().getSimpleName());
        }
    }
}
