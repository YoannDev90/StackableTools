package stackabletools.mixin

import net.minecraft.item.ElytraItem
import net.minecraft.item.Item
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import stackabletools.config.ConfigManager

@Mixin(Item::class)
abstract class ElytraItemMixin {

    @Inject(method = ["getMaxCount"], at = [At("HEAD")], cancellable = true)
    private fun onGetMaxCount(cir: CallbackInfoReturnable<Int>) {
        val item = this as Any as Item
        if (item is ElytraItem) {
            val config = try { ConfigManager.getConfig() } catch (e: Exception) { null }
            if (config != null && config.stacking.enable) {
                cir.returnValue = config.stacking.maxElytraStackSize.toInt().coerceAtLeast(1)
            }
        }
    }
}
