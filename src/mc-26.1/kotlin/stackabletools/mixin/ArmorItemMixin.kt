package stackabletools.mixin

import net.minecraft.world.item.Item
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import stackabletools.config.ConfigManager
import net.minecraft.core.component.DataComponents

@Mixin(Item::class)
abstract class ArmorItemMixin {

    @Inject(method = ["getDefaultMaxStackSize"], at = [At("HEAD")], cancellable = true)
    private fun onGetDefaultMaxStackSize(cir: CallbackInfoReturnable<Int>) {
        val item = this as Any as Item
        if (item.components().get(DataComponents.EQUIPPABLE) != null) {
            val config = try { ConfigManager.getConfig() } catch (e: Exception) { null }
            if (config != null && config.stacking.enable) {
                cir.returnValue = config.stacking.maxArmorPieceStackSize.toInt().coerceAtLeast(1)
            }
        }
    }
}
