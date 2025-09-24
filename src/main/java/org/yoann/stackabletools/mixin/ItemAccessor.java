package org.yoann.stackabletools.mixin;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.Mutable;

@Mixin(Item.class)
public interface ItemAccessor {
    @Accessor("maxCount")
    @Mutable
    void setMaxCount(int maxCount);
}
