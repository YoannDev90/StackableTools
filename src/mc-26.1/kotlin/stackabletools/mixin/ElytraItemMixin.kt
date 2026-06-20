package stackabletools.mixin

import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import stackabletools.config.ConfigManager

/**
 * Mixin that allows Elytras to be stacked in the inventory.
 */
@Mixin(Item::class)
abstract class ElytraItemMixin {

    @Inject(method = ["getDefaultMaxStackSize"], at = [At("HEAD")], cancellable = true)
    private fun onGetDefaultMaxStackSize(cir: CallbackInfoReturnable<Int>) {
        val item = this as Any as Item
        if (item === Items.ELYTRA) {
            val config = try { ConfigManager.getConfig() } catch (e: Exception) { null }
            if (config != null && config.stacking.enable) {
                cir.returnValue = config.stacking.maxElytraStackSize.toInt().coerceAtLeast(1)
            }
        }
    }
}
