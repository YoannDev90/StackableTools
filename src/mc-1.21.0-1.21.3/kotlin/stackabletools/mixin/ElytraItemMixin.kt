package stackabletools.mixin

import net.minecraft.item.Item
import net.minecraft.item.Items
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

    /**
     * Overrides the maximum stack size for any Item if it's an instance of ElytraItem.
     */
    @Inject(method = ["getMaxCount"], at = [At("HEAD")], cancellable = true)
    private fun onGetMaxCount(cir: CallbackInfoReturnable<Int>) {
        val item = this as Any as Item
        if (item === Items.ELYTRA) {
            val config = try { ConfigManager.getConfig() } catch (e: Exception) { null }
            if (config != null && config.stacking.enable) {
                cir.returnValue = config.stacking.maxElytraStackSize.toInt().coerceAtLeast(1)
            }
        }
    }
}
