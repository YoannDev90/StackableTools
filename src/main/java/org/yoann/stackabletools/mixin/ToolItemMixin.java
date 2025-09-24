package org.yoann.stackabletools.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToolItem.class)
public abstract class ToolItemMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void afterInit(ToolMaterial material, Item.Settings settings, CallbackInfo ci) {
        System.out.println("[StackableTools] afterInit called for " + this);
        ((ItemAccessor) (Object) this).setMaxCount(8);
    }

}
